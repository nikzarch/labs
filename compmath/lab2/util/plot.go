package util

import (
	"fmt"
	"gonum.org/v1/plot"
	"gonum.org/v1/plot/plotter"
	"gonum.org/v1/plot/plotutil"
	"gonum.org/v1/plot/vg"
	"lab2/funcs"
	"math"
	"os/exec"
)

func openImage(filename string) {
	var cmd string
	var args []string
	cmd = "cmd"
	args = []string{"/c", "start", filename}
	exec.Command(cmd, args...).Start()
}
func PlotFunction(f func(float64) float64, a, b float64) {
	p := plot.New()
	p.Title.Text = "График функции"
	p.Y.Label.Text = "f(x)"
	p.Add(plotter.NewGrid())

	n := 500
	pts := make(plotter.XYs, n)
	step := (b - a) / float64(n-1)

	minY, maxY := math.Inf(1), math.Inf(-1)

	for i := 0; i < n; i++ {
		x := a + float64(i)*step
		y := f(x)
		pts[i].X = x
		pts[i].Y = y

		if y < minY {
			minY = y
		}
		if y > maxY {
			maxY = y
		}
	}

	line, err := plotter.NewLine(pts)
	if err != nil {
		panic(err)
	}
	p.Add(line)

	axisX := plotter.NewFunction(func(x float64) float64 { return 0 }) // Ось X (y = 0)
	axisX.Color = plotutil.Color(1)
	axisY := plotter.NewFunction(func(y float64) float64 { return 0 })
	axisY.Color = plotutil.Color(2)

	p.Add(axisX, axisY)

	filename := "graph.png"
	if err := p.Save(8*vg.Inch, 6*vg.Inch, filename); err != nil {
		panic(err)
	}
	fmt.Println("График сохранен в", filename)
	openImage(filename)
}
func PlotSystem(system funcs.System, a, b float64) {
	p := plot.New()
	p.Title.Text = "График системы уравнений"
	p.X.Label.Text = "x"
	p.Y.Label.Text = "y"
	p.Add(plotter.NewGrid())

	n := 1000
	pts1 := make(plotter.XYs, 0)
	pts2 := make(plotter.XYs, 0)
	step := (b - a) / float64(n-1)

	for x := a; x <= b; x += step {
		for y := a; y <= b; y += step {
			if math.Abs(system.Equations[0](x, y)) < 1e-2 {
				pts1 = append(pts1, plotter.XY{X: x, Y: y})
			}
			if math.Abs(system.Equations[1](x, y)) < 1e-2 {
				pts2 = append(pts2, plotter.XY{X: x, Y: y})
			}
		}
	}

	scatter1, err := plotter.NewScatter(pts1)
	if err != nil {
		return
	}
	scatter1.GlyphStyle.Color = plotutil.Color(0)
	scatter1.GlyphStyle.Radius = vg.Points(1)
	scatter2, err := plotter.NewScatter(pts2)
	if err != nil {
		return
	}
	scatter2.GlyphStyle.Color = plotutil.Color(1)
	scatter2.GlyphStyle.Radius = vg.Points(1)
	axisX := plotter.NewFunction(func(x float64) float64 { return 0 }) // Ось X (y = 0)
	axisX.Color = plotutil.Color(1)
	axisY := plotter.NewFunction(func(y float64) float64 { return 0 })
	axisY.Color = plotutil.Color(2)
	p.Add(axisX, axisY)
	p.Add(scatter1, scatter2)
	p.Legend.Add("f1(x, y) = 0", scatter1)
	p.Legend.Add("f2(x, y) = 0", scatter2)

	filename := "graphsystem.png"
	if err := p.Save(6*vg.Inch, 4*vg.Inch, filename); err != nil {
		panic(err)
	}
	fmt.Println("График системы сохранён в", filename)
	openImage(filename)
}
