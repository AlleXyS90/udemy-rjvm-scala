package scala_beginners.lectures.part1basics

object CBNvsCBV extends App {

  def calledByValue(x: Long): Unit = {
    println("By value: " + x) // 2208834085267600
    println("By value: " + x) // 2208834085267600
  }

  def calledByName(x: => Long): Unit = {
    println("By name: " + x) // System.nanoTime()
    println("By name: " + x) // System.nanoTime()
  }

  calledByValue(System.nanoTime())
  calledByName(System.nanoTime())

  def infinite(): Int = 1 + infinite()
  def printFirst(x: Int, y: => Int) = println(x)

//  printFirst(infinite(), 34)
  printFirst(34, infinite())
}
