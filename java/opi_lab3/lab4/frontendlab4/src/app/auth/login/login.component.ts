import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  errorMessage: string = '';

  constructor(private fb: FormBuilder, private http: HttpClient, private router: Router) {}

  ngOnInit(): void {
    const token = localStorage.getItem('authToken');
      if (token) {
        this.router.navigate(['/main']);
      }
    this.loginForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  onSubmit(): void {
    if (this.loginForm.valid) {
      const formData = this.loginForm.value;

      this.http.post<any>('http://localhost:42300/lab4/api/auth/login', formData).subscribe(
        (response) => {
          const token = response.token;
          localStorage.setItem('authToken', token);
          this.router.navigate(['/main']);
          console.log("token was set");
        },
        (error) => {
          this.errorMessage = 'Invalid username or password';
        }
      );
    }
  }
}
