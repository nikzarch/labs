import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { catchError } from 'rxjs/operators';
import { of, interval } from 'rxjs';



@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.css']
})
export class MainComponent {
  xOptions = [
    { label: '-2', value: -2 },
    { label: '-1.5', value: -1.5 },
    { label: '-1', value: -1 },
    { label: '-0.5', value: -0.5 },
    { label: '0.5', value: 0.5 },
    { label: '1', value: 1 },
    { label: '1.5', value: 1.5 },
    { label: '2', value: 2 }
  ];
  rOptions = [
    { label: '-2', value: -2 },
    { label: '-1.5', value: -1.5 },
    { label: '-1', value: -1 },
    { label: '-0.5', value: -0.5 },
    { label: '0.5', value: 0.5 },
    { label: '1', value: 1 },
    { label: '1.5', value: 1.5 },
    { label: '2', value: 2 }
  ];

  x: number | null = null;
  y: number = 0;
  r: number | null = null;
  errorMessage: string = '';
  results: any[] = [];
  pointsByR: { [key: number]: { x: number; y: number; hit: boolean }[] } = {};
  updateIntervalId!: number;


  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit(): void {
    const authToken = localStorage.getItem('authToken');
    if (!authToken) {
      console.error('Auth token is missing.');
      this.router.navigate(['/login']);
      return;
    }

    this.http.get<any[]>(`http://localhost:42300/lab4/api/main/results/${authToken}`).pipe(
      catchError((error) => {
        console.error('Error fetching results:', error);
        localStorage.removeItem('authToken');
        this.router.navigate(['/login']);
        return of([]);
      })
    ).subscribe((data) => {
      this.results = data;
      this.results.forEach(result => {
        const { x, y, r, hit } = result;
        this.savePointByR(x, y, r, hit);
      });
      console.log('Results fetched:', this.results);
    });
     this.updateIntervalId = window.setInterval(() => {
           this.fetchResults();
         }, 5000);
  }
  ngOnDestroy(): void {
      if (this.updateIntervalId) {
            clearInterval(this.updateIntervalId);
          }
    }
  fetchResults(): void {
      const authToken = localStorage.getItem('authToken');
      if (!authToken) {
        console.error('Auth token is missing.');
        this.router.navigate(['/login']);
        return;
      }
      this.http.get<any[]>(`http://localhost:42300/lab4/api/main/results/${authToken}`).pipe(
        catchError((error) => {
          console.error('Error fetching results:', error);
          localStorage.removeItem('authToken');
          this.router.navigate(['/login']);
          return of([]);
        })
      ).subscribe((data) => {
        this.results = data;
        this.results.forEach(result => {
          const { x, y, r, hit } = result;
          this.savePointByR(x, y, r, hit);
        });
        localStorage.setItem('results', JSON.stringify(this.results));
        console.log('Results fetched and updated:', this.results);
        this.drawPointsForR(this.r);
      });
    }
  onSubmit() {
    const authToken = localStorage.getItem('authToken');
        if (!authToken) {
          console.error('Auth token is missing.');
          this.router.navigate(['/login']);
          return;
        }
    if (this.x === null || this.r === null) {
      this.errorMessage = 'Пожалуйста, выберите значения для X и R';
      return;
    }

    this.errorMessage = '';
    const payload = { x: this.x, y: this.y, r: this.r, userToken: localStorage.getItem("authToken") };

    this.http.post('http://localhost:42300/lab4/api/main/submit-point', payload).subscribe(
      (response: any) => {
      console.log(response);
        this.results.push(response);
        const {x,y,r,hit} = response;
        this.drawPoint(x,y,r,hit);
        this.savePointByR(x, y, r, hit);

      },
      (error) => {
        this.errorMessage = 'Произошла ошибка при отправке данных';

        console.error(error);
      }
    );
  }

