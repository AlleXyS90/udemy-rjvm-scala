package scala_advanced.lectures.part3concurrency

//import scala.collection.parallel.CollectionConverters._
import scala.jdk.CollectionConverters

object ParallelUtils extends App {

  // 1 - parallel collections

// var parList = List(1,2,3).par
//  val aParVector = ParVector[Int](1,2,3)

  /*
  Seq
  Vector
  Array
  Map - Hash, Trie
  Set - Hash, Trie
   */

  def measure[T](operation: => T): Long = {
    val time = System.currentTimeMillis()
    operation
    System.currentTimeMillis() - time
  }

  val list = (1 to 10000).toList
  val serialTime = measure {
    list.map(_ + 1)
  }
  println("serial time " + serialTime)

//  val parallelTime = measure {
//    list.par.map(_ + 1)
//  }

  /*
    Map-reduce model
    - split the elements into chunks - Splitter
    - operation
    - recombine - Combiner
   */

  // map, flatMap, filter, foreach, reduce, fold

  // fold, reduce with non-associative operators
  println(List(1,2,3).reduce(_ -  _)) // => -4
//  println(List(1,2,3).par.reduce(_ - _)) => 2

  // synchronization
  var sum = 0
//  List(1,2,3).par.foreach(sum += _) // result (6) is not guaranted each time
  List(1,2,3).foreach(sum += _)
  println(sum)
}


