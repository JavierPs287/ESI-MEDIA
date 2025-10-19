import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-loginuser',
  imports: [ReactiveFormsModule, CommonModule, RouterLink],
  templateUrl: './loginuser.component.html',
  styleUrl: './loginuser.component.css'
})
export class LoginuserComponent {
  fb = inject(FormBuilder);
  loginForm: FormGroup = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    contrasena: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(20)]],
  });

  onSubmit(): void {
    console.log('Form submitted:', this.loginForm.value);
  }
}
