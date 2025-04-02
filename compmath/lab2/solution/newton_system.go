package solution

import (
	"errors"
	"lab2/funcs"
	"math"
)

func NewtonSystem(system funcs.System, x0, y0, eps float64) (float64, float64, int, []float64, []float64, [][]float64, error) {
	x, y := x0, y0
	iter := 0
	xs := []float64{x}
	ys := []float64{y}
	errs := [][]float64{}

	for {
		f1 := system.Equations[0](x, y)
		f2 := system.Equations[1](x, y)
		J := system.Jacobian(x, y)
		det := J[0][0]*J[1][1] - J[0][1]*J[1][0]
		if math.Abs(det) < 1e-12 {
			return x, y, iter, xs, ys, errs, errors.New("Матрица Якоби вырождена (детерминант близок к 0)")
		}
		dx := (-f1*J[1][1] + f2*J[0][1]) / det
		dy := (-J[0][0]*f2 + J[1][0]*f1) / det
		xNew := x + dx
		yNew := y + dy

		errN := []float64{dx, dy}
		errs = append(errs, errN)
		iter++

		xs = append(xs, xNew)
		ys = append(ys, yNew)
		if math.Max(math.Abs(dx), math.Abs(dy)) <= eps {
			x = xNew
			y = yNew
			break
		}
		if iter > 1000 {
			return x, y, iter, xs, ys, errs, errors.New("не сходится за 1000 итераций")
		}

		x, y = xNew, yNew
	}

	return x, y, iter, xs, ys, errs, nil
}
