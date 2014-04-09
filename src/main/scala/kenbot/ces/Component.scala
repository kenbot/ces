package kenbot.ces

import scala.reflect.ClassTag

object Component {
  type Name = Symbol
  def nameForClass(c: Class[_]): Name = Symbol(c.getSimpleName)
  def nameFor[C <: Component](implicit ev: ClassTag[C]): Name = nameForClass(ev.runtimeClass)
}

trait Component extends Product {
  def componentName = Component nameForClass getClass
  def +(c: Component): Entity = Entity(this, c)
}
