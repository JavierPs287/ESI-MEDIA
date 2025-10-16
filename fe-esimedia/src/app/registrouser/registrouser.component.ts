import { Component,inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-registrouser',
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './registrouser.component.html',
  styleUrl: './registrouser.component.css'
})
export class RegisterComponent {

  fb = inject(FormBuilder);
  registerForm: FormGroup = this.fb.group({

    nombre: ['',[Validators.required, Validators.maxLength(25)]],
    apellido: ['',[Validators.required, Validators.maxLength(25)]],
    email: ['',[Validators.required, Validators.email]],
    alias: ['',[Validators.minLength(3), Validators.maxLength(20)]],
    cumpleanos: ['',[Validators.required, Validators.pattern(/^\d{4}-\d{2}-\d{2}$/)]],
    contrasena: ['',[Validators.required, Validators.minLength(8), Validators.maxLength(20)]],
    repetirContrasena: ['',[Validators.required, Validators.minLength(8), Validators.maxLength(20)]],
    fotoPerfil: [null]

  });

  onSubmit():void{
    console.log('Form submitted:', this.registerForm.value);
  }
}
