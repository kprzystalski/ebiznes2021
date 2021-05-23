package models.repository

import javax.inject.{Inject, Singleton}
import models.AuthToken
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

@Singleton
class AuthTokenRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class AuthTokenTable(tag: Tag) extends Table[AuthToken](tag, "authToken") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def userId = column[Long]("userId")

    def * = (id, userId) <> ((AuthToken.apply _).tupled, AuthToken.unapply)
  }

  val authToken = TableQuery[AuthTokenTable]

  def create(userId: Long): Future[AuthToken] = db.run {
    (authToken.map(r => r.userId)
      returning authToken.map(_.id)
      into { case (userId, id) => AuthToken(id, userId) }
      ) += userId
  }
}
