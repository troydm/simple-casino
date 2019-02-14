package com.simple.service.game

import org.scalatest._
import org.scalatest.junit.JUnitRunner
import org.scalatest.easymock.EasyMockSugar
import com.simple.data.game.ServiceData._
import com.simple.data.wallet.ServiceData._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.server.Directives._
import org.junit.runner.RunWith

@RunWith(classOf[JUnitRunner])
class GameServiceSpec extends FlatSpec with Matchers with EasyMockSugar 
  with ScalatestRouteTest with com.simple.data.game.ServiceData.JsonSupport {

  val walletService = mock[WalletServiceClient]
  expecting {
    walletService.withdraw(WithdrawRequest(4,20)).andReturn(WithdrawOK(4,Balance(20)))
    lastCall.times(1)
    walletService.withdraw(WithdrawRequest(4,30)).andReturn(WithdrawKO(4,"insufficient funds",Some(Balance(20))))
    lastCall.times(1)
  }
  val gameService = new GameService(walletService)

  "A GameService" should "allow players to place bets" in {
    whenExecuting(walletService) {
      Post("/place-bet", PlaceBetRequest(4,1,20)) ~> gameService.routes ~> check {
        responseAs[PlaceBetOK] should be(PlaceBetOK(4,Balance(20)))
      }
      Post("/place-bet", PlaceBetRequest(4,1,30)) ~> gameService.routes ~> check {
        responseAs[PlaceBetKO] should be(PlaceBetKO(4,"insufficient funds",Some(Balance(20))))
      }
    }
  }

  "A GameService" should "allow players show bets they've placed" in {
    Post("/show-bets", ShowBetsRequest(4)) ~> gameService.routes ~> check {
      responseAs[ShowBetsOK] should be(ShowBetsOK(4,List(Bet(1,20))))
    }
    Post("/show-bets", ShowBetsRequest(3)) ~> gameService.routes ~> check {
      responseAs[ShowBetsKO] should be(ShowBetsKO(3,"playerId not found or player has no bets"))
    }
  }
}
