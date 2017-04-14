#HSLIDE

### Magnet Pattern

Enlightened by a 2012 [post](http://spray.io/blog/2012-12-13-the-magnet-pattern/) by Mathias, creator of `Spray` or what is now `Akka Http`

#HSLIDE

### Magnet Pattern use case

The `Magnet Pattern` is an alternative to Scala method overloading that overcomes some of its issues.

Overloading the `render` method the standard way
```
class MyClass {
   def render(i: Int) : String = ??? 
   def render(i: Double) : String = ???
}

```

#HSLIDE

#### Standard Scala method overloading issue #1

```scala
class MyClass {
   def render(i: Set[Int]) : String = ???     // <= Compile Error!
   def render(i: Set[Double]) : String = ???  // <= Compile Error!
}

```

Type erasure makes both of these `render` methods the same: 
```def render(i: Set[_]) : String```

#HSLIDE

#### Standard Scala method overloading issue #2

Sometimes it's nice to lift a method into a function but this gets tricky with overloads:
```scala
class MyClass {
   def render(i: Int) : String = ???     
   def render(i: Double) : String = ???  
}
val a = new MyClass
val f = a.render _    // <= what did I just lift? 
f(2) // this worked out
f(3.0) // <= Compile Error!
```
#HSLIDE

### Magnet Pattern to the rescue!

Surprisingly simple:
```scala
trait RenderMagnet {
   def apply() : String 
}

class MyNewClass {
   def render(i: RenderMagnet) : String = i() 
}
```
Surely that didn't help _anything_, I want to render `Ints` and `Doubles` and they aren't `RenderMagnets`!

#HSLIDE

### implicits rescue the Magnet Pattern!

If we've got an `Int` and we need a `RenderMagnet` implicits can get us there:
```scala
object RenderMagnet {
   implicit def intToMagnet(i: Int) = new RenderMagnet {
      override def apply() : String = i.toString 
   }
   implicit def doubleToMagnet(i: Double) = new RenderMagnet {
      override def apply() : String = i.toString 
   }
}
```

#HSLIDE

### Putting it all together
```scala
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
a.render(1)
a.render(3.0)
```
And we're done.  Good night and good luck

#HSLIDE

### What the hell just happened? 
Rewind... 

```scala
val a = new MyNewClass()
a.render(1)   
```

* We invoke `MyNewClass.render` on an `Int` 
* But `render` takes a `RenderMagnet` as a parameter, not an `Int`
* The compiler goes searching for a conversion from `Int` to `RenderMagnet`...

#HSLIDE

### The compiler goes searching
* Looking for a `RenderMagnet` 
* Any implicits in `RenderMagnet`'s companion object are automatically in scope

```scala

object RenderMagnet {
   implicit def intToMagnet(i: Int) = new RenderMagnet {
      override def apply() : String = i.toString 
   }
   implicit def doubleToMagnet(i: Double) = new RenderMagnet {
      override def apply() : String = i.toString 
   }
}
```
Oh look!  we've got an implicit conversion from `Int` to `RenderMagnet`!

#HSLIDE

### Applying our implicit conversion
```scala
implicit def intToMagnet(i: Int) = new RenderMagnet { 
  override def apply() : String = i.toString 
}
```
* We get a `RenderMagnet` for `Ints`.  
* It has exactly one method and that's `apply`
* That `apply` method has closed over the `Int` `i` passed as a parameter to the implicit conversion.  
* When invoked it simply calls `toString` on that `i`

#HSLIDE

### The final step

Our original call passing an `Int`
```scala
a.render(1)
```
is now
```scala
a.render(RenderMagnet.intToMagnet(1))
```
And stripping away `a.render`, we have
```scala
RenderMagnet.intToMagnet(1).apply()   // result is the String "1"
```

#HSLIDE

### Another look at the full story
```scala
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
a.render(1)  // basically invokes RenderMagnet.intToMagnet(1).apply() 
a.render(3.0)
```
And we're done.  Good night and good luck

#HSLIDE

### There's more to the story

What if you want to cook up different things based on available ingredients?
```scala
trait IngredientsMagnet {
  type Meal
  def apply(): Meal 
}
```
* Same magnet pattern but `apply` returns a `Meal`
* Our trait has a `type` field called `Meal`
* Just like a `val` holds a value and a `def` holds a method, a `type` field holds a _type_

#HSLIDE

### Let's get in the kitchen

Putting our `IngredientsMagnet` to work:
```scala
trait IngredientsMagnet {
  type Meal
  def apply(): Meal 
}
object Kitchen {
   def cook(i: IngredientsMagnet) : i.Meal 
}
```
* `i.Meal`?
* Again just like any field in our trait, we can refer to it as `i.Meal` but we're referring to a _type_ 
* `Kitchen.cook` can now return a different type of `Meal` depending on what ingredients we provide

#HSLIDE

### Cooking with `Rice`

```scala
type Rice = ???
type RiceDish = ???
trait IngredientsMagnet {
  type Meal
  def apply(): Meal 
}
object Kitchen {
   def cook(i: IngredientsMagnet) : i.Meal = i()
}
object IngredientsMagnet {
   implicit def fromRice(i: Rice) : IngredientsMagnet = new IngredientsMagnet {
      type Meal = RiceDish 
      override def apply(): Meal = ??? // make some RiceDish from Rice
   }  
}
```

#HSLIDE

`IngredientsMagnet` knows too much. Let's move our implicit defs

```scala
object Rice {
   implicit def fromRice(i: Rice) : IngredientsMagnet = new IngredientsMagnet {
      type Meal = RiceDish 
      override def apply(): Meal = ??? // make some RiceDish from Rice
   }  
}
val rice : Rice = ...   
Kitchen.cook(rice)   // becomes Rice.fromRice(rice).apply()
```
* We're converting from `Rice` to `IngredientsMagnet`
* The compiler will search the companion objects of both types involved

#HSLIDE

### Boring meals

What if we want to cook with more than one ingredient?
```scala
Kitchen.cook(rice, fish)  // uh-oh!
```
Not to worry:
```scala
object RiceDishes {
  implicit def fromRiceAndFish(i: (Rice, Fish)) : IngredientsMagnet = new IngredientsMagnet {
     type Meal = FishDish
     override def apply(): Meal = ???
  }
}
```
We can get from `Rice` and `Fish` to a `Meal` of type `FishDish`

#HSLIDE

### Not so fast

* Our implicit conversion is from a tuple
* But we're not passing a tuple here!
```scala
Kitchen.cook(rice, fish) 
```
```scala
object RiceDishes {
  implicit def fromRiceAndFish(i: (Rice, Fish)) : IngredientsMagnet = 
     new IngredientsMagnet {
       type Meal = FishDish
       override def apply(): Meal = ???
  }
}
```
The compiler will try to find a conversion from a `Tuple` with the same types in the same order as we are passing

#HSLIDE

### Last step!

* The compiler searches the companion objects of the types involved
* Our implicit conversion is in the companion object of `RiceDishes`, not `(Rice, Fish)` or the magnet type
* We need to bring our implicit conversions into scope
```scala
import RiceDishes._
val rice : Rice = ???
val fish : Fish = ???
Kitchen.cook(rice, fish) // all good!
```

#HSLIDE

### Summary

Simple magnet:
```scala
trait SimpleMagnet { def apply() : String }
```
Cook up different return types magnet:
```scala
trait IngredientsMagnet { type Result; def apply(): Result }
object Kitchen { def cook(c: IngredientsMagnet) : c.Result }
```
Cook up multiple ingredients:
```scala
object Stuff { implicit def fromABC(c: (A, B, C): IngredientsMagnet = ??? }
import Stuff._
Kitchen.cook(a, b, c)

```

#HSLIDE

### Conclusions

* You've got a `cook` method that can cook _any_ inputs into _any_ output
* No worries over type erasure!
* When you lift `cook` you lift every overloaded version simultaneously
* You can keep extending what can be cooked indefinitely outside of the original class

#VSLIDE

### Addendum -- pure Type Class approach?
At the Scala meetup, Jacob asked what a more explicitly type-classy approach would look like

This would avoid creating a new magnet instance each time a we call `render` or `cook`
```scala
trait Cookable[T] {
  type Meal
  def cook(t: T): Meal
}
object Kitchen {
  def cook[I](i: I)(implicit ev: Cookable[I]) : ev.Meal = ev.cook(i)
}
```