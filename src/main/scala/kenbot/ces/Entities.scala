package kenbot.ces


import scala.reflect.ClassTag

object Entities {
  def apply(entities: (Entity.Id, Entity)*) = new Entities(entities.toMap)
}

class Entities(val toMap: Map[Entity.Id, Entity]) {
  

  class Having[Args](protected val having: List[Component.Name], toArgs: Entity => Args) {

    private def matches(e: Entity): Boolean = e.hasAll(having: _*)

    def and[B <: Component : ClassTag]: Having[Args] = 
      new Having(Component.nameFor[B] :: having, toArgs)
    
    def foreach(f: Args => Unit): Unit = 
      for ((_, e) <- toMap if matches(e)) {
        f(toArgs(e))
      }
    
    def map(f: Args => Entity): Entities = new Entities(
        for ((id, e) <- toMap if matches(e)) 
        yield id -> f(toArgs(e)))
    
    def filter(f: Args => Boolean): Entities = new Entities(
        for ((id, e) <- toMap if matches(e) && f(toArgs(e)))
        yield id -> e)
    
    def collect(f: PartialFunction[Args, Entity]): Entities = ???
    
    def edit[C <: Component : ClassTag](f: C => Component): Entities = 
      map(e => if (e.has[C]) e.edit[C](f) else e)
  }
  
  class Having0[A](having: List[Component.Name]) extends Having[A](having) {
    def and[B <: Component : ClassTag]: Having[(A,B)] = new Having[(A,B)](Component.nameFor[B] :: comps) {
      def and[C <: Component : ClassTag]: Having[(A,B,C)] = new Having[(A,B,C)](Component.nameFor[C] :: this.comps) {
        def and[D <: Component : ClassTag]: Having[(A,B,C,D)] = new Having[(A,B,C,D)](Component.nameFor[D] :: this.comps) {
          def and[E <: Component : ClassTag]: Having[(A,B,C,D,E)] = new Having[(A,B,C,D,E)](Component.nameFor[E] :: this.comps) {
            
          }
        }
      }
    }
  }
  
  /*
  class Focus[C <: Component](implicit C: ClassTag[C]) {
    def foreach(f: (Entity, C) => Unit): Unit = 
      having[C].foreach(e => f(e, e.get[C]))
      
    def map(f: (Entity, C) => Component): Entities = having[C].map(e => e collect {
      case C(comp) => f(e, comp)
      case x => x
    })
    
    def and[C2 <: Component : ClassTag] = new Focus2[C2]
    
    class Focus2[C2 <: Component](implicit C2: ClassTag[C2]) {
      def foreach(f: (Entity, C, C2) => Unit): Unit = 
        having[C].and[C2].foreach(e => f(e, e.get[C], e.get[C2]))
      
      def map(f: (Entity, C, C2) => Component): Entities = having[C].and[C2].map(e => e collect {
        case C2(comp) => f(e, comp)
        case x => x
      })
    }
  }
  
  def focus[C <: Component : ClassTag] = new Focus */

  def having[C <: Component : ClassTag]: Having = having(Component.nameFor[C])
  def having(compName0: Component.Name, compNames: Component.Name*): Having = new Having(compName0 :: compNames.toList)
  
  def entities = toMap.values
  
  def apply(id: Symbol): Entity = toMap(id)
  
  def update(id: Symbol, entity: Entity): Entities = new Entities(toMap + (id -> entity))
  
  def -(id: Symbol): Entities = new Entities(toMap - id)
  
  def :+(kv: (Entity.Id, Entity)): Entities = new Entities(toMap + kv)
    

  def filter(f: Entity => Boolean): Entities = collect {
    case entity if f(entity) => entity
  }
  
  def foreach(f: Entity => Unit): Unit = toMap.values foreach f

  def edit[C <: Component : ClassTag](f: C => C): Entities = 
    map(e => if (e.has[C]) e.edit[C](f) else e)
  
  def map(f: Entity => Entity) = new Entities(toMap mapValues f)

  def collect(pf: PartialFunction[Entity, Entity]): Entities = {
    new Entities(
      for ((key, entity) <- toMap if pf isDefinedAt entity) 
      yield key -> pf(entity))
  }
  
  override def toString() = toMap map {case (s,e) => s"${s.name} -> $e"} mkString ("Entities(\n  ", ",\n  ", ")")
}

