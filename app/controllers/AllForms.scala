package controllers

import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}
import scala.util.matching.Regex

case class Login(username: String, password: String)

case class SignUp(firstName: String, middleName: Option[String], lastName: String,
                  mobileNo: Long, username: String, password: String, repassword: String,
                  gender: String, age: Int)

class AllForms {

  val signUpForm = Form(mapping(
    "firstName" -> nonEmptyText,
    "middleName" -> optional(text),
    "lastName" -> nonEmptyText,
    "mobileNo" -> longNumber.verifying(numberOfDigits),
    "username" -> nonEmptyText,
    "password" -> nonEmptyText.verifying(checkPassword),
    "repassword" -> nonEmptyText.verifying(checkPassword),
    "gender" -> nonEmptyText,
    "age" -> number(18, 75)
  )(SignUp.apply)(SignUp.unapply)
    .verifying(
      "The passwords did not match!",
      signUp => signUp.password == signUp.repassword
    ))

  val loginForm = Form(mapping(
    "username" -> nonEmptyText,
    "password" -> nonEmptyText.verifying(checkPassword)
  )(Login.apply)(Login.unapply))

  def checkPassword: Constraint[String] = {
    Constraint("checkPassword.constraint")(
      {
        {
          case password if password.length < 8 => Invalid(ValidationError("Password length must be greater than or equal to 8"))
          case allLetters() => Invalid(ValidationError("Password must also contain numbers"))
          case allNumbers() => Invalid(ValidationError("Password must also contain letters"))
          case _ => Valid
        }
      }
    )
  }

  def numberOfDigits: Constraint[Long] = {
    Constraint("checkMobileNumber.constraint")(
      {
        {
          case mobileNumber if mobileNumber.toString.length != 10 => Invalid(ValidationError("Mobile Number must be of 10 digits!"))
          case _ => Valid
        }
      })
  }

  val allNumbers: Regex = """\d*""".r
  val allLetters: Regex = """[A-Za-z]*""".r

}
