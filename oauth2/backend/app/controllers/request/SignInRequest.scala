package controllers.request

import play.api.libs.json.{Json, OFormat}

case class SignInRequest(email: String, password: String)

object SignInRequest {
  implicit val signInRequestForm: OFormat[SignInRequest] = Json.format[SignInRequest]
}
