package scala_beginners.lectures.part3fp

object TuplesAndMaps extends App {

  // tuples = finite ordered "lists"
  val aTuple = (2, "Hello Scala") // Tuple2[Int, String] = (Int, String)

  println(aTuple._1) // 2
  println(aTuple.copy(_2 = "goodbye Java"))
  println(aTuple.swap) // ("Hello Scala", 2)

  // Maps - keys -> values
  val aMap: Map[String, Int] = Map()

  val phoneBook = Map(("Jim", 555), ("Alex", 345)).withDefaultValue(-1)
  val phoneBook2 = Map("Jim" -> 555, "Alex" -> 345)
  // a -> b is sugar for (a, b)
  println(phoneBook)

  // map ops
  println(phoneBook.contains("Jim"))
  println(phoneBook("Marry"))

  // add a pairing
  val newPairing = "Marry" -> 678
  val newPhonebook = phoneBook + newPairing
  println(newPhonebook)

  // functionals on maps
  // map, flatMap, filter

  println(phoneBook.map(pair => pair._1.toLowerCase -> pair._2))

  // filterKeys
  println(phoneBook.view.filterKeys(x => x.startsWith("J")).toMap)

  // mapValues
  println(phoneBook.view.mapValues(number => number * 10).toMap)

  // conversions to other collections
  println(phoneBook.toList)
  println(List(("Alex", 555)).toMap)
  val names = List("Bob", "James", "Angela", "Marry", "Daniel", "Jim")
  println(names.groupBy(name => name.charAt(0)))

  /*
    1. What would happen if I had two original entries "Jim" -> 555 and "JIM" -> 900?
    2. Overly simplified social network based on maps
      Person = String
        - add a person to the network
        - remove
        - friend (mutual)
        - unfriend (mutual)

        - number of friends of a person
        - person with most friends
        - how many people have NO friends
        - if there is a social connection between two people (direct or not)
   */
}
