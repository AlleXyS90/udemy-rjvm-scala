package scala_beginners.lectures.part2oop

object Inheritance extends App {

  // Single class inheritance
  class Animal {
    val creatureType = "wild"
    def eat = println("nomnomnom")
  }

  class Cat extends Animal {
    def crunch = {
      eat
      println("crunch crunch")
    }
  }

  val cat = new Cat
  // cat.eat
  cat.crunch

  // constructors
  class Person(name: String, age: Int) {
    def this(name: String) = this(name, 0)
  }
  class Adult(name: String, age: Int, idCard: String) extends Person(name)

  // overriding
  class Dog(override val creatureType: String) extends Animal {
    override def eat = {
      super.eat
      println("crunch crunch")
    }
//    override val creatureType = "domestic"
  }

  // OR

//  class Dog(dogType: String) extends Animal {
//    override val creatureType = dogType
//  }

  val dog = new Dog("K9")
  dog.eat
  println(dog.creatureType)

  // type substitution (broad: polymhorphism)
  val unknownAnimal: Animal = new Dog("K9")
  unknownAnimal.eat

  // overRIDING vs overLOADING

  // super

  // preventing overrides
  // 1 - use final keyword (final def eat in class Animal)
  // 2 - use final on the entire class
  // 3 - seal the class = can extend class in THIS FILE,
  // prevent extension in other classes (sealed class Animal)

}
