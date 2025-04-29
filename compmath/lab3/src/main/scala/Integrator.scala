class Integrator(f: Double => Double, a: Double, b: Double, tol: Double, initialN: Int = 4) {
  private val maxIterations = 20

  def rectangleMethod(variant: RectangleVariant): (Double, Int, Double) = {
    var n = initialN
    var I_n = rectangle(n, variant)
    val p = if (variant == Mid) 2 else 1
    var iteration = 0
    while (iteration < maxIterations) {
      val I_2n = rectangle(2 * n, variant)
      val err = math.abs(I_2n - I_n) / (math.pow(2, p) - 1)
      if (err < tol) return (I_2n, 2 * n, err)
      I_n = I_2n
      n *= 2
      iteration += 1
    }
    throw new RuntimeException("Превышено максимальное число итераций")
  }

  private def rectangle(n: Int, variant: RectangleVariant): Double = {
    val h = (b - a) / n
    (0 until n).map { i =>
      val x = variant match {
        case Left => a + i * h
        case Right => a + (i + 1) * h
        case Mid => a + (i + 0.5) * h
      }
      f(x)
    }.sum * h
  }

  def trapeziaMethod(): (Double, Int, Double) = {
    var n = initialN
    var I_n = trapezia(n)
    var iteration = 0
    while (iteration < maxIterations){
      val I_2n = trapezia(2 * n)
      val err = math.abs(I_2n - I_n) / (math.pow(2, 2) - 1)
      if (err < tol) return (I_2n, 2 * n, err)
      I_n = I_2n
      n *= 2
      iteration += 1
    }
    throw new RuntimeException("Превышено максимальное число итераций.")
  }

  private def trapezia(n: Int): Double = {
    val h = (b - a) / n
    (0 until n).map(i => f(a + i * h)).sum * h + (f(a) + f(b)) * h / 2
  }

  def simpsonMethod(): (Double, Int, Double) = {
    var n = if (initialN % 2 == 0) initialN else initialN + 1
    var I_n = simpson(n)
    var iteration = 0

    while (iteration < maxIterations) {
      val n2 = 2 * n
      val I_2n = simpson(n2)
      val err = math.abs(I_2n - I_n) / (math.pow(2, 4) - 1)
      if (err < tol) return (I_2n, n2, err)
      I_n = I_2n
      n = n2
      iteration += 1
    }
    throw new RuntimeException("Превышено максимальное число итераций.")
  }

  private def simpson(n: Int): Double = {
    val h = (b - a) / n
    val terms = (1 until n).map { i =>
      val x = a + i * h
      if (i % 2 == 0) 2 * f(x) else 4 * f(x)
    }
    (f(a) + f(b) + terms.sum) * h / 3
  }
}
