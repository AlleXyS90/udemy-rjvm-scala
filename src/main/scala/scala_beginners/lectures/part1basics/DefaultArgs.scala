package scala_beginners.lectures.part1basics

object DefaultArgs extends App {
  def trFact(n: Int, acc: Int = 1): Int =
    if (n <= 1) acc
    else trFact(n - 1, n * acc)

  val fact10 = trFact(10, 1)
  val fact12 = trFact(12, 2)
  val fact22 = trFact(22)

  def savePicture(format: String = "jpg", width: Int = 1920, height: Int = 1080): Unit =
    println("saving picture")

  savePicture("jpg", 800, 600)
  savePicture()

  /*
    1. pass in every loading argument
    2. name the arguments
   */

  savePicture(height = 600, width = 600, format = "bep")



}
