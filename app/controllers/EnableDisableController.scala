package controllers

import javax.inject.Inject

import models.{HobbyRepository, UserHobbyRepository, UserRepository}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class EnableDisableController @Inject()(userRepository: UserRepository, hobbyRepository: HobbyRepository,
                                        userHobbyRepository: UserHobbyRepository, allForms: AllForms, val messagesApi: MessagesApi) extends Controller with I18nSupport {

  implicit val messages: MessagesApi = messagesApi

  def showUserList: Action[AnyContent] = Action.async { implicit request =>
    val id: Option[String] = request.session.get("userID")
    id match {
      case Some(userID) =>
        userRepository.getAllUsersWithStatus(userID.toInt).map {
          listOfUsersWithStatus: Map[String, Boolean] =>
            Ok(views.html.enableDisable(listOfUsersWithStatus))
        }
      case None => Future.successful(Redirect(routes.LoginController.login()).flashing("error" -> "Something went wrong. Please login again."))
    }
  }

  def enableDisablePost(email: String): Action[AnyContent] = Action.async { implicit request =>
    val value: Option[String] = request.body.asFormUrlEncoded.get("ableButton").headOption
    value match {
      case Some("enable") =>
        userRepository.enableUser(email, true).map{
          case true =>
            Redirect(routes.EnableDisableController.showUserList()).flashing("success" -> "User was successfully enabled.")
          case false =>
            Redirect(routes.EnableDisableController.showUserList()).flashing("error" -> "Something went wrong. User was not enabled.")
        }
      case Some("disable") =>
        userRepository.enableUser(email, false).map{
          case true =>
            Redirect(routes.EnableDisableController.showUserList()).flashing("success" -> "User was successfully disabled.")
          case false =>
            Redirect(routes.EnableDisableController.showUserList()).flashing("error" -> "Something went wrong. User was not disabled.")
        }
      case None =>
        Future.successful(Redirect(routes.EnableDisableController.showUserList()).flashing("error" -> "Somethign went wrong. Please perform the action again."))
    }
  }

}
