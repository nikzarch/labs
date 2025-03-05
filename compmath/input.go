package main

import (
	"bufio"
	"fmt"
	"os"
	"strconv"
	"strings"
)

func InputMatrixFromConsole() ([][]float64, []float64, int, float64, error) {

	fmt.Println("Введите размерность матрицы (n ≤ 20):")
	var n int
	for {
		input, _ := Reader.ReadString('\n')
		input = strings.TrimSpace(input)
		value, err := strconv.Atoi(input)
		if err != nil || value <= 0 || value > 20 {
			fmt.Println("Ошибка: введите целое число от 1 до 20.")
			continue
		}
		n = value
		break
	}

	matrix := make([][]float64, n)
	b := make([]float64, n)

	fmt.Println("Введите коэффициенты матрицы: по ", n, " чисел в строке")
	for i := 0; i < n; i++ {
		for {
			fmt.Printf("Строка %d:", i)
			line, _ := Reader.ReadString('\n')
			numbers := strings.Fields(line)

			if len(numbers) != n {
				fmt.Println("Введите ровно ", n, " чисел")
				continue
			}
			row := make([]float64, n)
			flag := true
			for j, value := range numbers {
				num, err := strconv.ParseFloat(value, 64)
				if err != nil {
					fmt.Println("Неверный формат числа")
					flag = false
					break
				}
				row[j] = num
			}
			if flag {
				matrix[i] = row
				break
			}
		}
	}

	fmt.Println("Введите вектор свободных членов")
	for {
		line, _ := Reader.ReadString('\n')
		numbers := strings.Fields(line)
		row := make([]float64, n)
		if len(numbers) != n {
			fmt.Println("Введите ровно ", n, " чисел")
			continue
		}
		flag := true
		for j, value := range numbers {
			num, err := strconv.ParseFloat(value, 64)
			if err != nil {
				fmt.Println("Неверный формат числа")
				flag = false
				break
			}
			row[j] = num
		}
		if flag {
			b = row
			break
		}
	}

	fmt.Println("Введите точность (пример: 0.0001)")
	var epsilon float64
	for {
		line, _ := Reader.ReadString('\n')
		value := strings.TrimSpace(line)
		num, err := strconv.ParseFloat(value, 64)
		if err != nil || num <= 0 {
			fmt.Println("Введите положительное число")
			continue
		}
		epsilon = num
		break
	}

	return matrix, b, n, epsilon, nil
}
func InputMatrixFromFile() ([][]float64, []float64, int, float64, error) {
	fmt.Println("Введите путь к файлу")
	var filePath string
	fmt.Scanln(&filePath)

	file, err := os.Open(filePath)
	if err != nil {
		return nil, nil, 0, 0, fmt.Errorf("Ошибка открытия файла: %v", err)
	}
	defer file.Close()

	scanner := bufio.NewScanner(file)
	if !scanner.Scan() {
		return nil, nil, 0, 0, fmt.Errorf("Ошибка чтения размерности")
	}
	n, err := strconv.Atoi(strings.TrimSpace(scanner.Text()))
	if err != nil || n <= 0 || n > 20 {
		return nil, nil, 0, 0, fmt.Errorf("Ошибка: размерность должна быть числом от 1 до 20")
	}
	if !scanner.Scan() {
		return nil, nil, 0, 0, fmt.Errorf("Ошибка чтения точности")
	}
	epsilon, err := strconv.ParseFloat(strings.TrimSpace(scanner.Text()), 64)
	if err != nil || epsilon <= 0 {
		return nil, nil, 0, 0, fmt.Errorf("Ошибка: точность должна быть положительным числом")
	}
	matrix := make([][]float64, n)
	for i := 0; i < n; i++ {
		if !scanner.Scan() {
			return nil, nil, 0, 0, fmt.Errorf("Ошибка: недостаточно строк в матрице")
		}
		line := strings.Fields(scanner.Text())
		if len(line) != n {
			return nil, nil, 0, 0, fmt.Errorf("Ошибка: в строке %d должно быть %d чисел", i+1, n)
		}

		matrix[i] = make([]float64, n)
		for j, value := range line {
			num, err := strconv.ParseFloat(value, 64)
			if err != nil {
				return nil, nil, 0, 0, fmt.Errorf("Ошибка в строке %d: неверное число %s", i+1, value)
			}
			matrix[i][j] = num
		}
	}
	b := make([]float64, n)
	if !scanner.Scan() {
		return nil, nil, 0, 0, fmt.Errorf("Ошибка: недостаточно элементов в векторе свободных членов")
	}
	line := strings.Fields(scanner.Text())
	if len(line) != n {
		return nil, nil, 0, 0, fmt.Errorf("Ошибка: в строке  должно быть %d чисел", n)
	}

	for index, value := range line {
		num, err := strconv.ParseFloat(value, 64)
		if err != nil {
			return nil, nil, 0, 0, fmt.Errorf("Ошибка в строке %d: неверное число %s", index+1, value)
		}
		b[index] = num
	}

	return matrix, b, n, epsilon, nil
}
