package com.simple.service.wallet

import org.scalatest._
import org.scalatest.junit.JUnitRunner
import com.simple.data.wallet.ServiceData._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.server.Directives._
import com.simple.service.wallet.persistence.BalanceManager
import org.junit.runner.RunWith

@RunWith(classOf[JUnitRunner])
class WalletServiceSpec extends FlatSpec with Matchers with ScalatestRouteTest with JsonSupport {

  val balanceManager = new BalanceManager("jdbc:h2:mem:./test")
  val walletService = new WalletService(balanceManager)

  "A WalletService" should "register player wallet" in {
    Post("/register", RegisterRequest(4)) ~> walletService.routes ~> check {
      responseAs[RegisterOK] should be(RegisterOK(4,Balance(0)))
    }
  }

  "A WalletService" should "deposit money on player wallet" in {
    Post("/deposit", DepositRequest(4,20)) ~> walletService.routes ~> check {
      responseAs[DepositOK] should be(DepositOK(4,Balance(20)))
    }
    Post("/deposit", DepositRequest(3,20)) ~> walletService.routes ~> check {
      responseAs[DepositKO] shouldBe a[DepositKO]
    }
  }

  "A WalletService" should "withdraw money from player wallet" in {
    Post("/withdraw", WithdrawRequest(4,20)) ~> walletService.routes ~> check {
      responseAs[WithdrawOK] should be(WithdrawOK(4,Balance(0)))
    }
    Post("/withdraw", WithdrawRequest(4,20)) ~> walletService.routes ~> check {
      responseAs[WithdrawKO] should be(WithdrawKO(4,"insufficient funds",Some(Balance(0))))
    }
    Post("/withdraw", WithdrawRequest(3,20)) ~> walletService.routes ~> check {
      responseAs[WithdrawKO] should be(WithdrawKO(3,"playerId not found",None))
    }
  }

  "A WalletService" should "report player balance" in {
    Post("/balance", BalanceRequest(4)) ~> walletService.routes ~> check {
      responseAs[BalanceOK] should be(BalanceOK(4,Balance(0)))
    }
    Post("/balance", BalanceRequest(3)) ~> walletService.routes ~> check {
      responseAs[BalanceKO] should be(BalanceKO(3,"playerId not found"))
    }
  }
}
