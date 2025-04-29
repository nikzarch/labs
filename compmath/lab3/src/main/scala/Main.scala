object Main extends App {
  val func: FunctionDef = FunctionEvaluator.selectFunction()
  val a = scala.io.StdIn.readLine("Введите нижний предел интегрирования a: ").toDouble
  val b = scala.io.StdIn.readLine("Введите верхний предел интегрирования b: ").toDouble
  val tol = scala.io.StdIn.readLine("Введите требуемую точность: ").toDouble

  println(
    """
      |Выберите метод интегрирования:
      |1. Метод прямоугольников (левые)
      |2. Метод прямоугольников (правые)
      |3. Метод прямоугольников (средние)
      |4. Метод трапеций
      |5. Метод Симпсона
      |6. Все сразу
      |""".stripMargin)

  val methodChoice = scala.io.StdIn.readLine("Введите номер метода: ").trim

  println(f"Интегрирование функции ${func.expr} на отрезке [$a%.2f; $b%.2f] с точностью $tol%.1e")

  val integrator = new Integrator(func.func, a, b, tol)

  private def runMethod(methodName: String, integrationMethod: () => (Double, Int, Double)): Unit = {
    try {
      val (res, n, err) = integrationMethod()
      println(f"$methodName: Значение интеграла = $res%.10f, число разбиений = $n, погрешность = $err%.1e")
    } catch {
      case ex: RuntimeException =>
        println(s"$methodName: Ошибка — ${ex.getMessage}")
    }
  }

  methodChoice match {
    case "1" =>
      runMethod("Метод прямоугольников (левые)", () => integrator.rectangleMethod(Left))
    case "2" =>
      runMethod("Метод прямоугольников (правые)", () => integrator.rectangleMethod(Right))
    case "3" =>
      runMethod("Метод прямоугольников (средние)", () => integrator.rectangleMethod(Mid))
    case "4" =>
      runMethod("Метод трапеций", () => integrator.trapeziaMethod())
    case "5" =>
      runMethod("Метод Симпсона", () => integrator.simpsonMethod())
    case "6" =>
      runMethod("Метод прямоугольников (левые)", () => integrator.rectangleMethod(Left))
      runMethod("Метод прямоугольников (правые)", () => integrator.rectangleMethod(Right))
      runMethod("Метод прямоугольников (средние)", () => integrator.rectangleMethod(Mid))
      runMethod("Метод трапеций", () => integrator.trapeziaMethod())
      runMethod("Метод Симпсона", () => integrator.simpsonMethod())
    case _ =>
      println("Неверный выбор метода.")
  }
}
