import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { catchError } from 'rxjs/operators';
import { of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ResultsService {
  private resultsSubject: BehaviorSubject<any[]> = new BehaviorSubject<any[]>([]);
  public results$: Observable<any[]> = this.resultsSubject.asObservable();

  constructor(private http: HttpClient) {}

  loadResults(authToken: string): void {
    this.http.get<any[]>(`http://localhost:42300/lab4/api/main/results/${authToken}`).pipe(
      catchError(error => {
        console.error('Error fetching results:', error);
        return of([]);
      })
    ).subscribe(response => {
      this.resultsSubject.next(response); /
    });
  }

  submitPoint(x: number, y: number, r: number, authToken: string): Observable<any> {
    const payload = { x, y, r, userToken: authToken };
    return this.http.post('http://localhost:42300/lab4/api/main/submit-point', payload).pipe(
      catchError(error => {
        console.error('Error submitting point:', error);
        return of({ x, y, r, hit: false });
      })
    );
  }

  clearResults(authToken: string): Observable<void> {
    return this.http.delete<void>(`http://localhost:8080/lab4/api/main/clear/${authToken}`).pipe(
      catchError(error => {
        console.error('Error clearing results:', error);
        return of(void 0);
      })
    );
  }

  setResults(results: any[]): void {
    this.resultsSubject.next(results);
  }
}
