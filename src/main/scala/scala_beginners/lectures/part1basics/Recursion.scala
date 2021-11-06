package scala_beginners.scala_beginners.lectures.part1basics

import scala.annotation.tailrec

object Recursion extends App {
  def factorial(n: Int): Int =
    if (n <= 1) 1
    else {
      println(s"Computing factorial of $n")
      val result = n * factorial(n - 1)
      println(s"Computing factorial of $n")
      result
    }

  // println(factorial(5000)) => STACKOVERFLOW err

  def anotherFactorial(n: Int): BigInt = {
    @tailrec // tail recursive
    def factHelper(x: Int, accumulator: BigInt): BigInt =
      if (x <= 1) accumulator
      else factHelper(x - 1, x * accumulator) // TAIL RECURSION = Use recursive call as the LAST expression

    factHelper(n, 1)
  }

    /*
      anotherFactorial(10) = factHelper(10, 1)
       = factHelper(9, 10 * 1)
       = factHelper(8, 9 * 10 * 1)
       = factHelper(7, 8 * 9 * 10 * 1)
       = ...
       = factHelper(2, 3 * 4 * ... * 10 * 1)
       = factHelper(1, 2 * 3 * ... * 10 * 1)
       = 1 * 2 * 3 * 4 * ... * 10 * 1
     */

  // println(anotherFactorial(5000))

  // WHEN YOU NEED LOOPS, USE _TAIL_ RECURSION

    /*
    1. Concatenate a string a times
    2. isPrime function tail recursive
    3. Fibonacci function, tail recursive
     */

  def concatFunc(n: Int, x: String): String = {
    def concatHelper(y: Int, acc: String): String = {
      if (y <= 1) acc
      else concatHelper(y - 1, acc + x)
    }

    concatHelper(n, x)
  }

  def concatTailRec(aString: String, n: Int, accumulator: String): String =
    if (n <= 0) accumulator
    else concatTailRec(aString, n - 1, aString + accumulator)

  println(concatFunc(5, "TEST_"))
  println(concatTailRec("Hello_", 3, ""))

  def isPrime(n: Int): Boolean = {
    @tailrec // optional
    def isPrimeTailrec(t: Int, isStillPrime: Boolean): Boolean = {
      if (!isStillPrime) false
      else if (t <= 1) true
      else isPrimeTailrec(t - 1, n % t != 0 && isStillPrime)
    }

    isPrimeTailrec(n / 2, true)
  }

  println(isPrime(2003))
  println(isPrime(629))

  def fibonacci(n: Int): Int = {
    def fiboTailrec(i: Int, last: Int, nextToLast: Int): Int =
      if (i >= n) last
      else fiboTailrec(i + 1, last + nextToLast, last)

    if (n <= 2) 1
    else fiboTailrec(2, 1, 1)
  }

  println(fibonacci(8))
}
