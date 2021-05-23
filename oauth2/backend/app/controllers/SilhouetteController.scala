package controllers

import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.{SecuredActionBuilder, UnsecuredActionBuilder}
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AuthenticatorService
import com.mohiva.play.silhouette.api.util.{Clock, PasswordHasherRegistry}
import com.mohiva.play.silhouette.impl.providers.{CredentialsProvider, SocialProviderRegistry}
import javax.inject.{Inject, Singleton}
import models.repository.{AuthTokenRepository, UserRepository}
import play.api.Logging
import play.api.http.FileMimeTypes
import play.api.i18n.{I18nSupport, Langs, MessagesApi}
import play.api.mvc._
import utils.DefaultEnv

abstract class SilhouetteController(override protected val controllerComponents: DefaultSilhouetteControllerComponents)
  extends MessagesAbstractController(controllerComponents) with SilhouetteComponents with I18nSupport with Logging {

  def securedAction: SecuredActionBuilder[EnvType, AnyContent] = controllerComponents.silhouette.SecuredAction

  def unsecuredAction: UnsecuredActionBuilder[EnvType, AnyContent] = controllerComponents.silhouette.UnsecuredAction

  def userRepository: UserRepository = controllerComponents.userRepository

  def authInfoRepository: AuthInfoRepository = controllerComponents.authInfoRepository

  def passwordHasherRegistry: PasswordHasherRegistry = controllerComponents.passwordHasherRegistry

  def authTokenRepository: AuthTokenRepository = controllerComponents.authTokenRepository

  def clock: Clock = controllerComponents.clock

  def credentialsProvider: CredentialsProvider = controllerComponents.credentialsProvider

  def silhouette: Silhouette[EnvType] = controllerComponents.silhouette

  def authenticatorService: AuthenticatorService[AuthType] = silhouette.env.authenticatorService

  def socialProviderRegistry: SocialProviderRegistry = controllerComponents.socialProviderRegistry
}

trait SilhouetteComponents {
  type EnvType = DefaultEnv
  type AuthType = EnvType#A
  type IdentityType = EnvType#I

  def userRepository: UserRepository

  def authInfoRepository: AuthInfoRepository

  def passwordHasherRegistry: PasswordHasherRegistry

  def authTokenRepository: AuthTokenRepository

  def clock: Clock

  def credentialsProvider: CredentialsProvider

  def socialProviderRegistry: SocialProviderRegistry

  def silhouette: Silhouette[EnvType]
}

@Singleton
final case class DefaultSilhouetteControllerComponents @Inject()(silhouette: Silhouette[DefaultEnv],
                                                                 userRepository: UserRepository,
                                                                 authInfoRepository: AuthInfoRepository,
                                                                 passwordHasherRegistry: PasswordHasherRegistry,
                                                                 authTokenRepository: AuthTokenRepository,
                                                                 clock: Clock,
                                                                 credentialsProvider: CredentialsProvider,
                                                                 socialProviderRegistry: SocialProviderRegistry,
                                                                 messagesActionBuilder: MessagesActionBuilder,
                                                                 actionBuilder: DefaultActionBuilder,
                                                                 parsers: PlayBodyParsers,
                                                                 messagesApi: MessagesApi,
                                                                 langs: Langs,
                                                                 fileMimeTypes: FileMimeTypes,
                                                                 executionContext: scala.concurrent.ExecutionContext) extends MessagesControllerComponents with SilhouetteComponents
