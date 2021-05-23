package modules

import com.google.inject.name.Named
import com.google.inject.{AbstractModule, Provides}
import com.mohiva.play.silhouette.api.crypto._
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services._
import com.mohiva.play.silhouette.api.util._
import com.mohiva.play.silhouette.api.{Environment, EventBus, Silhouette, SilhouetteProvider}
import com.mohiva.play.silhouette.crypto.{JcaCrypter, JcaCrypterSettings, JcaSigner, JcaSignerSettings}
import com.mohiva.play.silhouette.impl.authenticators._
import com.mohiva.play.silhouette.impl.providers._
import com.mohiva.play.silhouette.impl.providers.oauth2.{FacebookProvider, GoogleProvider}
import com.mohiva.play.silhouette.impl.providers.state.{CsrfStateItemHandler, CsrfStateSettings}
import com.mohiva.play.silhouette.impl.util._
import com.mohiva.play.silhouette.password.{BCryptPasswordHasher, BCryptSha256PasswordHasher}
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import com.mohiva.play.silhouette.persistence.repositories.DelegableAuthInfoRepository
import com.typesafe.config.Config
import models.repository.{OAuth2InfoRepository, PasswordInfoRepository, UserRepository}
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import net.ceedubs.ficus.readers.ValueReader
import net.codingwell.scalaguice.ScalaModule
import play.api.Configuration
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.ws.WSClient
import play.api.mvc.{Cookie, CookieHeaderEncoding}
import utils.DefaultEnv

import scala.concurrent.ExecutionContext.Implicits.global
import scala.reflect.ClassTag

class SilhouetteModule extends AbstractModule with ScalaModule {

  implicit val sameSiteReader: ValueReader[Option[Option[Cookie.SameSite]]] =
    (config: Config, path: String) => {
      if (config.hasPathOrNull(path)) {
        if (config.getIsNull(path))
          Some(None)
        else {
          Some(Cookie.SameSite.parse(config.getString(path)))
        }
      } else {
        None
      }
    }

  override def configure(): Unit = {
    bind[Silhouette[DefaultEnv]].to[SilhouetteProvider[DefaultEnv]]
    bind[CacheLayer].to[PlayCacheLayer]
    bind[IDGenerator].toInstance(new SecureRandomIDGenerator())
    bind[FingerprintGenerator].toInstance(new DefaultFingerprintGenerator(false))
    bind[EventBus].toInstance(EventBus())
    bind[Clock].toInstance(Clock())
  }

  @Provides
  def provideHTTPLayer(client: WSClient): HTTPLayer = new PlayHTTPLayer(client)

  @Provides
  def provideEnvironment(userRepository: UserRepository,
                         authenticatorService: AuthenticatorService[CookieAuthenticator],
                         eventBus: EventBus): Environment[DefaultEnv] =
    Environment[DefaultEnv](
      userRepository,
      authenticatorService,
      Seq(),
      eventBus
    )

  @Provides
  @Named("authenticator-signer")
  def provideAuthenticatorSigner(configuration: Configuration): Signer = {
    val config = configuration.underlying.as[JcaSignerSettings]("silhouette.authenticator.signer")

    new JcaSigner(config)
  }

  @Provides
  @Named("authenticator-crypter")
  def provideAuthenticatorCrypter(configuration: Configuration): Crypter = {
    val config = configuration.underlying.as[JcaCrypterSettings]("silhouette.authenticator.crypter")
    new JcaCrypter(config)
  }

  @Provides
  def provideAuthInfoRepository(passwordInfoDAO: DelegableAuthInfoDAO[PasswordInfo],
                                oAuth2InfoDao: DelegableAuthInfoDAO[OAuth2Info]): AuthInfoRepository =
    new DelegableAuthInfoRepository(passwordInfoDAO, oAuth2InfoDao)

