package controllers

import javax.inject.Inject

import dal.UserRepository
import play.api.mvc._

class MainPageController @Inject()(repo: UserRepository) extends Controller {

  def index = Action { implicit request =>
    val user = request.session.get("id").flatMap(repo.load)
    Ok(views.html.index("Hello!", user))
  }
}
