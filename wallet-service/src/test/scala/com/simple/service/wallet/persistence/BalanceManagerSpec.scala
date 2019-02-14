package com.simple.service.wallet.persistence

import org.scalatest._
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith

@RunWith(classOf[JUnitRunner])
class BalanceManagerSpec extends FlatSpec with Matchers {

  val balanceManager = new BalanceManager("jdbc:h2:mem:./test")

  "A BalanceManager" should "register new balance and shouldn't register it twice" in {
    balanceManager.register(1) should be(true)
    balanceManager.getBalance(1) should be(Some(0))
    balanceManager.register(1) should be(false)
    balanceManager.register(2) should be(true)
    balanceManager.register(2) should be(false)
  }

  "A BalanceManager" should "deposit to balance" in {
    balanceManager.getBalance(1) should be(Some(0))
    balanceManager.deposit(1,20) should be(Some(20))
    balanceManager.getBalance(1) should be(Some(20))
    balanceManager.deposit(1,20) should be(Some(40))
    balanceManager.getBalance(1) should be(Some(40))
  }

  "A BalanceManager" should "withdraw from balance" in {
    balanceManager.getBalance(1) should be(Some(40))
    balanceManager.withdraw(1,20) should be((true,Some(20)))
    balanceManager.getBalance(1) should be(Some(20))
    balanceManager.withdraw(1,30) should be((false,Some(20)))
    balanceManager.getBalance(1) should be(Some(20))
    balanceManager.withdraw(1,20) should be((true,Some(0)))
    balanceManager.getBalance(1) should be(Some(0))
    balanceManager.withdraw(3,20) should be((false,None))
  }
}
