package com.simple.service.wallet.persistence

import org.eclipse.persistence.sessions.DatabaseLogin
import org.eclipse.persistence.sessions.Session
import org.eclipse.persistence.sessions.factories.SessionCustomizer

class MySessionCustomizer extends SessionCustomizer {
  override def customize(session: Session) = {
    val databaseLogin = session.getDatasourceLogin().asInstanceOf[DatabaseLogin]
    databaseLogin.setTransactionIsolation(DatabaseLogin.TRANSACTION_SERIALIZABLE)
  }
}
