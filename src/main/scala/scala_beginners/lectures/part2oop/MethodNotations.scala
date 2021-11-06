package scala_beginners.lectures.part2oop

object MethodNotations extends App {
  class Person(val name: String,
               favoriteMovie: String,
               val age: Int = 0) {
    def likes(movie: String): Boolean =
      movie == favoriteMovie

    def +(person: Person): String =
      s"${this.name} is hanging out with ${person.name}"

    def +(nickname: String): Person = new Person(s"$name ($nickname)", favoriteMovie)

    def unary_! : String  = s"$name, what the heck!?"
    def unary_+ : Person = new Person(name, favoriteMovie, age + 1)
    def isAlive: Boolean = true
    def apply(): String = s"Hi, my name is $name and I like $favoriteMovie"
    def apply(n: Int): String = s"$name watched $favoriteMovie $n times"

    def learns(thing: String) = s"$name is learning $thing"
    def learnsScala = this learns "Scala"
  }

  val mary = new Person("Mary", "Inception")

  println(mary.likes("Inception"))
  println(mary likes "Inception") // equivalent
  // infix notation = operator notation / syntactic sugar
  // only works with methods with only one parameter

  // *operators* in Scala
  val tom = new Person("Tom", "Fight Club")

  println(mary + tom)
  println(mary.+(tom))

  println(1 + 2)
  println(1.+(2))

  // ALL OPERATORS ARE METHODS

  // Akka actors have ! ? operators

  // prefix notations
  val x = -1 // equivalent with 1.unary_-
  val y = 1.unary_-
  // unary_ prefix only works with - + ~ !

  println(!mary)
  println(mary.unary_!)

  // postfix notation
  println(mary.isAlive)
  // println(mary isAlive)

  // apply
  println(mary.apply())
  println(mary()) // equivalent

  /*
    1. Overload the + operator
      mary + "the rockstar" => new person "Marry (the rockstar)"

    2. Add an age to the Person class
      Add an unary + operator => new Person with the age + 1
      +mary => mary with the age incremented

    3. Add a "learns" method in the Person class => Marry learns Scala
      Add a learnsScala method, calls learns method with "Scala"
      Use it in postfix notations

    4. Overload the apply method
      mary.apply(2) => "Marry watched Inception 2 times"
   */

  // Hi, my name is Mary (the rockstar) and I like Inception
  println((mary + "the rockstar")())

  println((+mary).age)

  println(mary.learnsScala)

  // Mary watched Inception 10 times
  println(mary(10))
}
