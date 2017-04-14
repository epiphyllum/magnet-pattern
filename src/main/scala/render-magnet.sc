trait RenderMagnet {
  def apply() : String
}

class MyNewClass {
  def render(i: RenderMagnet) : String = i()
}

object RenderMagnet {
  implicit def intToMagnet(i: Int) = new RenderMagnet {
    override def apply() : String = i.toString
  }
  implicit def doubleToMagnet(i: Double) = new RenderMagnet {
    override def apply() : String = i.toString
  }
}

val a = new MyNewClass()
val i = 1
a.render(i)
a.render(3.0)