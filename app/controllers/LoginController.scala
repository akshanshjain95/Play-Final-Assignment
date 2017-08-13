package controllers

import com.google.inject.Inject
import models.UserRepository
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{AnyContent, Controller, _}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class LoginController @Inject()(userRepository: UserRepository, allForms: AllForms, val messagesApi: MessagesApi) extends Controller with I18nSupport {

  implicit val messages: MessagesApi = messagesApi

  def login: Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.login(allForms.loginForm))
  }

  def loginPost: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    allForms.loginForm.bindFromRequest.fold(
      formWithErrors => {
        Logger.info("Form with errors = " + formWithErrors)
        Future.successful(BadRequest(views.html.login(formWithErrors)))
      },
      userData => {
        userRepository.checkIfUserExists(userData.username, userData.password).map {
          case true => {
            val email = userRepository.getEmail(userData.username)
            Ok(views.html.userProfile(userData)).withSession("email" -> s"$email")
          }
          case false => Redirect(routes.LoginController.login)
        }
      })
  }

}
