package com.simple.service.wallet

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import com.simple.data.wallet.ServiceData._
import com.simple.service.wallet.persistence.BalanceManager

class WalletService(balanceManager: BalanceManager) extends JsonSupport {
  val routes = {
    logRequestResult("wallet-service",Logging.InfoLevel) {
      path("register") {
        post {
          entity(as[RegisterRequest]) { request =>
            complete {
              if(balanceManager.register(request.playerId))
                RegisterOK(request.playerId,Balance(0))
              else
                RegisterKO(request.playerId,"playerId already registered")
            }
          }
        }
      } ~
      path("deposit") {
        post {
          entity(as[DepositRequest]) { request =>
            complete {
              balanceManager.deposit(request.playerId,request.amount) match {
                case Some(amount) => DepositOK(request.playerId,Balance(amount))
                case None => DepositKO(request.playerId,"playerId not found")
              }
            }
          }
        }
      } ~
      path("withdraw") {
        post {
          entity(as[WithdrawRequest]) { request =>
            complete {
              balanceManager.withdraw(request.playerId,request.amount) match {
                case (true,Some(amount)) => WithdrawOK(request.playerId,Balance(amount))
                case (false,Some(amount)) => WithdrawKO(request.playerId,"insufficient funds",Some(Balance(amount)))
                case _ => WithdrawKO(request.playerId,"playerId not found",None)
              }
            }
          }
        }
      } ~
      path("balance") {
        post {
          entity(as[BalanceRequest]) { request =>
            complete {
              balanceManager.getBalance(request.playerId) match {
                case Some(amount) => BalanceOK(request.playerId,Balance(amount))
                case None => BalanceKO(request.playerId,"playerId not found")
              }
            }
          }
        }
      }

    }
  }
}

object WalletService extends App {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executor = system.dispatcher
  val config = ConfigFactory.load()
  val logger = Logging(system, getClass)
  val balanceManager = new BalanceManager(config.getString("jpa.url"))

  Http().bindAndHandle(new WalletService(balanceManager).routes,
    config.getString("http.interface"), config.getInt("http.port"))
}
