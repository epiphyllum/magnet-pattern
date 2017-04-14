type Rice = String
type Fish = String
type FishDish = String

trait IngredientsMagnet {
  type Meal
  def apply(): Meal
}
object Kitchen {
  def cook(i: IngredientsMagnet) : i.Meal = i()
}
object Rice {
  implicit def fromRiceAndFish(i: (Rice, Fish)) : IngredientsMagnet = new IngredientsMagnet {
    type Meal = FishDish
    override def apply(): Meal = s"${i._2} over ${i._1}"
  }
}
val fish: Fish = "Seabass"
val rice: Rice = "White rice"
import Rice._
Kitchen.cook((rice, fish)

