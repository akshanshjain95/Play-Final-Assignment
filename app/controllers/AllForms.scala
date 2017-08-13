package controllers

import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}
import scala.util.matching.Regex

case class Login(username: String, password: String)

case class SignUp(name: Name, mobileNo: Long, email: String, username: String, password: String,
                  repassword: String, gender: String, age: Int, hobbies: List[String])

case class Name(firstName: String, middleName: Option[String], lastName: String)

case class UpdateUserForm(name: Name, mobileNo: Long, email: String, username: String,
                          gender: String, age: Int, hobbies: List[String])

class AllForms {

  val MAX_AGE = 75
  val MIN_AGE = 18

  val signUpForm = Form(mapping(
    "name" -> mapping(
      "firstName" -> nonEmptyText.verifying(checkName),
      "middleName" -> optional(text).verifying(checkMiddleName),
      "lastName" -> nonEmptyText.verifying(checkName)
    )(Name.apply)(Name.unapply),
    "mobileNo" -> longNumber.verifying(numberOfDigits),
    "email" -> email,
    "username" -> nonEmptyText,
    "password" -> nonEmptyText.verifying(checkPassword),
    "repassword" -> nonEmptyText.verifying(checkPassword),
    "gender" -> nonEmptyText,
    "age" -> number(MIN_AGE, MAX_AGE),
    "hobbies" -> list(text)
  )(SignUp.apply)(SignUp.unapply)
    .verifying(
      "The passwords did not match!",
      signUp => signUp.password == signUp.repassword
    ))

  val loginForm = Form(mapping(
    "username" -> nonEmptyText,
    "password" -> nonEmptyText.verifying(checkPassword)
  )(Login.apply)(Login.unapply))

  val updateUserForm = Form(mapping(
    "name" -> mapping(
      "firstName" -> nonEmptyText.verifying(checkName),
      "middleName" -> optional(text).verifying(checkMiddleName),
      "lastName" -> nonEmptyText.verifying(checkName)
    )(Name.apply)(Name.unapply),
    "mobileNo" -> longNumber.verifying(numberOfDigits),
    "email" -> email,
    "username" -> nonEmptyText,
    "gender" -> nonEmptyText,
    "age" -> number(MIN_AGE, MAX_AGE),
    "hobbies" -> list(text)
  )(UpdateUserForm.apply)(UpdateUserForm.unapply))

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

  def checkName: Constraint[String] = {
    Constraint("checkName.constraint")(
      {
        {
          case allLetters() => Valid
          case _ => Invalid(ValidationError("Name must only contain letters!"))
        }
      }
    )
  }

  def checkMiddleName: Constraint[Option[String]] = {
    Constraint("checkName.constraint")(
      {
        middleNameOption =>
          val middleName = middleNameOption.fold("None")(identity)
          middleName match {
            case "None" => Valid
            case allLetters() => Valid
            case _ => Invalid(ValidationError("Name must only contain letters!"))
          }
      }
    )
  }

  val allNumbers: Regex = """\d*""".r
  val allLetters: Regex = """[A-Za-z]*""".r

}
