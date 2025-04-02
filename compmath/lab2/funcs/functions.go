package funcs

import "math"

type Equation struct {
	Description              string
	Function                 func(float64) float64
	FunctionDerivative       func(float64) float64
	FunctionSecondDerivative func(float64) float64
	A                        float64
	B                        float64
}

var Equations = [...]Equation{
	{
		Description:              "x^2 - 4x + 1",
		Function:                 func(x float64) float64 { return math.Pow(x, 2) - 4*x + 1 },
		FunctionDerivative:       func(x float64) float64 { return 2*x - 4 },
		FunctionSecondDerivative: func(x float64) float64 { return 2 },
		A:                        0,
		B:                        1,
	},
	{
		Description:              "e^x - 3x",
		Function:                 func(x float64) float64 { return math.Exp(x) - 3*x },
		FunctionDerivative:       func(x float64) float64 { return math.Exp(x) - 3 },
		FunctionSecondDerivative: func(x float64) float64 { return math.Exp(x) },
		A:                        0.1,
		B:                        1,
	},
	{
		Description:              "sin(x) - x/2",
		Function:                 func(x float64) float64 { return math.Sin(x) - x/2 },
		FunctionDerivative:       func(x float64) float64 { return math.Cos(x) - 1/2 },
		FunctionSecondDerivative: func(x float64) float64 { return -math.Sin(x) },
		A:                        -2,
		B:                        0,
	},
}

type System struct {
	Description string
	Equations   []func(x, y float64) float64
	Jacobian    func(x, y float64) [2][2]float64
}

var Systems = []System{
	{
		Description: "cos(x-1) + y - 0.5 = 0;   x - cos(y) - 3 = 0",
		Equations: []func(x, y float64) float64{
			func(x, y float64) float64 { return math.Cos(x-1) + y - 0.5 },
			func(x, y float64) float64 { return x - math.Cos(y) - 3 },
		},
		Jacobian: func(x, y float64) [2][2]float64 {
			return [2][2]float64{
				{-math.Sin(x - 1), 1},
				{1, math.Sin(y)},
			}
		},
	},
	{
		Description: "e^x + y - 3 = 0; x - y = 0",
		Equations: []func(x, y float64) float64{
			func(x, y float64) float64 { return math.Exp(x) + y - 3 },
			func(x, y float64) float64 { return x - y },
		},
		Jacobian: func(x, y float64) [2][2]float64 {
			return [2][2]float64{
				{math.Exp(x), 1},
				{1, -1},
			}
		},
	},
}
