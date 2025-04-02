package solution

import (
	"errors"
	"lab2/funcs"
	"math"
)

func SecantMethod(e funcs.Equation, x0, epsilon float64) (float64, float64, int) {
	f := e.Function
	x1 := x0 + epsilon
	var x2 float64
	iterations := 0
	for {
		fx0 := f(x0)
		fx1 := f(x1)
		x2 = x1 - (x1-x0)*fx1/(fx1-fx0)
		iterations++

		if math.Abs(x2-x1) <= epsilon || math.Abs(f(x2)) <= epsilon {
			break
		}

		x0, x1 = x1, x2
	}
	return x2, f(x2), iterations
}

func CalculateX0(e funcs.Equation, a, b float64) (float64, error) {
	if e.Function(a)*e.FunctionSecondDerivative(a) > 0 {
		return a, nil
	} else if e.Function(b)*e.FunctionSecondDerivative(b) > 0 {
		return b, nil
	} else {
		return 0, errors.New("условие не выполняется")
	}
}
