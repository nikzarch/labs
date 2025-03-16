package main

import (
	"math"
)

const (
	maxIterations = 1000
)

func GaussZeidel(matrix [][]float64, b []float64, epsilon float64) ([]float64, int, []float64) {
	n := len(matrix)
	x0 := make([]float64, 0)
	for i := 0; i < n; i++ {
		x0 = append(x0, b[i]/matrix[i][i])
	}
	//fmt.Println("Начальное приближение: ", x0)
	inaccuracies := make([]float64, 0)
	for iterations := 0; iterations <= maxIterations; iterations++ {

		diff := 0.0 // разница между итерациями
		for i := 0; i < n; i++ {
			s := 0.0
			for j := 0; j < i; j++ {
				s += matrix[i][j] * x0[j]
			}
			for j := i + 1; j < n; j++ {
				s += matrix[i][j] * x0[j]
			}
			x := (b[i] - s) / matrix[i][i]

			d := math.Abs(x - x0[i])
			if d > diff {
				diff = d
			}
			x0[i] = x
		}
		//fmt.Println("Итерация номер ", iterations+1, ": ", x0)
		//fmt.Println("Вектор погрешности:", diff)
		inaccuracies = append(inaccuracies, diff)
		if diff < epsilon {
			return x0, iterations + 1, inaccuracies
		}
	}
	return nil, maxIterations, inaccuracies
}
