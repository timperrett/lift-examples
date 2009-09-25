package eu.getintheloop.example.comet

import _root_.net.liftweb.http._
import _root_.net.liftweb.util._
import _root_.scala.xml._

/**
 * This comet actor uses the Ask / Answer paradigm.
 * The class will only ever be instansiated in an "ask" block
 * therefore its appropriate to answer()
 */ 
class AskName extends CometActor {
  def render = {
    SHtml.ajaxForm(
      <div>What is your username?</div> ++
      SHtml.text("",name => answer(name.trim)) ++
      <input type="submit" value="Signin"/>)
  }
}
