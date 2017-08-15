package models

import org.scalatestplus.play.PlaySpec

class UserHobbyRepositoryTest extends PlaySpec {

  val userHobbyRepo = new ModelsTest[UserHobbyRepository]

  "UserHobbyRepository" should {

    "not be able to add the hobby for the user if received hobby list is empty" in {

      val result = userHobbyRepo.result(userHobbyRepo.repository.addUserHobby(1, List()))

      result mustEqual false
    }

    "be able to add the hobby for the user" in {

      val result = userHobbyRepo.result(userHobbyRepo.repository.addUserHobby(1, List(1,3)))

      result mustEqual true
    }

    "return all the hobbies for a given user" in {

      val result = userHobbyRepo.result(userHobbyRepo.repository.getUserHobby(1))

      result mustEqual List(1,3)
    }

    "be able to delete the hobbies for the user" in {

      val result = userHobbyRepo.result(userHobbyRepo.repository.deleteUserHobby(1))

      result mustEqual true
    }
  }
}
