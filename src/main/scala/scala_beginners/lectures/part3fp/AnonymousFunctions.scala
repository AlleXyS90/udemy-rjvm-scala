package scala_beginners.lectures.part3fp

object AnonymousFunctions extends App {

  //  val doubler = new Function1[Int, Int] {
  //    override def apply(x: Int) = x * 2
  //  }

  // ANONYMOUS FUNCTION (LAMBDA)
  val doubler = (x: Int) => x * 2

  // multimple params in a lambda
  val adder = (a: Int, b: Int) => a + b
  // val added: (Int, Int) => Int

  val justDoSomething: () => Int = () => 3

  // careful
  println(justDoSomething) // function itself
  println(justDoSomething()) // call

  // curly braces with lambda
  val stringToInt = { (str: String) =>
    str.toInt
  }

  // MOAR syntactic sugar
  val niceIncrementer: Int => Int = _ + 1 // equivalent to x => x + 1

  val niceAdder: (Int, Int) => Int = _ + _ // equivalent to (a, b) => a + b

  /*
      1. MyList: replace all FunctionX calls with lambdas
      2. Rewrite the *special* adder as an anonymous function
   */

  val superAdd = (x: Int) => (y: Int) => x + y
  println(superAdd(3)(4))
}
