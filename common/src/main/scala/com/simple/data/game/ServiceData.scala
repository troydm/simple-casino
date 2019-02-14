package com.simple.data.game

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._

object ServiceData {
  type PlayerId = Int
  type GameId = Int
  import com.simple.data.wallet.ServiceData.{Amount, Balance}

  final case class Bet(gameId: GameId, amount: Amount)

  abstract class PlaceBetIn
  abstract class PlaceBetOut
  final case class PlaceBetRequest(playerId: PlayerId, gameId: GameId, amount: Amount) extends PlaceBetIn
  final case class PlaceBetOK(playerId: PlayerId, balance: Balance) extends PlaceBetOut
  final case class PlaceBetKO(playerId: PlayerId, reason: String, balance: Option[Balance]) extends PlaceBetOut

  abstract class ShowBetsIn
  abstract class ShowBetsOut
  final case class ShowBetsRequest(playerId: PlayerId) extends ShowBetsIn
  final case class ShowBetsOK(playerId: PlayerId, bets: List[Bet]) extends ShowBetsOut
  final case class ShowBetsKO(playerId: PlayerId, reason: String) extends ShowBetsOut

  trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
    implicit val balanceFormat = jsonFormat1(Balance)
    implicit val betFormat = jsonFormat2(Bet)

    implicit val placeBetRequestFormat = jsonFormat3(PlaceBetRequest)
    implicit val placeBetOKFormat = jsonFormat2(PlaceBetOK)
    implicit val placeBetKOFormat = jsonFormat3(PlaceBetKO)

    implicit val showBetsRequestFormat = jsonFormat1(ShowBetsRequest)
    implicit val showBetsOKFormat = jsonFormat2(ShowBetsOK)
    implicit val showBetsKOFormat = jsonFormat2(ShowBetsKO)
  }
}
