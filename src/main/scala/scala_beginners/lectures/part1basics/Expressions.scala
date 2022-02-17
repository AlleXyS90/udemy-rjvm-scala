package scala_beginners.lectures.part1basics

object Expressions extends App {

  val x = 1 + 2 // EXPRESSION
  println(x)

  println(2 + 3 * 4)

  println(1 == x)

  println(!(1 == x))

  var aVariable = 2
  aVariable += 3
  println(aVariable)

  // Instructions (DO) vs Expressions

  // # IF expression
  val aCondition = true
  val aConditionedValue = if (aCondition) 5 else 3 // IF EXPRESSION
  println(aConditionedValue)
  println(if (aCondition) 5 else 3)

  var i = 0
  while (i < 10) {
    println(i)
    i += 1
  }
  // NEVER WRITE THIS AGAIN

  // EVERYTHING IN Scala is an Expression!

  val aWeirdValue = (aVariable = 3) // Unit === void
  println(aWeirdValue)

  // side effects: println(), whiles, reassigning (imperative programming)


  // # Code blocks

  val aCodeBlock = {
    val y = 2
    val z = y + 1

    if (z > 2) "hello" else "goodbye"
  }
  // code block is an expression

  // 1. difference between "hello world" vs println("hello world")?

  // 2. value of
  val someValue = {
    2 < 3
  } // true

  // 3. value of
  val someOtherValue = {
    if(someValue) 239 else 986
  } // 239

}
