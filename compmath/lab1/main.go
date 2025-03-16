package main

import (
	"bufio"
	"fmt"
	"os"
	"strings"
)

var (
	Reader *bufio.Reader
)

func main() {
	Reader = bufio.NewReader(os.Stdin)

	fmt.Print("Выберите режим ввода:\n" +
		"f - ввод с файла\n" +
		"c - ввод с консоли\n")
	var (
		matrix  [][]float64
		b       []float64
		epsilon float64
		err     error
	)
	for {
		input, _ := Reader.ReadString('\n')
		input = strings.TrimSpace(input)

		if input == "c" {
			matrix, b, _, epsilon, err = InputMatrixFromConsole()
			break
		} else if input == "f" {
			fmt.Println("Порядок вводных данных в файле: размерность(n),точность,матрица коэффициентов (n x n), вектор свободных членов(b)")
			matrix, b, _, epsilon, err = InputMatrixFromFile()
			if err != nil {
				fmt.Println(err)
				continue
			}
			break
		} else {
			fmt.Println("Ошибка: введите 'c' для консольного ввода или 'f' для файла.")
		}
	}
	if err != nil {
		fmt.Println(err)
	}
	fmt.Println("До преобразования")
	for _, value := range matrix {
		for _, value1 := range value {
			fmt.Print(value1, " ")
		}
		fmt.Println()
	}
	mmatrix := make([][]float64, len(matrix))
	for index, value := range matrix {
		mmatrix[index] = make([]float64, len(matrix))
		copy(mmatrix[index], value)
	}
	matrix, b, err = MakeDiagonalDominant(matrix, b)
	if err != nil {
		fmt.Println(err)
		//return
	}
	fmt.Println("После преобразования")
	for _, value := range matrix {
		for _, value1 := range value {
			fmt.Print(value1, " ")
		}
		fmt.Println()
	}

	norm := CalculateNorm(matrix)
	fmt.Printf("Норма преобразованной матрицы равна %.6f\n", norm)

	solution, iters, inaccuracies := GaussZeidel(matrix, b, epsilon)

	fmt.Println("Решение системы")
	for i, x := range solution {
		fmt.Printf("x[%d] = %.9f\n", i+1, x)
	}
	fmt.Printf("\nЧисло итераций: %d\n", iters)
	fmt.Println("\nВектор погрешностей:")
	for i, e := range inaccuracies {
		fmt.Printf("e[%d] = %.9f\n", i+1, e)
	}
}
