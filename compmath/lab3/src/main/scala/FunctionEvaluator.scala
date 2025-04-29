import scala.annotation.tailrec
import scala.io.StdIn.readLine
sealed trait FunctionDef {
  def func(x: Double): Double
  def expr: String
}
case object Function1 extends FunctionDef{
  def func(x: Double): Double = -math.pow(x, 3) - math.pow(x, 2) + x + 3
  val expr: String = "-x^3 - x^2 + x + 3"
}
case object Function2 extends FunctionDef {
  def func(x: Double): Double = math.sin(x)
  val expr: String = "sin(x)"
}
case object Function3 extends FunctionDef {
  def func(x: Double) : Double = math.exp(x*x)
  val expr: String = "e^(x^2)"
}
case object Function4 extends FunctionDef {
  def func(x: Double) : Double = x*x
  val expr: String = "x^2"
}

object FunctionEvaluator {
  def selectFunction(): FunctionDef = {
    val functions = Seq(Function1, Function2, Function3, Function4)

    @tailrec
    def ask(): FunctionDef = {
      println("Выберите функцию для интегрирования:")
      functions.zipWithIndex.foreach { case (fn, idx) =>
        println(s"${idx + 1}: ${fn.expr}")
      }

      val input = readLine("Введите номер функции: ").trim
      val maybeFn = input.toIntOption.flatMap(i => functions.lift(i - 1))

      if (maybeFn.isDefined) maybeFn.get
      else {
        println("Неверный выбор, попробуйте ещё раз.")
        ask()
      }
    }

    ask()
  }
}