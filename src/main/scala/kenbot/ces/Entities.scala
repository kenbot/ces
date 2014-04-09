package kenbot.ces


import scala.reflect.runtime.universe.{Symbol => _, _}
import scala.reflect.ClassTag

object Entities {
  def apply(entities: (Symbol, Entity)*) = new Entities(entities.toMap)
}

class Entities(val toMap: Map[Symbol, Entity]) {
  
  class Having(comps: List[Symbol]) {
    def and[C <: Component : ClassTag]: Having = new Having(Component.nameFor[C] :: comps)
    
    private def matches(e: Entity): Boolean = e.hasAll(comps: _*)
    
    def foreach(f: Entity => Unit): Unit = 
      for ((_, e) <- toMap if matches(e)) {
        f(e)
      }
    
    def map(f: Entity => Entity): Entities = new Entities(
        for ((id, e) <- toMap if matches(e)) 
        yield id -> f(e))
    
    def filter(f: Entity => Boolean): Entities = new Entities(
        for ((id, e) <- toMap if matches(e) && f(e))
        yield id -> e)
    
    def collect(f: PartialFunction[Entity, Entity]): Entities = ???
    
    def edit[C <: Component : ClassTag](f: C => Component): Entities = 
      map(e => if (e.has[C]) e.edit[C](f) else e)
  }

  def having[C <: Component : ClassTag]: Having = having(Component.nameFor[C])
  def having(compName0: Symbol, compNames: Symbol*): Having = new Having(compName0 :: compNames.toList)
  
  def entities = toMap.values
  
  def apply(id: Symbol): Entity = toMap(id)
  
  def update(id: Symbol, entity: Entity): Entities = new Entities(toMap + (id -> entity))
  
  def -(id: Symbol): Entities = new Entities(toMap - id)
  
  def +(kv: (Symbol, Entity)): Entities = new Entities(toMap + kv)
    

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

