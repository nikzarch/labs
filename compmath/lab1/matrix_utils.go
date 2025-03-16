package main

import (
	"errors"
	"math"
)

func MakeDiagonalDominant(matrix [][]float64, b []float64) ([][]float64, []float64, error) {
	n := len(matrix)
	used := make([]bool, n)
	newMatrix := make([][]float64, n)
	newB := make([]float64, n)
	for i := 0; i < n; i++ {
		var (
			maxValue float64
			maxIndex int
			flag     = true
		)
		for r := 0; r < n; r++ {
			if used[r] {
				continue
			}
			d := math.Abs(matrix[r][i])
			sum := 0.0
			for j := 0; j < n; j++ {
				if j != i {
					sum += math.Abs(matrix[r][j])
				}
			}

			if d > sum && d > maxValue {
				maxValue = d
				maxIndex = r
				flag = false // нашли оптимальное место
			}
		}
		if flag { // не нашли
			return matrix, b, errors.New("Данную матрицу не получилось привести к виду")
		}
		newMatrix[i] = make([]float64, n)
		copy(newMatrix[i], matrix[maxIndex])
		newB[i] = b[maxIndex]
		used[maxIndex] = true
	}
	return newMatrix, newB, nil
}
func CalculateNorm(matrix [][]float64) float64 {
	max := 0.0
	for i, row := range matrix {
		sum := 0.0
		for _, value := range row {
			sum += math.Abs(value)
		}
		diag := math.Abs(row[i])
		m := (sum - diag) / diag
		if m > max {
			max = m
		}
	}
	return max
}
