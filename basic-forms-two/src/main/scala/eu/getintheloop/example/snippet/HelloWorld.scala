package eu.getintheloop.example.snippet

import scala.xml.{NodeSeq,Text}
import net.liftweb.http.{S,SHtml,RequestVar}
import net.liftweb.util.{Full,Box,Empty,Failure}
import net.liftweb.util.Helpers._
 
class HelloWorld {
  def howdy: NodeSeq = <span>Hello there!</span>
}