  @Provides
  def provideAuthenticatorService(
                                   @Named("authenticator-signer") signer: Signer,
                                   @Named("authenticator-crypter") crypter: Crypter,
                                   cookieHeaderEncoding: CookieHeaderEncoding,
                                   fingerprintGenerator: FingerprintGenerator,
                                   idGenerator: IDGenerator,
                                   configuration: Configuration,
                                   clock: Clock): AuthenticatorService[CookieAuthenticator] = {
    val config = configuration.underlying.as[CookieAuthenticatorSettings]("silhouette.authenticator")
    val authenticatorEncoder = new CrypterAuthenticatorEncoder(crypter)
    new CookieAuthenticatorService(config, None, signer, cookieHeaderEncoding, authenticatorEncoder, fingerprintGenerator, idGenerator, clock)
  }

  @Provides
  def providePasswordHasherRegistry(): PasswordHasherRegistry =
    PasswordHasherRegistry(new BCryptSha256PasswordHasher(), Seq(new BCryptPasswordHasher()))

  @Provides
  def provideCredentialsProvider(authInfoRepository: AuthInfoRepository,
                                 passwordHasherRegistry: PasswordHasherRegistry): CredentialsProvider =
    new CredentialsProvider(authInfoRepository, passwordHasherRegistry)

  @Provides
  def passwordInfoDao(dbConfigProvider: DatabaseConfigProvider): DelegableAuthInfoDAO[PasswordInfo] =
    new PasswordInfoRepository(dbConfigProvider)

  @Provides
  def passwordInfoClassTag(): ClassTag[PasswordInfo] =
    ClassTag[PasswordInfo](PasswordInfo.getClass)

  @Provides
  def provideSocialProviderRegistry(googleProvider: GoogleProvider,
                                    facebookProvider: FacebookProvider): SocialProviderRegistry =
    SocialProviderRegistry(Seq(googleProvider, facebookProvider))

  @Provides
  def provideGoogleProvider(httpLayer: HTTPLayer,
                            socialStateHandler: SocialStateHandler,
                            configuration: Configuration): GoogleProvider =
    new GoogleProvider(httpLayer, socialStateHandler, configuration.underlying.as[OAuth2Settings]("silhouette.google"))

  @Provides
  def provideFacebookProvider(httpLayer: HTTPLayer,
                              socialStateHandler: SocialStateHandler,
                              configuration: Configuration): FacebookProvider =
    new FacebookProvider(httpLayer, socialStateHandler, configuration.underlying.as[OAuth2Settings]("silhouette.facebook"))

  @Provides
  def provideSocialStateHandler(@Named("social-state-signer") signer: Signer,
                                csrfStateItemHandler: CsrfStateItemHandler): SocialStateHandler =
    new DefaultSocialStateHandler(Set(csrfStateItemHandler), signer)

  @Provides
  @Named("social-state-signer")
  def provideSocialStateSigner(configuration: Configuration): Signer = {
    val config = configuration.underlying.as[JcaSignerSettings]("silhouette.socialStateHandler.signer")
    new JcaSigner(config)
  }

  @Provides
  def provideCsrfStateItemHandler(idGenerator: IDGenerator,
                                  @Named("csrf-state-item-signer") signer: Signer,
                                  configuration: Configuration): CsrfStateItemHandler = {
    val settings = configuration.underlying.as[CsrfStateSettings]("silhouette.csrfStateItemHandler")
    new CsrfStateItemHandler(settings, idGenerator, signer)
  }

  @Provides
  @Named("csrf-state-item-signer")
  def provideCSRFStateItemSigner(configuration: Configuration): Signer = {
    val config = configuration.underlying.as[JcaSignerSettings]("silhouette.csrfStateItemHandler.signer")
    new JcaSigner(config)
  }

  @Provides
  def oAuth2InfoDao(dbConfigProvider: DatabaseConfigProvider): DelegableAuthInfoDAO[OAuth2Info] =
    new OAuth2InfoRepository(dbConfigProvider)

  @Provides
  def oAuth2InfoClassTag(): ClassTag[OAuth2Info] =
    ClassTag[OAuth2Info](OAuth2Info.getClass)
}
