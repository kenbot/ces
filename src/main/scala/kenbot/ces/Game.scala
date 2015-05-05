package kenbot.ces

import scala.reflect.ClassTag
import shapeless._


case object Player extends Component

case class Position(x: Double, y: Double) extends Component {
  def left(n: Double) = copy(x = x - n)
  def right(n: Double) = copy(x = x + n)
  def up(n: Double) = copy(y = y - n)
  def down(n: Double) = copy(y = y + n)
}

case class Name(name: String) extends Component

case class Health(health: Int) extends Component {
  def +(n: Int) = Health(health + n)
  def -(n: Int) = Health(health - n)
}

case class AWTRender(apply: (java.awt.Graphics2D, Entity) => Unit) extends Component

class Input

import Game.doall

object System {
  def onEntities(f: Entities => Entities): System = System((_, g) => g mapEntities f)
}

case class System(f: (Input, Game) => Game) {
  def apply(input: Input, game: Game): Game = f(input, game)
}



case class Game(entities: Entities, systems: List[System]) {
  def mapEntities(f: Entities => Entities) = Game(f(entities), systems)
  def run(input: Input): Game = (this /: systems)((game, sys) => sys(input, game))
}

object Game {
  
  val emptyGame = Game(Entities(), Nil)

  def genEntities(): Entities = {
    val rand = new java.util.Random
    def randInt(max: Int) = rand.nextInt(max)
    def genPos(max: Int) = Position(randInt(max), randInt(max))
    def genPlayer() = Entity(genPos(100), Health(100))
    val es = 1 to 100 map (n => Symbol(n.toString) -> genPlayer())
    Entities(es: _*)
  }
    
  val redDot = AWTRender {
    case (g,e) => 
      val Position(x,y) = e.get[Position]
      g.fillRect(x.toInt, y.toInt, 5, 5)
  }
  
  val entities = Entities(
    'player -> Entity(Player, Position(99,88), Name("Flapjack"), Health(10), redDot),
    'foo -> Entity(Name("foo"), Position(9,8), redDot))
    
  entities.having[Position].and[Health].edit[Position](_ up 5)
    

  def doall[S](state0: S)(fs: (S => S)*): S = 
    (state0 /: fs)((s, f) => f(s))
    
  doall(entities)(
    _.having[Position].map(_ + Health(66)),
    _.edit[Position](_ up 5),
    _.edit[Health](_ + 5))

  
    
  val x = Health :: Position :: HNil

  entities.edit[Position](_ left 5)
  entities.edit[Health](_ + 5)
    
}
