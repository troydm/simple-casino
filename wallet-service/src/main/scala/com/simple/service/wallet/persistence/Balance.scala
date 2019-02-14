package com.simple.service.wallet.persistence

import javax.persistence.{NamedQuery, Basic, Entity, Id}
import com.simple.data.game.ServiceData._
import com.simple.data.wallet.ServiceData._

@Entity
class Balance {
  @Id
  var id: PlayerId = 0

  var amount: Amount = 0.0
}
