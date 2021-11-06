package scala_beginners.lectures.part2oop

import scala_beginners.playground.{Cinderella => Princess, PrinceCharming}

import java.util.Date
import java.sql.{Date => SqlDate}

object PackagingAndImports extends App {
  // package members are accesible by their simple name
  val writer = new Writer("Alex", "RockTheJVM", 2021)

  val princess = new Princess // playground.Cinderella = fully qualified name

  // packages are in hierarchy
  // matching folder structure

  // package object
  sayHello
  println(SPEED_OF_LIFHT)

  // imports
  val prince = new PrinceCharming

  // 1. use fully qualified name
  val date = new Date
  val sqlDate = new java.sql.Date(2021, 4, 26)

  // 2. aliases
  val sqlDate2 = new SqlDate(2021, 4, 26)

  // default imports
  // java.lang - String, Object, Exception
  // scala - Int, Nothing, Function
  // scala.Predef - println, ???
}
