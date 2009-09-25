package eu.getintheloop.example.comet

import _root_.scala.actors.Actor
import _root_.scala.actors.Actor._
import _root_.net.liftweb.http._
import _root_.net.liftweb.util._
import _root_.net.liftweb.util.Helpers._
import _root_.net.liftweb.textile.TextileParser
import _root_.scala.xml.{NodeSeq, Text}
import _root_.java.util.Date

/**
 * A chat server.  It gets messages and returns them
 */
object ChatServer extends Actor with ListenerManager {
  private var chats: List[ChatLine] = Nil

  override def lowPriority = {
    case ChatServerMsg(user, msg) if msg.length > 0 =>
      chats ::= ChatLine(user, toHtml(msg), timeNow)
      chats = chats.take(50)
      updateListeners()

    case _ =>
  }

  def createUpdate = ChatServerUpdate(chats.take(15))

  /**
   * Convert an incoming string into XHTML using Textile Markup
   *
   * @param msg the incoming string
   *
   * @return textile markup for the incoming string
   */
  def toHtml(msg: String): NodeSeq = TextileParser.paraFixer(TextileParser.toHtml(msg, Empty))

  this.start
}

case class ChatLine(user: String, msg: NodeSeq, when: Date)
case class ChatServerMsg(user: String, msg: String)
case class ChatServerUpdate(msgs: List[ChatLine])

