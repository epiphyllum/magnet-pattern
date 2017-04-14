trait Cookable[T] {
  type Meal
  def cook(t: T): Meal
}

object Kitchen {
  def cook[I](i: I)(implicit ev: Cookable[I]) : ev.Meal = ev.cook(i)
}

case class Fish()
case class FishDish()

object Fish {
  implicit object FishAreCookable extends Cookable[Fish] {
    override type Meal = FishDish
    override def cook(t: Fish): Meal = FishDish()
  }
}

Kitchen.cook(Fish())

case class Rice()

object FishAndRice {
  implicit val fishAndRiceAreCookable = new Cookable[(Fish, Rice)] {
    override type Meal = FishDish

    override def cook(t: (Fish, Rice)): Meal = FishDish()
  }
}

import FishAndRice._
Kitchen.cook(Fish(), Rice())