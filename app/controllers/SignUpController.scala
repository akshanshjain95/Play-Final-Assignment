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
  lazy val hobbiesList: Future[List[Hobby]] = hobbyRepository.getHobbies

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
          signUpData.mobileNo, signUpData.email, hashPassword,
          signUpData.gender, signUpData.age, false, true)
        userRepository.checkEmail(userData.email).flatMap {
          case true =>
                Logger.info("userData = " + userData)
                userRepository.addUser(userData).flatMap {
                  case true =>
                      userRepository.getUserID(userData.email).flatMap {
                        case Nil => Future.successful(Redirect(routes.SignUpController.signUp)
                          .flashing("error" -> "Something went wrong and we weren't able to retrieve your account's ID."))
                        case id: List[Int] =>
                          userHobbyRepository.addUserHobby(id.head, signUpData.hobbies.map(_.toInt)).map {
                            case true => Redirect(routes.UpdateProfileController.showProfile)
                              .flashing("success" -> "Successfully signed up!").withSession("userID" -> s"${id.head}")
                            case false => Redirect(routes.SignUpController.signUp)
                              .flashing("error" -> "Something went wrong and we weren't able to store your selected hobbies.")
                          }
                      }
                  case false => Future.successful(Redirect(routes.SignUpController.signUp)
                    .flashing("error" -> "Something went wrong and we weren't able to store your information. Please sign up again."))
                }
          case false => Future.successful(Redirect(routes.SignUpController.signUp)
            .flashing("error" -> "Entered email already exists. If you're an existing member then please sign in!"))
            }
        }
    )
  }

}
