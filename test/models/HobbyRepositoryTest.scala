package models

import org.scalatestplus.play.PlaySpec

class HobbyRepositoryTest extends PlaySpec {

  val hobbyRepo = new ModelsTest[HobbyRepository]

  "Hobby Repository" should {

    "Return list of all hobbies" in {

      val result = hobbyRepo.result(hobbyRepo.repository.getHobbies)

      result mustEqual List(Hobby(1, "Programming"), Hobby(2, "Reading"), Hobby(3, "Sports"), Hobby(4, "Writing"), Hobby(5, "Swimming"))
    }
  }
}
