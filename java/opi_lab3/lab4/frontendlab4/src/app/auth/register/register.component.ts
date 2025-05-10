import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {
  registerForm: FormGroup;
  errorMessage: string = '';

  constructor(private fb: FormBuilder, private http: HttpClient, private router: Router) {}

  ngOnInit(): void {
    this.registerForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required],
      confirmPassword: ['', Validators.required]
    });
  }

  onSubmit(): void {

    if (this.registerForm.valid) {
      const password = this.registerForm.get('password')?.value;
      const confirmPassword = this.registerForm.get('confirmPassword')?.value;
      if (password !== confirmPassword) {
              this.errorMessage = "Passwords must be equal";
              return;
        }

      const formData = {
            username: this.registerForm.get('username')?.value,
            password: password
          };

      this.http.post<any>('http://localhost:42300/lab4/api/auth/register', formData).subscribe(
        (response) => {
          const token = response.token;
          localStorage.setItem('authToken', token);
          this.router.navigate(['/main']);
          console.log("token was set");
        },
        (error) => {
          this.errorMessage = 'Registration error';
        }
      );
    }
  }
}
