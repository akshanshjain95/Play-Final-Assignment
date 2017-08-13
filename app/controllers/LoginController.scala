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
    val email = request.flash.get("email").getOrElse("")
    Ok(views.html.login(allForms.loginForm.fill(Login(email, ""))))
  }

  def loginPost: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    allForms.loginForm.bindFromRequest.fold(
      formWithErrors => {
        Logger.info("Form with errors = " + formWithErrors)
        Future.successful(BadRequest(views.html.login(formWithErrors)))
      },
      userData => {
        userRepository.checkIfUserExists(userData.email, userData.password).flatMap {
          case true => {
            Logger.info("User exists!")
            userRepository.getUserID(userData.email).map {
              case Nil => Ok(views.html.index())
              case id: List[Int] =>
                Redirect(routes.UpdateProfileController.showProfile).withSession("userID" -> s"${id.head}")
            }
          }
          case false => Future.successful(Redirect(routes.LoginController.login).flashing("error" -> "Username and password combination did not match!", "email" -> s"${userData.email}"))
        }
      })
  }

}
