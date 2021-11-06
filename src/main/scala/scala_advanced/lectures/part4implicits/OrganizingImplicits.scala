package scala_advanced.lectures.part4implicits

object OrganizingImplicits extends App {

  implicit def reverseOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _)
//  implicit val normalOrdering: Ordering[Int] = Ordering.fromLessThan(_ < _)
  println(List(1,4,5,3,2).sorted)

  // scala.Predef

  /*
    Implicits (used as implicit parameters):
       - val/var
       - objects
       - accessor methods = defs with no parentheses: ex: reverseOrdering / not reverseOrdering()
   */

  // Exercise
  case class Person(name: String, age: Int)

  val persons = List(
    Person("Steve", 30),
    Person("Amy", 22),
    Person("John", 66)
  )

//  object Person {
//    implicit def alphabeticOrdering: Ordering[Person] = Ordering.fromLessThan((x, y) => x.name.compareTo(y.name) < 0)
//  }

//  implicit def ageOrdering: Ordering[Person] = Ordering.fromLessThan((x, y) => x.age < y.age)

//  println(persons.sorted)

  /*
    Implicit scope
    - normal scope = LOCAL SCOPE
    - imported scope
    - companions of all types involved in the method signature
      - List
      - Ordering
      - all the types involved = A or any supertype
   */

//   def sorted[B >: A](implicit ord: Ordering[B]): List[B]

  object AlphabeticNameOrdering {
    implicit def alphabeticOrdering: Ordering[Person] = Ordering.fromLessThan((x, y) => x.name.compareTo(y.name) < 0)
  }

  object AgeOrdering {
    implicit def ageOrdering: Ordering[Person] = Ordering.fromLessThan((x, y) => x.age < y.age)
  }

  import AgeOrdering._
  println(persons.sorted)

  /*
    Exercise

      - totalPrice = most used (70%)
      - by unit count = 20%
      - by unit price = 10%
   */

  case class Purchase(nUnits: Int, unitPrice: Double)
  object Purchase {
    implicit val totalPriceOrdering: Ordering[Purchase] = Ordering.fromLessThan((x, y) => x.nUnits * x.unitPrice < y.nUnits * y.unitPrice)
  }

  object UnitCountOrdering {
    implicit val unitCountOrdering: Ordering[Purchase] = Ordering.fromLessThan((x, y) => x.nUnits < y.nUnits)
  }

  object UnitPriceOrdering {
    implicit val unitPriceOrdering: Ordering[Purchase] = Ordering.fromLessThan((x, y) => x.unitPrice < y.unitPrice)
  }

}
