package controllers

import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}
import scala.util.matching.Regex

case class Login(email: String, password: String)

case class SignUp(name: Name, mobileNo: Long, email: String, password: String,
                  repassword: String, gender: String, age: Int, hobbies: List[String])

case class Name(firstName: String, middleName: Option[String], lastName: String)

case class UpdateUserForm(name: Name, mobileNo: Long,
                          gender: String, age: Int, hobbies: List[Int])

case class UpdatePassword(email: String, password: String, repassword: String)

case class AddAssignment(title: String, description: String)

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
    "password" -> nonEmptyText.verifying(checkPassword),
    "repassword" -> nonEmptyText.verifying(checkPassword),
    "gender" -> nonEmptyText,
    "age" -> number(MIN_AGE, MAX_AGE),
    "hobbies" -> list(nonEmptyText).verifying(nonEmptyList)
  )(SignUp.apply)(SignUp.unapply)
    .verifying(
      "The passwords did not match!",
      signUp => signUp.password == signUp.repassword
    ))

  val loginForm = Form(mapping(
    "email" -> email,
    "password" -> nonEmptyText.verifying(checkPassword)
  )(Login.apply)(Login.unapply))

  val updateUserForm = Form(mapping(
    "name" -> mapping(
      "firstName" -> nonEmptyText.verifying(checkName),
      "middleName" -> optional(text).verifying(checkMiddleName),
      "lastName" -> nonEmptyText.verifying(checkName)
    )(Name.apply)(Name.unapply),
    "mobileNo" -> longNumber.verifying(numberOfDigits),
    "gender" -> nonEmptyText,
    "age" -> number(MIN_AGE, MAX_AGE),
    "hobbies" -> list(number).verifying(nonEmptyListOfID)
  )(UpdateUserForm.apply)(UpdateUserForm.unapply))

  val updatePasswordForm = Form(mapping(
    "email" -> email,
    "password" -> nonEmptyText.verifying(checkPassword),
    "repassword" -> nonEmptyText.verifying(checkPassword)
  )(UpdatePassword.apply)(UpdatePassword.unapply)
    .verifying(
      "The passwords did not match!",
      updatePassword => updatePassword.password == updatePassword.repassword
    ))

  val addAssignmentForm = Form(mapping(
    "title" -> nonEmptyText,
    "description" -> nonEmptyText
  )(AddAssignment.apply)(AddAssignment.unapply))

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

  def nonEmptyList: Constraint[List[String]] = {
    Constraint("checkList.constraint")(
      {
        hobbies =>
          if(hobbies.isEmpty) Invalid(ValidationError("Select atleast one hobby!")) else Valid
      }
    )
  }

  def nonEmptyListOfID: Constraint[List[Int]] = {
    Constraint("checkList.constraint")(
      {
        hobbies =>
          if(hobbies.isEmpty) Invalid(ValidationError("Select atleast one hobby!")) else Valid
      }
    )
  }

  val allNumbers: Regex = """\d*""".r
  val allLetters: Regex = """[A-Za-z]*""".r

}
