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

### Standard Scala method overloading issue #1

```
   class MyClass {
      def render(i: Set[Int]) : String = ???
      def render(i: Set[Double]) : String = ???
   }

```
Type erasure makes both of these `render` methods the same: `def render(i: Set[_]) : String`

