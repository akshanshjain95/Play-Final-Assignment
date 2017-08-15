package controllers

import javax.inject.Inject

import akka.util.ByteString
import models.UserRepository
import play.api.Logger
import play.api.http.HttpEntity
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class EnableDisableController @Inject()(userRepository: UserRepository, val messagesApi: MessagesApi) extends Controller with I18nSupport {

  implicit val messages: MessagesApi = messagesApi

  def showUserList: Action[AnyContent] = Action.async { implicit request =>
    val id: Option[String] = request.session.get("userID")
    val isAdmin = request.session.get("isAdmin")
    isAdmin match {
      case Some("true") =>
        id match {
          case Some(userID) =>
            userRepository.getAllUsersWithStatus(userID.toInt).map {
              listOfUsersWithStatus: Map[String, Boolean] =>
                Ok(views.html.enableDisable(listOfUsersWithStatus))
            }
          case None => Future.successful(Redirect(routes.LoginController.login()).flashing("error" -> "Something went wrong. Please login again."))
        }
      case Some("false") => Future.successful(Result(header = ResponseHeader(NOT_FOUND),
        body = HttpEntity.Strict(ByteString("You are not authorised to view this page."), Some("text/plain"))
      ))
      case None => Future.successful(Redirect(routes.LoginController.login())
        .flashing("error" -> "Cannot detect if you're an admin or not. Please login again.").withNewSession)
    }
  }

  def enableDisablePost(email: String): Action[AnyContent] = Action.async { implicit request =>
    Logger.error("body of request = " + request.body)
    val value: Option[String] = request.body.asFormUrlEncoded.get("ableButton").headOption
    value match {
      case Some("enable") =>
        userRepository.enableUser(email, true).map {
          case true =>
            Redirect(routes.EnableDisableController.showUserList()).flashing("success" -> "User was successfully enabled.")
          case false =>
            Redirect(routes.EnableDisableController.showUserList()).flashing("error" -> "Something went wrong. User was not enabled.")
        }
      case Some("disable") =>
        userRepository.enableUser(email, false).map {
          case true =>
            Redirect(routes.EnableDisableController.showUserList()).flashing("success" -> "User was successfully disabled.")
          case false =>
            Redirect(routes.EnableDisableController.showUserList()).flashing("error" -> "Something went wrong. User was not disabled.")
        }
    }
  }

}
