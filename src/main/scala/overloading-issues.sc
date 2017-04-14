
class TypeErasure {
  def render(i: Set[Int]) : String = ???
  def render(j: Set[Double]) : String = ???
}


class MyClass {
  def render(i: Double) : String = i.toString
  def render(i: Int) : String = i.toString
}

val a = new MyClass
val f = a.render _

f(2)
f(3.0)
