package eu.getintheloop.example.comet

import _root_.scala.actors.Actor
import _root_.scala.xml.{Elem,NodeSeq,Text}
import _root_.net.liftweb.http.{ListenerManager,CometActor,CometListenee,SHtml}
import _root_.net.liftweb.http.js.JsCmds._

case class Messages(msgs : List[String])

object ChatServer extends Actor with ListenerManager { 
  // Global list of messages 
  private var msgs: List[String] = Nil 
  // The message that we'll send to all subscribers 
  protected def createUpdate = Messages(msgs) 
  // Process messages that we receive 
  override def highPriority = { 
    case s: String if s.length > 0 => 
      msgs ::= s 
      updateListeners() 
  } 
  this.start 
}

class Chat extends CometActor with CometListenee { 
  private var msgs: List[String] = Nil 
  def render = 
  <div> 
    <ul>{msgs.reverse.map(m => <li>{m}</li>)}</ul> 
    {SHtml.ajaxText("", s => {ChatServer ! s; Noop})} 
  </div> 
  protected def registerWith = ChatServer 
  override def highPriority = { 
    case Messages(m) => msgs = m ; reRender(false) 
  } 
}