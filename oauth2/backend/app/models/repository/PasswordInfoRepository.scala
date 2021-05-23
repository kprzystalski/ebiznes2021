package models.repository

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

class PasswordInfoRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext,
                                                                                 implicit val classTag: ClassTag[PasswordInfo]) extends DelegableAuthInfoDAO[PasswordInfo] {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  case class PasswordInfoDto(id: Long, providerId: String, providerKey: String, hasher: String, password: String, salt: Option[String])

  class PasswordInfoTable(tag: Tag) extends Table[PasswordInfoDto](tag, "passwordInfo") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def providerId = column[String]("providerId")

    def providerKey = column[String]("providerKey")

    def hasher = column[String]("hasher")

    def password = column[String]("password")

    def salt = column[Option[String]]("salt")

    def * = (id, providerId, providerKey, hasher, password, salt) <> ((PasswordInfoDto.apply _).tupled, PasswordInfoDto.unapply)
  }

  val passwordInfo = TableQuery[PasswordInfoTable]

  override def find(loginInfo: LoginInfo): Future[Option[PasswordInfo]] = db.run {
    passwordInfo.filter(_.providerId === loginInfo.providerID)
      .filter(_.providerKey === loginInfo.providerKey)
      .result
      .headOption
  }.map(_.map(dto => PasswordInfo(dto.hasher, dto.password, dto.salt)))

  override def add(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = db.run {
    (passwordInfo.map(c => (c.providerId, c.providerKey, c.hasher, c.password, c.salt))
      returning passwordInfo.map(_.id)
      into { case ((providerId, providerKey, hasher, password, salt), id) => PasswordInfoDto(id, providerId, providerKey, hasher, password, salt) }
      ) += (loginInfo.providerID, loginInfo.providerKey, authInfo.hasher, authInfo.password, authInfo.salt)
  }.map(_ => authInfo)

  override def update(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = db.run {
    passwordInfo.filter(_.providerId === loginInfo.providerID)
      .filter(_.providerKey === loginInfo.providerKey)
      .map(u => (u.hasher, u.password, u.salt))
      .update((authInfo.hasher, authInfo.password, authInfo.salt))
  }.map(_ => authInfo)

  override def save(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] =
    find(loginInfo)
      .flatMap {
        case Some(_) => update(loginInfo, authInfo)
        case None => add(loginInfo, authInfo)
      }

  override def remove(loginInfo: LoginInfo): Future[Unit] = db.run {
    passwordInfo.filter(_.providerId === loginInfo.providerID)
      .filter(_.providerKey === loginInfo.providerKey)
      .delete
  }.map(_ => Unit)
}
