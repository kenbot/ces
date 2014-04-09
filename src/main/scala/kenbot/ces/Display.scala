package kenbot.ces

import scala.swing._
import java.awt.{Graphics2D, Color}

class Display extends scala.swing.SimpleSwingApplication {
  var game = Game.emptyGame
  
  val top = new MainFrame {
    contents = new Panel {
      override def paintComponent(g: Graphics2D): Unit = {
        g.setColor(Color.red)
        game.entities.having[AWTRender] foreach { e => 
          e.get[AWTRender].render(g, e)
        }
      }
    }
  }
}


