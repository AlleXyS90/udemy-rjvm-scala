package scala_advanced.lectures.part2fp

object CurriesPAF extends App {

  // curried functions
  val superAdder: Int => Int => Int =
    x => y => x + y

  val add3 = superAdder(3) // Int => Int = y => 3 + y
  println(add3(5))
  println(superAdder(3)(5)) // curried function

  // METHOD !
  def curriedAdder(x: Int)(y: Int): Int = x + y //curried method

  // converting a method into a function values
  val add4: Int => Int = curriedAdder(4) // doesn't work if remove method type [Int => Int]

  // lifting = ETA-EXPANSION

  // functions != methods (JVM limitation)
  def inc(x: Int) = x + 1
  List(1,2,3).map(x => inc(x)) // ETA-expansion
  // .max(inc)

  // Partial function applications
  val add5 = curriedAdder(5) _ // underscore tell compiler to ETA-expansion: Int => Int

  // EXERCISE
  val simpleAddFunction = (x: Int, y: Int) => x + y
  def simpleAddMethod(x: Int, y: Int) = x + y
  def curriedAddMethod(x: Int)(y: Int) = x + y

  // add7: Int => It = y => 7 + y
  // as many different implementations of add7 using the above

  val add7 = (x: Int) => simpleAddFunction(7, x) // simplest
  val add7_2 = simpleAddFunction.curried(7)
  val add7_6 = simpleAddFunction(7, _: Int) // works as well

  val add7_3 = curriedAddMethod(7) _ // PAF (partial added function)
  val add7_4 = curriedAddMethod(7)(_) // PAF (alternative syntax)

  val add7_5 = simpleAddMethod(7, _: Int) // alternative syntax for turning methods into function values
                    // y => simpleAddMethod(7, y)

  // underscores are powerful
  def concatenator(a: String, b: String, c: String) = a + b + c
  val insertName = concatenator("Hello, I'm ", _: String, ", how are you?") // x: String => concatenator(Hello, x, how are you)
  println(insertName("Alex"))

  val fillInTheBlanks = concatenator("Hello, ", _: String, _: String) // (x, y) => concatenator("hello, ", x, y)
  println(fillInTheBlanks("Alex", " Scala is awesome"))

  //  EXERCISES
  /*
    #1. Process a list of numbers and return their string representations with different formats
      Use the %4.2f, %8.6g and %13.12f with a curried formatter function
   */
  // println("%4.2f".format(Math.PI))
  def curriedFormatter(s: String)(number: Double): String = s.format(number)
  val numbers = List(Math.PI, Math.E, 1, 9.8, 1.3e-12)
  val simpleFormat = curriedFormatter("%4.2f") _ // lift
  val seriousFormat = curriedFormatter("%8.6f") _
  val preciseFormat = curriedFormatter("%14.12f") _
  println(numbers.map(simpleFormat))
  println(numbers.map(curriedFormatter("%14.12f"))) // compiler does sweet eta-expansion for us

  /*
    #2. difference between
      - functions vs methods
      - parameters: by-name vs 0-lambda
   */
  def byName(n: => Int) = n + 1
  def byFunction(f: () => Int) = f() + 1

  def method: Int = 42
  def parenMethod(): Int = 42

  /*
    calling byName and byFunction
      - int
      - method
      - parentMethod
      - lambda
      - PAF
   */

  byName(23) // ok
  byName(method) // ok
  byName(parenMethod())
  byName(parenMethod) // ok but beware ==> byName(parenMethod())
  // byName(() => 42)  // not ok
  byName((() => 42)()) //ok
  // byName(parenMethod _) // not ok


  // byFunction(45) // not ok
  // byFunction(method ) // not ok!! does not do ETA-expansion!
  byFunction(() => parenMethod) // compiler does ETA-expansion
  byFunction(() => 45) // works
  byFunction(parenMethod _) // also works, but warning - unnecesarry

 }
