package controllers

import models.Global
import play.api.mvc._

class Application extends Controller {

  def index = Action {
    val result = Global.engine.execute("RETURN 'Hello Through Neo4j!' as x").dumpToString()

    Ok(views.html.index(result))
  }

}
