package eu.getintheloop.example.snippet

import scala.xml._
import net.liftweb.http.{S,SHtml,RequestVar}
import net.liftweb.util.{Full,Box,Empty,Failure}
import net.liftweb.util.Helpers._
 
class HelloWorld {
  object who extends RequestVar[Box[String]](Full("world"))
  
  def show(xhtml: NodeSeq): NodeSeq = bind("hello", xhtml,
    "whoField" -> SHtml.text(who.openOr(""), input => who(Full(input))),
    "submit" -> SHtml.submit(S.?("Send"), () => { 
      // note this could be another function, elsewhere, and usually is
      // but for the sake of understanding, we write it directly here.
      // print the value to console window when the submit button is pressed.
      println("Value of RequestVar: " + who.openOr(""))
    }),
    "who" -> Text(who.openOr(""))
  )
  
}
