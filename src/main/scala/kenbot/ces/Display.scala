package kenbot.ces

import scala.swing._, Swing._
import java.awt.{Graphics2D, Color, GraphicsDevice, GraphicsEnvironment}

object Display extends scala.swing.SimpleSwingApplication {
  var game = Game(Game.entities, Nil)
  
  val top = new MainFrame {
    contents = new Panel {
      override def paintComponent(g: Graphics2D): Unit = {
        game.entities.using[AWTRender].and[Foo] { 
          case HList(e, render) => 
        }
        
//        game.entities.
//          having[Foo].  // having[Foo]
//          and[Bar].     // having Foo, Bar // edit[C](c) foreach(entity), map(entity)
//          focus[Boo].
//          and[Nar]// having Foo, Bar, Boo, Nar. Focus: Boo, Nar
//          .foreach((e, boo, nar) => () )
//          .map(e, boo, nar => e)
//          .collect((e, boo, bar) => e)
//          
//          focusE.foreach(entity, boo => )
//        
//        game.entities.having[AWTRender] foreachE { e => 
//          renderer(g, e)
//        }
      }
    }
    
    title = "Component Entity System game"
    size = java.awt.Toolkit.getDefaultToolkit.getScreenSize
  }
  
  val gameTimer = new javax.swing.Timer(40, ActionListener {_ =>
    game = game.run(new Input)
    top.repaint()
  })

  override def startup(args: Array[String]): Unit = {
    super.startup(args)
    gameTimer.start()
  }

  override def shutdown(): Unit = {
    gameTimer.stop()
    super.shutdown()
  }

}
