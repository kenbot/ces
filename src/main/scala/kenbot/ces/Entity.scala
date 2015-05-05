package kenbot.ces

import scala.reflect.ClassTag


object Entity {
  type Id = Symbol
  
  def apply(components: Component*) = new Entity(components.map(c => c.componentName -> c).toMap)
}

class Entity(val toMap: Map[Component.Name, Component]) {
  def components = toMap.values
  
  def apply(componentName: Component.Name): Option[Component] = toMap.get(componentName)
  
  def edit[C <: Component : ClassTag](f: C => Component): Entity = {
    get[C].fold(this) { c => 
      new Entity(toMap + (Component.nameFor[C] -> f(c)))
    }
  }
  
  def get[C <: Component : ClassTag]: Option[C] = 
    apply(Component.nameFor[C]).map(_.asInstanceOf[C])
  
  def has[C <: Component : ClassTag] = hasAll(Component.nameFor[C])
  
  def hasAll(componentNames: Component.Name*): Boolean = 
    componentNames.forall(cn => components exists (_.componentName == cn))
  
  def foreach(f: Component => Unit): Unit = toMap.values foreach f
  
  def map(f: Component => Component): Entity = new Entity(toMap mapValues f)
  
  def collect(pf: PartialFunction[Component, Component]): Entity = 
    new Entity(
      for ((key, comp) <- toMap if pf isDefinedAt comp) 
      yield key -> pf(comp))
  
  def :+(comp: Component) = new Entity(toMap + (comp.componentName -> comp))
  
  def ++(e: Entity) = new Entity(toMap ++ e.toMap)
  
  def -(componentName: Symbol) = new Entity(toMap - componentName)
  
  override def toString() = components.mkString("Entity(", ", ", ")")
}