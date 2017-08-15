package models

import org.scalatestplus.play.PlaySpec

class UserRepositoryTest extends PlaySpec {

  val userRepo = new ModelsTest[UserRepository]

  val user = User(100, "Akshansh", None, "Jain", 9999819877L, "akshansh786@knoldus.com", "akshansh123",
  "male", 21, false, true)

  "User Repository" should {
    "be able to add a user" in {
      val result = userRepo.result(userRepo.repository.addUser(user))
       result mustEqual true
    }
  }

}