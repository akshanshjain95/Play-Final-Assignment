package controllers

import com.google.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import models._
import org.mindrot.jbcrypt.BCrypt
import play.api.Logger
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{AnyContent, Controller, _}
import scala.concurrent.Future


class SignUpController @Inject()(userRepository: UserRepository, hobbyRepository: HobbyRepository,
                                 userHobbyRepository: UserHobbyRepository, allForms: AllForms,
                                 val messagesApi: MessagesApi) extends Controller with I18nSupport {

  implicit val messages: MessagesApi = messagesApi
  val signUpForm: Form[SignUp] = allForms.signUpForm
  lazy val hobbiesList: Future[List[String]] = hobbyRepository.getHobbies

  def signUp: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    hobbiesList.map(hobbies => Ok(views.html.signUp(allForms.signUpForm, hobbies)))
  }

  def signUpPost: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    Logger.info("SignUpForm is  = " + allForms.signUpForm)
    allForms.signUpForm.bindFromRequest.fold(
      formWithErrors => {
        Logger.error("Form was submitted with errors " + formWithErrors)
        hobbiesList.map(hobbies => BadRequest(views.html.signUp(formWithErrors, hobbies)))
      },
      signUpData => {
        Logger.info("Form was successfully submitted" + signUp)
        Logger.info("Hobbies = " + signUpData.hobbies)
        val hashPassword = BCrypt.hashpw(signUpData.password, BCrypt.gensalt())
        val userData: User = User(0, signUpData.name.firstName, signUpData.name.middleName, signUpData.name.lastName,
          signUpData.mobileNo, signUpData.email, signUpData.username, hashPassword,
          signUpData.gender, signUpData.age, false, true)
        userRepository.checkEmail(userData.email).flatMap {
          case true =>
            userRepository.checkUsername(userData.username).flatMap {
              case true =>
                Logger.info("userData = " + userData)
                userRepository.addUser(userData).flatMap {
                  case true =>
                    val hobbyIDs: Future[List[List[Int]]] = hobbyRepository.getHobbyIDs(signUpData.hobbies)
                    hobbyIDs.flatMap(
                      listOfHobbyIds => userHobbyRepository.addUserHobby(userData.email, listOfHobbyIds).map {
                        case true => Ok(views.html.userProfile(signUpData)).withSession("email" -> s"${userData.email}")
                        case false => Redirect(routes.SignUpController.signUp)
                      }
                    )
                  case false => Future.successful(Redirect(routes.SignUpController.signUp))
                }
              case false => Future.successful(Redirect(routes.SignUpController.signUp))
            }
          case false => Future.successful(Redirect(routes.SignUpController.signUp))
        }
      }
    )
  }

}
