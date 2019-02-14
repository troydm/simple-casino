package com.simple.service.wallet.persistence

import org.scala_libs.jpa._
import com.simple.data.game.ServiceData.PlayerId
import com.simple.data.wallet.ServiceData.Amount

class BalanceManager(url: String) {
  object ThreadEM extends LocalEMF("test",false,Some(Map("javax.persistence.jdbc.url" -> url))) with ThreadLocalEM

  private def get(playerId: PlayerId): Option[Balance] = ThreadEM.find[Balance](classOf[Balance],playerId)

  def getBalance(playerId: PlayerId): Option[Amount] = {
    val a = get(playerId).map(_.amount)
    ThreadEM.cleanup
    a
  }

  def register(playerId: PlayerId): Boolean = {
    get(playerId) match {
      case Some(_) => {
        ThreadEM.cleanup
        false
      }
      case None => {
        val b = new Balance
        b.id = playerId
        b.amount = 0
        ThreadEM.persistAndFlush(b)
        ThreadEM.cleanup
        true
      }
    }
  }

  def deposit(playerId: PlayerId, amount: Amount): Option[Amount] = {
    get(playerId) match {
      case Some(b) => {
        if(amount > 0){
          b.amount += amount
          ThreadEM.persistAndFlush(b)
          ThreadEM.cleanup
          Some(b.amount)
        }else{
          ThreadEM.cleanup
          None
        }
      }
      case None => {
        ThreadEM.cleanup
        None
      }
    }
  }

  def withdraw(playerId: PlayerId, amount: Amount): (Boolean,Option[Amount]) = {
    get(playerId) match {
      case Some(b) => {
        val a = if(amount < 0 || b.amount >= amount){
          b.amount -= amount
          ThreadEM.persistAndFlush(b)
          (true,Some(b.amount))
        }else{
          (false,Some(b.amount))
        }
        ThreadEM.cleanup
        a
      }
      case None => {
        ThreadEM.cleanup
        (false,None)
      }
    }
  }
}
