package bootstrap.liftweb

import _root_.net.liftweb.util.{Box,Full,Empty}
import _root_.net.liftweb.http._
import _root_.net.liftweb.sitemap._
import _root_.net.liftweb.sitemap.Loc._
import _root_.net.liftweb.util.Helpers._
import net.liftweb.mapper.{ConnectionManager,DB,ConnectionIdentifier,DefaultConnectionIdentifier}
import java.sql.{Connection, DriverManager}

class Boot {
  def boot {
    /**
     * Hook up a default database vendor if one is not supplied
     * via JNDI by the container or such.
     */
    DB.defineConnectionManager(DefaultConnectionIdentifier, DBVendor)
    
    LiftRules.addToPackages("eu.getintheloop.example")
    
    // Build SiteMap
    val entries = Menu(Loc("Home", List("index"), "Home")) :: Nil
    LiftRules.setSiteMap(SiteMap(entries:_*))
  }
}

/**
 * Example of a basic database vendor... of course you would
 * not hardcode your connection string, that would go in a 
 * properties file or similar.
 */
object DBVendor extends ConnectionManager {
 def newConnection(name: ConnectionIdentifier): Box[Connection] = {
   try {
     Class.forName("com.mysql.jdbc.Driver")
     val dm = DriverManager.getConnection("jdbc:mysql://localhost/name-of-your-database?user=root&password=mysql-password")
     Full(dm)
   } catch {
     case e : Exception => e.printStackTrace; Empty
   }
 }
 def releaseConnection(conn: Connection) {conn.close}
}
