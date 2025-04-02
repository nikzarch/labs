package solution

import (
	"lab2/funcs"
	"math"
)

func HordesMethod(e funcs.Equation, a, b, epsilon float64) (float64, float64, int) {

	var x float64
	iterations := 0
	f := e.Function

	for math.Abs(f(x)) > epsilon {
		//fmt.Println(iterations+1, x, f(x), math.Abs(b-a))
		x = a - (f(a)*(b-a))/(f(b)-f(a))
		if f(x)*f(a) < 0 { // разные знаки с а
			b = x
		} else {
			a = x
		}
		iterations++

	}

	return x, f(x), iterations
}
