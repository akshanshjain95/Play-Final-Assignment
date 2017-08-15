package models

import controllers.{AllForms, Name, UpdatePassword, UpdateUserForm}
import org.scalatestplus.play.PlaySpec

class UserRepositoryTest extends PlaySpec {

  val userRepo = new ModelsTest[UserRepository]

  val user = User(2, "Akshansh", None, "Jain", 9999819877L, "akshansh786@knoldus.com", "$2a$10$y981gqfa08fwWpojVudnKuwnJSt5lkYhr1IHEIMXUb9sN42usQ4Tu",
  "male", 21, false, true)

  val updateUserForm = UpdateUserForm(Name("Akshansh", None, "Jain"), 1234567891L,
    "male", 21, List(1,3))

  val updatePassword = UpdatePassword("akshansh786@knoldus.com", "akshansh1234", "akshansh1234")

  "User Repository" should {

    "be able to add a user" in {

      val result = userRepo.result(userRepo.repository.addUser(user))
       result mustEqual true
    }

    "return false if email exists" in {

      val result = userRepo.result(userRepo.repository.checkEmail("akshansh786@knoldus.com"))
      result mustEqual false
    }

    "return true if email does not exist" in {

      val result = userRepo.result(userRepo.repository.checkEmail("not@exists.com"))
      result mustEqual true
    }

    "return true if user exists in Database" in {

      val result = userRepo.result(userRepo.repository.checkIfUserExists("akshansh786@knoldus.com", "akshansh123"))

      result mustEqual true
    }

    "return false if user does not exist in Database" in {

      val result = userRepo.result(userRepo.repository.checkIfUserExists("akshansh95@knoldus.com", "akshansh123"))

      result mustEqual false
    }

    "return false if user entered wrong password" in {

      val result = userRepo.result(userRepo.repository.checkIfUserExists("akshansh786@knoldus.com", "akshansh1123"))

      result mustEqual false
    }

    "return user by ID" in {

      val result = userRepo.result(userRepo.repository.getUserByID(2))

      result mustEqual List(User(2, "Akshansh", None, "Jain", 9999819877L, "akshansh786@knoldus.com", "$2a$10$y981gqfa08fwWpojVudnKuwnJSt5lkYhr1IHEIMXUb9sN42usQ4Tu",
        "male", 21, false, true))
    }

    "return userID" in {

      val result = userRepo.result(userRepo.repository.getUserID("akshansh786@knoldus.com"))

      result mustEqual List(2)
    }

    "update user" in {

      val result = userRepo.result(userRepo.repository.updateUser(updateUserForm, 2))

      result mustEqual true
    }

    "update password for the user" in {

      val result = userRepo.result(userRepo.repository.updateUserByEmail(updatePassword))

      result mustEqual true
    }

    "return all the users with the enable/disable status" in {

      val result = userRepo.result(userRepo.repository.getAllUsersWithStatus(1))

      result mustEqual Map("akshansh786@knoldus.com" -> true)
    }

    "Disable a user in database" in {

      val result = userRepo.result(userRepo.repository.enableUser("akshansh786@knoldus.com", false))

      result mustEqual true
    }

    "return user information for session" in {

      val result = userRepo.result(userRepo.repository.getUserInfoForSession("akshansh786@knoldus.com"))

      result mustEqual List((2, false, false))
    }
  }

}