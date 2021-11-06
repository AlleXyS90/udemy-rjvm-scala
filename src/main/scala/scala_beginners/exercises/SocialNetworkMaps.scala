package scala_beginners.exercises

import scala.annotation.tailrec

object SocialNetworkMaps extends App {

  /*
    PART 3 - Tuples and Maps

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

  // solutions:
  // # 1.
  // 900 will override previous value for the same key
  val map = Map("Jim" -> 555, "Jim" -> 900)
  println(map)

  // # 2.
  val persons: Map[String, Set[String]] = Map.empty

  def add(network: Map[String, Set[String]], person: String): Map[String, Set[String]] =
    network + (person -> Set())


  def remove(network: Map[String, Set[String]], person: String): Map[String, Set[String]] = {
    def removeAux(friends: Set[String], networkAcc: Map[String, Set[String]]): Map[String, Set[String]] =
      if (friends.isEmpty) networkAcc
      else removeAux(friends.tail, unfriend(networkAcc, person, friends.head))

    val unfriended = removeAux(network(person), network)

    unfriended - person
  }

  def friend(network: Map[String, Set[String]], a: String, b: String): Map[String, Set[String]] = {
    val friendsA = network(a)
    val friendsB = network(b)

    network + (a -> (friendsA + b)) + (b -> (friendsB + a))
  }

  def unfriend(network: Map[String, Set[String]], a: String, b: String): Map[String, Set[String]] = {
    val friendsA = network(a)
    val friendsB = network(b)

    network + (a -> (friendsA - b)) + (b -> (friendsB - a))
  }


  // tests
  val empty: Map[String, Set[String]] = Map()
  val network = add(add(empty, "Bob"), "Marry")

  println(network)
  println(friend(network, "Bob", "Marry"))
  println(unfriend(friend(network, "Bob", "Marry"), "Bob", "Marry"))
  println(remove(friend(network, "Bob", "Marry"), "Bob"))

  // Jim, Bob, Marry
  val people = add(add(add(empty, "Bob"), "Marry"), "Jim")
  val jimBob = friend(people, "Bob", "Jim")
  val testNetwork = friend(jimBob, "Bob", "Marry")

  println(testNetwork)

  def nFriends(network: Map[String, Set[String]], person: String): Int =
    if (!network.contains(person)) 0
    else network(person).size

  println(nFriends(testNetwork, "Bob"))

  def mostFriends(network: Map[String, Set[String]]): String =
    network.maxBy(pair => pair._2.size)._1

  println(mostFriends(testNetwork))

  def nPeopleWithNoFriends(network: Map[String, Set[String]]): Int =
    network.count(pair => pair._2.isEmpty)

  println(nPeopleWithNoFriends(testNetwork))

  def socialConnection(network: Map[String, Set[String]], a: String, b: String): Boolean = {
    @tailrec
    def bfs(target: String, consideredPeople: Set[String], discoveredPeople: Set[String]): Boolean = {
      if (discoveredPeople.isEmpty) false
      else {
        val person = discoveredPeople.head
        if (person == target) true
        else if (consideredPeople.contains(person))
          bfs(target, consideredPeople, discoveredPeople.tail)
        else bfs(target, consideredPeople + person, discoveredPeople.tail ++ network(person))
      }
    }

    bfs(b, Set(), network(a) + a)
  }

  println(socialConnection(testNetwork, "Marry", "Jim"))
  println(socialConnection(network, "Marry", "Bob"))



}
