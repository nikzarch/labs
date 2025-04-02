package main

import (
	"bufio"
	"errors"
	"fmt"
	"lab2/funcs"
	"lab2/solution"
	"lab2/util"
	"math"
	"os"
	"strconv"
	"strings"
)

var Reader = bufio.NewReader(os.Stdin)

func main() {
	for {
		fmt.Println("Выберите режим:\n1 - Решение нелинейного уравнения\n2 - Решение системы нелинейных уравнений")
		choice, err := readInt()
		if err != nil {
			fmt.Println("Некорректный ввод, попробуйте снова.")
			continue
		}

		switch choice {
		case 1:
			equation, err := chooseEquation()
			if err != nil {
				fmt.Println(err)
				continue
			}
			chooseAndInvokeMethod(equation)
		case 2:
			solveSystem()
		default:
			fmt.Println("Некорректный выбор")
		}
	}
}

func solveSystem() {
	fmt.Println("Выберите систему нелинейных уравнений:")
	for i, sys := range funcs.Systems {
		fmt.Printf("%d: %s\n", i+1, sys.Description)
	}

	choice, err := readInt()
	if err != nil || choice < 1 || choice > len(funcs.Systems) {
		fmt.Println("Некорректный ввод")
		return
	}
	system := funcs.Systems[choice-1]
	fmt.Println("Выбрана система:", system.Description)

	x0, y0, eps, err := readInitialApproximation()
	if err != nil {
		fmt.Println(err)
		return
	}

	xSol, ySol, iterations, xs, ys, errVec, err := solution.NewtonSystem(system, x0, y0, eps)
	if err != nil {
		fmt.Println("Ошибка решения:", err)
		return
	}

	printSystemSolution(xSol, ySol, iterations, xs, ys, errVec, system)
	fmt.Printf("f1(x,y) = %e \n", system.Equations[0](xSol, ySol))
	fmt.Printf("f2(x,y) = %e \n", system.Equations[1](xSol, ySol))
	util.PlotSystem(system, xSol-5, xSol+5)
}
func printSystemSolution(xSol, ySol float64, iterations int, xs, ys []float64, errVec [][]float64, system funcs.System) {
	fmt.Printf("Решение системы уравнений:\n")
	fmt.Printf("x = %.6f, y = %.6f\n", xSol, ySol)
	fmt.Printf("Количество итераций: %d\n", iterations)
	fmt.Println("Промежуточные значения:")
	fmt.Println("Итерация  |     x      |    y       |    dx      |  dy")
	for i := 0; i < iterations; i++ {
		fmt.Printf("%4d      | %.6f   | %.6f   | %.6f   | %.6f\n", i+1, xs[i], ys[i], errVec[i][0], errVec[i][1])
	}

	fmt.Printf("\nКонечное решение системы:\n")
	fmt.Printf("x = %.6f, y = %.6f\n", xSol, ySol)
}

func chooseAndInvokeMethod(equation funcs.Equation) {
	fmt.Println("Выберите метод для решения:\n1 - Метод хорд\n2 - Метод секущих\n3 - Метод простой итерации")
	choice, _ := readInt()

	a, b, epsilon, err := chooseABEpsilon()
	if err != nil {
		fmt.Println(err)
		return
	}
	if equation.Function(a)*equation.Function(b) >= 0 && equation.FunctionDerivative(a)*equation.FunctionDerivative(b) <= 0 {
		fmt.Println("не один корень на заданном интервал")
	} // done
	switch choice {
	case 1:
		solveWithMethod(equation, solution.HordesMethod, a, b, epsilon)
		util.PlotFunction(equation.Function, a, b)
	case 2:
		x0, err := solution.CalculateX0(equation, a, b)
		if err != nil {
			fmt.Println(err)
			return
		}
		solveWithMethod(equation, wrappedSecantMethod, x0, b, epsilon)
		util.PlotFunction(equation.Function, a, b)
	case 3:

		x0, err := solution.CalculateX0(equation, a, b)
		if err != nil {
			fmt.Println(err)
			return
		}
		//
		lambda := solution.CalculateLambda(equation, a, b)
		if !checkForSimpleIterationMethod(equation, a, b, lambda) {
			fmt.Println("не выполняется достаточное условие сходимости")
		}
		solveWithMethod(equation, wrappedSimpleIterationMethod, x0, lambda, epsilon)
		util.PlotFunction(equation.Function, a, b)
	default:
		fmt.Println("Некорректный выбор")
	}
}
func wrappedSimpleIterationMethod(e funcs.Equation, x0, lambda, eps float64) (float64, float64, int) {
	return solution.SimpleIterationMethod(e, x0, eps, lambda)
}
func wrappedSecantMethod(e funcs.Equation, x0, b, eps float64) (float64, float64, int) {
	return solution.SecantMethod(e, x0, eps)
}

func solveWithMethod(equation funcs.Equation, method func(funcs.Equation, float64, float64, float64) (float64, float64, int), a, b, epsilon float64) {
	x, y, iter := method(equation, a, b, epsilon)
	fmt.Printf("Корень: x = %.6f, y = %e, итераций: %d\n", x, y, iter)

}

func checkForSimpleIterationMethod(equation funcs.Equation, a, b, lambda float64) bool {
	maxValue := math.Inf(-1)

	for i := a; i <= b+0.1; i += 0.1 {
		maxValue = math.Max(maxValue, 1+math.Abs(lambda*equation.FunctionDerivative(i)))
	}
	fmt.Println(1+math.Abs(lambda*equation.FunctionDerivative(a)), 1+lambda*math.Abs(equation.FunctionDerivative(b)), maxValue)
	return maxValue < 1
}

func chooseEquation() (funcs.Equation, error) {
	fmt.Println("Выберите уравнение:")
	for i, eq := range funcs.Equations {
		fmt.Printf("%d : %s\n", i+1, eq.Description)
	}

	choice, err := readInt()
	if err != nil || choice < 1 || choice > len(funcs.Equations) {
		return funcs.Equation{}, errors.New("Некорректный ввод")
	}
	return funcs.Equations[choice-1], nil
}

func chooseABEpsilon() (float64, float64, float64, error) {
	fmt.Println("Введите границы интервала и эпсилон ")

	values, err := readFloats(3)
	if err != nil {
		return 0, 0, 0, err
	}
	return values[0], values[1], values[2], nil
}

func readInitialApproximation() (float64, float64, float64, error) {
	fmt.Println("Введите начальное приближение для x и y через пробел:")
	values, err := readFloats(2)
	if err != nil {
		return 0, 0, 0, err
	}
	fmt.Println("Введите точность eps:")
	epsValues, err := readFloats(1)
	if err != nil {
		return 0, 0, 0, err
	}
	return values[0], values[1], epsValues[0], nil
}

func readFloats(count int) ([]float64, error) {
	line, _ := Reader.ReadString('\n')
	parts := strings.Fields(line)
	if len(parts) != count {
		return nil, errors.New("Некорректный ввод")
	}
	result := make([]float64, count)
	for i, p := range parts {
		val, err := strconv.ParseFloat(p, 64)
		if err != nil {
			return nil, errors.New("Ошибка преобразования числа")
		}
		result[i] = val
	}
	return result, nil
}

func readInt() (int, error) {
	line, _ := Reader.ReadString('\n')
	return strconv.Atoi(strings.TrimSpace(line))
}
