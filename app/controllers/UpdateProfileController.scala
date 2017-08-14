package controllers

import javax.inject.Inject
import models.{HobbyRepository, User, UserHobbyRepository, UserRepository}
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Controller}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UpdateProfileController @Inject()(userRepository: UserRepository, hobbyRepository: HobbyRepository,
                                        userHobbyRepository: UserHobbyRepository, allForms: AllForms, val messagesApi: MessagesApi) extends Controller with I18nSupport {

  implicit val messages: MessagesApi = messagesApi
  lazy val hobbiesList: Future[List[String]] = hobbyRepository.getHobbies

  def showProfile: Action[AnyContent] = Action.async { implicit request =>
    val userIDString: Option[String] = request.session.get("userID")

    userIDString match {

      case Some(userIDString) =>
        val userID: Int = userIDString.toInt
        Logger.info("Got the userID from the session! UserID = " + userID)
        userRepository.getUserByID(userID).flatMap {

          case Nil =>
            Logger.info("Did not receive any user with given UserID! Redirecting to welcome page!")
            Future.successful(Ok(views.html.index()))
          case userList: List[User] =>
            val user = userList.head
            userHobbyRepository.getUserHobby(userID).flatMap {
              case Nil =>
                Logger.info("Did not receive any hobbies for the user!")
                Future.successful(Ok(views.html.index()))

              case hobbies: List[String] =>
                Logger.info("Recieved list of hobbies")
                val updateUserFormValues = UpdateUserForm(Name(user.firstName, user.middleName, user.lastName),
                  user.mobileNo, user.email, user.gender, user.age, hobbies)
                hobbiesList.map(hobbies =>
                  Ok(views.html.userProfile(allForms.updateUserForm.fill(updateUserFormValues), hobbies))
                )
            }
        }
      case None => Future.successful(Ok(views.html.index()))
    }
  }

  def showProfilePost: Action[AnyContent] = Action.async { implicit request =>
    allForms.updateUserForm.bindFromRequest.fold(
      formWithErrors => {
        Logger.error("Form was submitted with errors " + formWithErrors)
        hobbiesList.map(hobbies => BadRequest(views.html.userProfile(formWithErrors, hobbies)))
      },
      updateUserData => {
        Logger.info("Form was successfully submitted")
        val optionOfID = request.session.get("userID")
        optionOfID match {
          case Some(userID) =>
            val updateUserForm = UpdateUserForm(updateUserData.name, updateUserData.mobileNo,
              updateUserData.email, updateUserData.gender, updateUserData.age, updateUserData.hobbies)
            userRepository.checkEmailForUpdate(updateUserData.email, userID.toInt).flatMap {
              case true =>
                Logger.info("Email is unique!")
                userRepository.updateUser(updateUserForm, userID.toInt).flatMap {
                  case true =>
                    userHobbyRepository.deleteUserHobby(userID.toInt).flatMap {
                      case true =>
                        hobbyRepository.getHobbyIDs(updateUserData.hobbies).flatMap {
                          case Nil => Future.successful(Redirect(routes.UpdateProfileController.showProfile).flashing("error" -> "Something went wrong!"))
                          case hobbyIDs: List[List[Int]] =>
                            userHobbyRepository.addUserHobby(userID.toInt, hobbyIDs).map {
                              case true => Redirect(routes.UpdateProfileController.showProfile).flashing("success" -> "User Profile successfully updated!")
                              case false => Redirect(routes.UpdateProfileController.showProfile).flashing("error" -> "Something went wrong!")
                            }
                        }
                      case false => Future.successful(Redirect(routes.UpdateProfileController.showProfile).flashing("error" -> "User Profile not updated due to errors!"))
                    }
                }
              case false => Future.successful(Redirect(routes.UpdateProfileController.showProfile).flashing("error" -> "Email already exists!"))
            }
          case None => Future.successful(Ok(views.html.index()))
        }
      }
    )
  }

}
