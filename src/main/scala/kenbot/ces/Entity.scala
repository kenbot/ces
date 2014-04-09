package kenbot.ces

import scala.reflect.ClassTag


object Entity {
  def apply(components: Component*) = new Entity(components.map(c => c.componentName -> c).toMap)
}


class Entity(val toMap: Map[Symbol, Component]) {
  def components = toMap.values
  
  def apply(componentName: Symbol): Component = toMap(componentName)
  
  def edit[C <: Component : ClassTag](f: C => Component): Entity = 
    new Entity(toMap + (Component.nameFor[C] -> f(get[C])))
  
  def get[C <: Component : ClassTag]: C = apply(Component.nameFor[C]).asInstanceOf[C]
  
  def has[C <: Component : ClassTag] = hasAll(Component.nameFor[C])
  
  def hasAll(componentNames: Symbol*): Boolean = componentNames.forall(cn => components exists (_.componentName == cn))
  
  def foreach(f: Component => Unit): Unit = toMap.values foreach f
  
  def map(f: Component => Component): Entity = new Entity(toMap mapValues f)
  
  def collect(pf: PartialFunction[Component, Component]): Entity = 
    new Entity(
      for ((key, comp) <- toMap if pf isDefinedAt comp) 
      yield key -> pf(comp))
  
  def +(comp: Component) = new Entity(toMap + (comp.componentName -> comp))
  
  def ++(e: Entity) = new Entity(toMap ++ e.toMap)
  
  def -(componentName: Symbol) = new Entity(toMap - componentName)
  
  override def toString() = components.mkString("Entity(", ", ", ")")
}