package eu.getintheloop.example.comet

import _root_.scala.actors._
import _root_.scala.actors.Actor._
// import _root_.net.liftweb._
import _root_.net.liftweb.http._
import _root_.net.liftweb.util._
import _root_.net.liftweb.util.Helpers._
import _root_.scala.xml._
// import S._
// import SHtml._
import _root_.net.liftweb.http.js._
import _root_.net.liftweb.http.js.JsCmds._
import _root_.net.liftweb.http.js.JE._
import _root_.net.liftweb.http.js.jquery.JqJsCmds._

class Chat extends CometActor with CometListener {
  private var userName = ""
  private var chats: List[ChatLine] = Nil
  private lazy val infoId = uniqueId + "_info"
  private lazy val infoIn = uniqueId + "_in"
  private lazy val inputArea = findKids(defaultXml, "chat", "input")
  private lazy val bodyArea = findKids(defaultXml, "chat", "body")
  private lazy val singleLine = deepFindKids(bodyArea, "chat", "list")

  // handle an update to the chat lists
  // by diffing the lists and then sending a partial update
  // to the browser
  override def lowPriority = {
    case ChatServerUpdate(value) =>
      val update = (value -- chats).reverse.map(b => AppendHtml(infoId, line(b)))
      partialUpdate(update)
      chats = value
  }

  // render the input area by binding the
  // appropriate dynamically generated code to the
  // view supplied by the template
  override lazy val fixedRender: Box[NodeSeq] =SHtml.ajaxForm(
    After(100, SetValueAndFocus(infoIn, "")),
    bind("chat", inputArea,
         "input" -> SHtml.text("", sendMessage _, "id" -> infoIn)
    )
  )

  // send a message to the chat server
  private def sendMessage(msg: String) = ChatServer ! ChatServerMsg(userName, msg.trim)

  // display a line
  private def line(c: ChatLine) = bind("list", singleLine,
                                       "when" -> hourFormat(c.when),
                                       "who" -> c.user,
                                       "msg" -> c.msg)

  // display a list of chats
  private def displayList(in: NodeSeq): NodeSeq = chats.reverse.flatMap(line)

  // render the whole list of chats
  override def render = {
    println("RENDERING COMET ACTOR CHAT")
    bind("chat", bodyArea,
         "name" -> userName, 
         AttrBindParam("id", Text(infoId), "id"),
         "list" -> displayList _)
  }

  // setup the component
  override def localSetup {
    askForName
    super.localSetup
  }

  // register as a listener
  def registerWith = ChatServer

  // ask for the user's name
  private def askForName {
    if (userName.length == 0) {
      ask(new AskName, "what's your username"){
        case s: String if (s.trim.length > 2) => {
          userName = s.trim
          println(userName)
          // if the name they supplied is longer
          // that two chars, tell the comet actor to
          // re-render itself
          reRender(true)
        }
        case _ => {
          // if they did anything else, we probally still need
          // there name so lets keep asking for it.
          askForName
          reRender(false)
        }
      }
    }
  }

}
