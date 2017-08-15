package views

import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec

class AdminNavbarTest extends PlaySpec with MockitoSugar {

  "Admin Navbar" should {

    "render navbar for admin" in {

      val adminNavbarPage = views.html.adminNavbar.render("Profile")

      adminNavbarPage.toString.contains("Knoldus") mustEqual true
    }

  }
}
