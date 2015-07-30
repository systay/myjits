package controllers

import javax.inject.Inject

import dal.UserRepository
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n._
import play.api.mvc.{Action, Controller}

class UserController @Inject()(repo: UserRepository, val messagesApi: MessagesApi) extends Controller with I18nSupport {
  val createUserForm = Form(
    mapping(
      "name" -> nonEmptyText,
      "email" -> email,
      "password" -> nonEmptyText(minLength = 6)
    )(CreateUserForm.apply)(CreateUserForm.unapply)
  )

  val loginForm = Form(
    mapping(
      "email" -> email,
      "password" -> nonEmptyText
  )(LogOnForm.apply)(LogOnForm.unapply))


  def index = Action { implicit request =>
    if (request.session.data.contains("id"))
      Redirect(routes.MainPageController.index)
    else
      Ok(views.html.login("", createUserForm, loginForm))
  }

  def addUser = Action { implicit request =>
    createUserForm.bindFromRequest().fold(
      errorForm => Ok(views.html.login("Try again", errorForm, loginForm)),
      user => {
        val createdUser = repo.saveUser(name = user.name, email = user.email, password = user.passwd)
        Redirect(routes.MainPageController.index).withSession("id" -> createdUser.email)
      }
    )
  }

  def logIn = Action { implicit request =>
    loginForm.bindFromRequest().fold(
      errorForm => Ok(views.html.login("Try again", createUserForm, errorForm)),
      logOnDetails => {
        repo.load(email = logOnDetails.email) match {
          case Some(user) if user.checkPassword(logOnDetails.passwd) =>
            Redirect(routes.MainPageController.index).withSession("id" -> user.email)
          case _ => Ok(views.html.login("Unknown user or wrong password!", createUserForm, loginForm))
        }

      }
    )
  }

  def logOff = Action { implicit request =>
    Redirect(routes.MainPageController.index).removingFromSession("id")
  }

}

case class CreateUserForm(name: String, email: String, passwd: String)
case class LogOnForm(email: String, passwd: String)
