#HSLIDE

### Magnet Pattern

Enlightened by a 2012 [post](http://spray.io/blog/2012-12-13-the-magnet-pattern/) by Mathias, creator of `Spray` or what is now `Akka Http`

#HSLIDE

### Magnet Pattern use case

The `Magnet Pattern` is an alternative to standard Scala method overloading that overcomes some of its issues.

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
