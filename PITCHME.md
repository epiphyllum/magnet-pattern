#HSLIDE

### Magnet Pattern

Enlightened by a 2012 [post](http://spray.io/blog/2012-12-13-the-magnet-pattern/) by Mathias, creator of `Spray` or what is now `Akka Http`

#HSLIDE

### Test

The `Magnet Pattern` is an alternative to Scala method overloading that overcomes some of its issues.

This example of Scala method overloading overloads the `render` method:
```
   class MyClass {
      def render(i: Int) : String 
      def render(j: Double) : String
   }

```