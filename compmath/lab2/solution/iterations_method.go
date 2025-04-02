package solution

import (
	"fmt"
	"lab2/funcs"
	"math"
)

func CalculateLambda(e funcs.Equation, a, b float64) float64 {

	maxDerivative := math.Max(math.Abs(e.FunctionDerivative(a)), math.Abs(e.FunctionDerivative(b)))
	if e.FunctionDerivative(a) > 0 && e.FunctionDerivative(b) > 0 {
		return -1 / maxDerivative
	} else {
		return 1 / maxDerivative
	}

}

func SimpleIterationMethod(e funcs.Equation, x0, eps float64, lambda float64) (float64, float64, int) {

	x := x0
	iter := 0

	for {
		fVal := e.Function(x)
		fmt.Printf("Iter = %d, x = %.6f, f(x) = %.6f\n", iter, x, fVal)

		xNext := x + lambda*fVal
		iter++

		if math.Abs(xNext-x) < eps && math.Abs(e.Function(xNext)) < eps {
			x = xNext
			break
		}

		x = xNext
		if iter > 1000 {
			fmt.Println("Превышено число итераций")
			break
		}
	}

	return x, e.Function(x), iter
}
