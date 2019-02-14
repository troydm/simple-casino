package com.simple.data.wallet

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._

object ServiceData {
  import com.simple.data.game.ServiceData.PlayerId
  type Amount = Double

  final case class Balance(amount: Amount)

  abstract class RegisterIn
  abstract class RegisterOut
  final case class RegisterRequest(playerId: PlayerId) extends RegisterIn
  final case class RegisterOK(playerId: PlayerId, balance: Balance) extends RegisterOut
  final case class RegisterKO(playerId: PlayerId, reason: String) extends RegisterOut

  abstract class DepositIn
  abstract class DepositOut
  final case class DepositRequest(playerId: PlayerId, amount: Amount) extends DepositIn
  final case class DepositOK(playerId: PlayerId, balance: Balance) extends DepositOut
  final case class DepositKO(playerId: PlayerId, reason: String) extends DepositOut

  abstract class WithdrawIn
  abstract class WithdrawOut
  final case class WithdrawRequest(playerId: PlayerId, amount: Amount) extends WithdrawIn
  final case class WithdrawOK(playerId: PlayerId, balance: Balance) extends WithdrawOut
  final case class WithdrawKO(playerId: PlayerId, reason: String, balance: Option[Balance]) extends WithdrawOut

  abstract class BalanceIn
  abstract class BalanceOut
  final case class BalanceRequest(playerId: PlayerId) extends BalanceIn
  final case class BalanceOK(playerId: PlayerId, balance: Balance) extends BalanceOut
  final case class BalanceKO(playerId: PlayerId, reason: String) extends BalanceOut

  trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
    implicit val balanceFormat = jsonFormat1(Balance)

    implicit val registerRequestFormat = jsonFormat1(RegisterRequest)
    implicit val registerOKFormat = jsonFormat2(RegisterOK)
    implicit val registerKOFormat = jsonFormat2(RegisterKO)

    implicit val depositRequestFormat = jsonFormat2(DepositRequest)
    implicit val depositOKFormat = jsonFormat2(DepositOK)
    implicit val depositKOFormat = jsonFormat2(DepositKO)

    implicit val withdrawRequestFormat = jsonFormat2(WithdrawRequest)
    implicit val withdrawOKFormat = jsonFormat2(WithdrawOK)
    implicit val withdrawKOFormat = jsonFormat3(WithdrawKO)

    implicit val balanceRequestFormat = jsonFormat1(BalanceRequest)
    implicit val balanceOKFormat = jsonFormat2(BalanceOK)
    implicit val balanceKOFormat = jsonFormat2(BalanceKO)
  }
}

