package scala_beginners.lectures.part3fp

object MapFlatMapFilterFor extends App {

  val list = List(1, 2, 3)
  println(list.head)
  println(list.tail)

  // map
  println(list.map(_ + 1))

  // filter
  println(list.filter(_ % 2 == 0))

  // flatMap
  val toPair = (x: Int) => List(x, x + 1)
  println(list.flatMap(toPair))

  // print all combinations between two lists
  val numbers = List(1, 2, 3, 4)
  val chars = List('a', 'b', 'c', 'd')
  val colors = List("black", "white")
  // List("a1", "a2", ...)

  val addDigit = (x: Int) => chars.map(c => s"$c$x")
  val result = numbers.flatMap(addDigit)

  println(result)

  val combinations = numbers
    .flatMap(n => chars
      .flatMap(c => colors
        .map(color => s"$c$n-$color")))

  println(combinations)

  // foreach
  list.foreach(println)

  // for-comprehensions
  val forCombinations = for {
    n <- numbers if n % 2 == 0
    c <- chars
    color <- colors
  } yield s"$c$n-$color"

  println(forCombinations)

  for {
    n <- numbers
  } println(n)

  // syntax overload
  list.map {
    x => x * 2
  }

  /*
    1. MyList supports for comprehensions?
      map(f: A => B) => MyList[B]
      filter(p: A => Boolean) => MyList[A]
      flatMap(f: A => MyList[B]) => MyList[B]
    2. A small collection of at most ONE element - Maybe[+T]
      - map, flatMap, filter
   */

}
