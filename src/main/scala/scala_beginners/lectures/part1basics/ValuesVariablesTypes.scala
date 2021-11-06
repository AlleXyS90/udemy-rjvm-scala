package scala_beginners.scala_beginners.lectures.part1basics

object ValuesVariablesTypes extends App {
  val x: Int = 42
  val y = 42 // type is optional
  println(x)

  // x = 2
  // VALS ARE IMMUTABLE
  // COMPILER can infer types

  val aString: String = "hello"
  val anotherString = "goodbye"

  val aBoolean: Boolean = false
  val aChar: Char = 'a'
  val anInt: Int = x // on 4 bytes
  val aShort: Short = 4613 // on 2 bytes
  val aLong: Long = 23434534545345345L // L - for Long values
  val aFloat: Float = 2.0f // f - for Float values
  val aDouble: Double = 3.14

  // variables
  var aVariable: Int = 4

  aVariable = 5 // side effects


}
