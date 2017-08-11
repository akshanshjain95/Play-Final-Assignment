package controllers

import com.google.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import models.{User, UserRepository}
import play.api.Logger
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{AnyContent, Controller, _}
import scala.concurrent.Future


class SignUpController @Inject()(userRepository: UserRepository, allForms: AllForms, val messagesApi: MessagesApi) extends Controller with I18nSupport {

  implicit val messages = messagesApi
  val signUpForm: Form[SignUp] = allForms.signUpForm

  def signUp: Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.signUp(signUpForm))
  }

  def signUpPost: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    Logger.info("SignUpForm is  = " + signUpForm)
    allForms.signUpForm.bindFromRequest.fold(
      formWithErrors => {
        Logger.error("Form was submitted with errors" + formWithErrors)
        Future.successful(BadRequest(views.html.signUp(formWithErrors)))
      },
      signUpData => {
        Logger.info("Form was successfully submitted")
        val userData: User = User(0, signUpData.firstName, signUpData.middleName, signUpData.lastName,
          signUpData.mobileNo, signUpData.username, signUpData.password,
          signUpData.gender, signUpData.age)
        userRepository.checkUsername(userData.username).flatMap {
          case true =>
            userRepository.addUser(userData).map {
              case true => Ok(views.html.showDataOfSignUp(signUpData))
              case false => Redirect(routes.SignUpController.signUp)
            }
          case false => Future.successful(Redirect(routes.SignUpController.signUp))
        }
      }
    )
  }

}
