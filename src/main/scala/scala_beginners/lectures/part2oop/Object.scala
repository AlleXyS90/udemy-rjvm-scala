package scala_beginners.lectures.part2oop

object Object {
  // SCALA DOES NOT HAVE CLASS-LEVEL FUNCTIONALITY ("static")
  // as in Java (below)

  object Person { // type + its only instance
    val N_EYES = 2
    def canFly: Boolean = false

    // factory method
    def apply(mother: Person, father: Person): Person =
      new Person("Bobbie")
  }
  class Person(val name: String) {
    // instance-level functionality
  }
  // COMPANIONS

  // or Object extends App
  def main(args: Array[String]): Unit = {
    println(Person.N_EYES)
    println(Person.canFly)

    // Scala object = SINGLETON INSTANCE
    val mary = new Person("Mary")
    val john = new Person("John")
    println(mary == john)

    val person1 = Person
    val person2 = Person
    println(person1 == person2)

    val bobbie = Person(mary, john)
  }

  // Scala Applications = Scala object with
  // def main(args: Array[String]): Unit

}


//public class JavaPlayground {
//  public static void main(String args[]) {
//    System.out.println("Hello, Java");
//
//    System.out.println(Person.N_EYES);
//  }
//}
//
//class Person {
//  public static final int N_EYES = 2;
//}
