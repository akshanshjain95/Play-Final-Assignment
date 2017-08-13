package controllers

import models.{HobbyRepository, User, UserHobbyRepository, UserRepository}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller}

class UpdateProfileController(userRepository: UserRepository, hobbyRepository: HobbyRepository,
                              userHobbyRepository: UserHobbyRepository, allForms: AllForms, val messagesApi: MessagesApi) extends Controller with I18nSupport {

  implicit val messages: MessagesApi = messagesApi

  def showProfile = Action { implicit request =>
    val email: Option[String] = request.session.get("email")

    email match {

      case Some(email) => userRepository.getUser(email).map {

        case Nil =>
        case userList: List[User] =>
          val user = userList.head
          val updateUserForm = UpdateUserForm(Name(user.firstName, user.middleName, user.lastName),
            user.mobileNo, user.email, user.username, user.gender, user.age)

      }
        Ok(views.html.userProfile)
    }
  }
}
