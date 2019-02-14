package com.simple.service.game

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.marshalling._
import akka.http.scaladsl.unmarshalling._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import com.simple.data.game.ServiceData._
import com.simple.data.wallet.ServiceData._

import scala.concurrent._
import scala.concurrent.duration._
import scala.util.{ Failure, Success }

class WalletServiceClient(url: String)(implicit val system: ActorSystem, val mat: ActorMaterializer)
  extends com.simple.data.wallet.ServiceData.JsonSupport {
  import scala.concurrent.ExecutionContext.Implicits.global

  def withdraw(request: WithdrawRequest): WithdrawOut = {
    val waitFor = 5 seconds
    val content = Await.result(for {
      requestEntity <- Marshal(request).to[RequestEntity]
      response <- Http().singleRequest(HttpRequest(method = HttpMethods.POST, uri = url+"/withdraw", entity = requestEntity))
      entity <- Unmarshal(response.entity).to[String]
    } yield entity, waitFor)
    Await.result(Unmarshal(content).to[WithdrawKO] recover {
      case e: Exception => Await.result(Unmarshal(content).to[WithdrawOK], waitFor)
    }, waitFor)
  }
}

class GameService(walletService: WalletServiceClient)
  extends com.simple.data.game.ServiceData.JsonSupport {
  val bets = new collection.mutable.HashMap[PlayerId,collection.mutable.ListBuffer[Bet]]
  private def playerBetsList(playerId: PlayerId): collection.mutable.ListBuffer[Bet] = {
    bets.synchronized {
      bets.get(playerId) match {
        case Some(l) => l
        case None => {
          val l = new collection.mutable.ListBuffer[Bet]
          bets(playerId) = l
          l
        }
      }
    }
  }
  private def placeBet(playerId: PlayerId, gameId: GameId, amount: Amount): Unit = {
    val l = playerBetsList(playerId)
    l.synchronized {
      l += new Bet(gameId, amount)
    }
  }
  private def playerBets(playerId: PlayerId): Option[List[Bet]] = {
    val l = playerBetsList(playerId)
    l.synchronized {
      if(l.size > 0) Some(List(l: _*)) else None
    }
  }
  val routes = {
    logRequestResult("game-service",Logging.InfoLevel) {
      path("place-bet") {
        post {
          entity(as[PlaceBetRequest]) { request =>
            complete {
              walletService.withdraw(new WithdrawRequest(request.playerId,request.amount)) match {
                case WithdrawOK(_, balance) => {
                  placeBet(request.playerId, request.gameId, request.amount)
                  PlaceBetOK(request.playerId, balance)
                }
                case WithdrawKO(_, reason, balance) => PlaceBetKO(request.playerId, reason, balance)
              }
            }
          }
        }
      } ~
      path("show-bets") {
        post {
          entity(as[ShowBetsRequest]) { request =>
            complete {
              playerBets(request.playerId) match {
                case Some(l) => ShowBetsOK(request.playerId, l)
                case None => ShowBetsKO(request.playerId, "playerId not found or player has no bets")
              }
            }
          }
        }
      }
    }
  }
}

object GameService extends App {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executor = system.dispatcher
  val config = ConfigFactory.load()
  val logger = Logging(system, getClass)
  val walletService = new WalletServiceClient(config.getString("wallet.url"))

  Http().bindAndHandle(new GameService(walletService).routes, config.getString("http.interface"), config.getInt("http.port"))
}
