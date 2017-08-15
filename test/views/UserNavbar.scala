package views

import org.scalatestplus.play.PlaySpec

class UserNavbar extends PlaySpec {

  "User Navbar template" should {

    "render navbar for user" in {

      val userNavbarPage = views.html.userNavbar.render("Profile")

      userNavbarPage.toString.contains("Knoldus") mustEqual true
    }
  }
}