 onGraphClick(event: MouseEvent) {
   const authToken = localStorage.getItem('authToken');
   if (!authToken) {
     console.error('Auth token is missing.');
     this.router.navigate(['/login']);
     return;
   }

   const svgElement = document.getElementById('graph');
     if (!svgElement) {
       console.error('SVG element not found');
       return;
     }

      const rect = svgElement.getBoundingClientRect();

     const graphX = (event.clientX - rect.left - 250) / 200;
     const graphY = (250 - (event.clientY - rect.top)) / 200;

     if (this.r === null) {
       this.errorMessage = 'Выберите значение R перед использованием графика';
       return;
     }


     this.errorMessage = '';
     const normalizedX = graphX * this.r;
     const normalizedY = graphY * this.r;

     const payload = {
       x: normalizedX,
       y: normalizedY,
       r: this.r,
       userToken: localStorage.getItem("authToken")
     };
      console.log(payload);
     this.http.post('http://localhost:42300/lab4/api/main/submit-point', payload).subscribe(
       (response: any) => {
         console.log(response)
         response.x = response.x.toFixed(8);
         response.y = response.y.toFixed(8);
         this.results.push(response);
         this.drawPoint(normalizedX, normalizedY, this.r, response.hit);
         this.savePointByR(normalizedX, normalizedY, this.r, response.hit);
         localStorage.setItem('results', JSON.stringify(this.results));
       },
       (error) => {
         this.errorMessage = 'Произошла ошибка при обработке клика на графике';
         console.error(error);
       }
     );

 }
 updateGraph(r: number) {
   const svg = document.getElementById('graph-content') as unknown as SVGGElement;
   if (svg instanceof SVGGElement){
      if (this.r !== null && this.r < 0) {
             svg.style.transform = 'scale(-1, -1)';
             svg.style.transformOrigin = 'center';
           } else {
             svg.style.transform = 'scale(1, 1)';
             svg.style.transformOrigin = 'center';
             }
   }

 }

 drawPoint(xCoord: number, yCoord: number, r: number, hit: boolean): void {
   if (isNaN(xCoord) || isNaN(yCoord) || isNaN(r)) {
     return;
   }

   console.log(`Drawing point: x=${xCoord}, y=${yCoord}, r=${r}, hit=${hit}`);
   const svg = document.getElementById('graph');
   if (!svg) {
     console.error('Graph not found');
     return;
   }

   const point = document.createElementNS('http://www.w3.org/2000/svg', 'circle');

   const svgX = 250 + (xCoord / r) * 200;
   const svgY = 250 - (yCoord / r) * 200;


   point.setAttribute('cx', svgX.toString());
   point.setAttribute('cy', svgY.toString());
   point.setAttribute('r', '5');
   point.setAttribute('fill', hit ? 'green' : 'red');
   point.classList.add('graph-point');

   svg.appendChild(point);
 }


  savePointByR(x: number, y: number, r: number, hit: boolean): void {
    if (!this.pointsByR[r]) {
      this.pointsByR[r] = [];
    }
    this.pointsByR[r].push({ x, y, hit });
    console.log('Point saved for R=${r}: x=${x}, y=${y}, hit=${hit}');
  }

  onClear() {
      const authToken = localStorage.getItem('authToken');
          if (!authToken) {
            console.error('Auth token is missing.');
            this.router.navigate(['/login']);
            return;
          }

      this.http.delete(`http://localhost:42300/lab4/api/main/clear/${authToken}`).subscribe(
        response => {
          console.log('Results cleared successfully:', response);
          this.pointsByR= {};
          const svg = document.getElementById('graph');
          const existingPoints = svg.querySelectorAll('.graph-point');
          existingPoints.forEach(point => point.remove());
          this.results = [];
          localStorage.setItem('results', JSON.stringify(this.results));
        },
        error => {
          console.error('Error clearing results:', error);
          this.errorMessage = 'Ошибка при очистке данных';
        }
      );
    }

  onRChange(): void {
    const authToken = localStorage.getItem('authToken');
        if (!authToken) {
          console.error('Auth token is missing.');
          this.router.navigate(['/login']);
          return;
        }
    if (this.r !== null) {
      if (this.r < 0){
        console.log('updateGraph is invoked');
        this.updateGraph(this.r);
      }

      this.drawPointsForR(this.r);
    }
  }

  drawPointsForR(r: number): void {
    const svg = document.getElementById('graph');
    if (!svg) {
      console.error('graph not found');
      return;
    }

    const existingPoints = svg.querySelectorAll('.graph-point');
    existingPoints.forEach(point => point.remove());

    const points = this.pointsByR[r] || [];
    points.forEach(point => {
      this.drawPoint(point.x, point.y, r, point.hit);
    });
  }
  logout() {
      localStorage.removeItem('authToken');

      this.router.navigate(['/login']);
    }
}
