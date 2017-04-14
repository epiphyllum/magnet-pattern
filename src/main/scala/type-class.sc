trait CanCook[T] {
  type Meal
  def cook(t: T): Meal
}

object Kitchen {
  def cook[I](i: I)(implicit ev: CanCook[I]) : ev.Meal = ev.cook(i)
}

case class Fish()
case class FishDish()

object Fish {
  implicit object CanCookFish extends CanCook[Fish] {
    override type Meal = FishDish
    override def cook(t: Fish): Meal = FishDish()
  }
}

Kitchen.cook(Fish())

case class Rice()

object FishAndRice {
  implicit val canCookfishAndRice = new CanCook[(Fish, Rice)] {
    override type Meal = FishDish

    override def cook(t: (Fish, Rice)): Meal = FishDish()
  }
}

import FishAndRice._
Kitchen.cook(Fish(), Rice())