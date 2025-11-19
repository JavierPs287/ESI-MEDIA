import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule} from '@angular/forms';
import { Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { FIELDS, DEPARTMENTS } from '../../constants/form-constants';
import { PHOTO_OPTIONS } from '../../constants/avatar-constants';
import { N } from '@angular/cdk/keycodes';

@Component({
  selector: 'app-edit-profiles',
  imports: [ CommonModule, ReactiveFormsModule ],
  templateUrl: './edit-profiles.component.html',
  styleUrl: './edit-profiles.component.css'
})
export class EditProfilesComponent {
  editedUser: any = {
    id: '123',
    role: 'creator', // 'user'|'creator'|'admin'
    nombre: 'Juan',
    apellidos: 'Pérez',
    alias: 'juanp',
    fechaNacimiento: '1990-01-01',
  };
  role: string = this.editedUser.role;
  isSelf: boolean = true; // si el usuario edita su propio perfil

  editMode = false;
  backup: any = {};
  fields = FIELDS;
  departments = DEPARTMENTS;
  photoOptions = PHOTO_OPTIONS;
  isVip = !!this.editedUser?.vip;
  showPhotoOptions = false;
  selectedPhoto: number | null = null;

  fb = inject(FormBuilder);
   editForm!: FormGroup;

  ngOnInit() {
    this.editForm = this.fb.group({
      name: [this.editedUser?.nombre || ''],
      lastName: [this.editedUser?.apellidos || ''],
      alias: [this.editedUser?.alias || ''],
      birthDate: [this.editedUser?.fechaNacimiento || '', this.minAgeValidator(4)],
      vip: [!!this.editedUser?.vip],
      description: [this.editedUser?.descripcion || ''],
      especialidad: [this.editedUser?.especialidad || ''],
      department: [this.editedUser?.departamento || ''],
      foto: [this.editedUser?.fotoUrl || ''],
    });

    // arrancamos en modo lectura: deshabilitamos todos los controles
    this.setReadMode();
  }

minAgeValidator(minAge: number) {
    return (control: AbstractControl): ValidationErrors | null => {
      if (!control.value) {
        return null;
      }
      const birthDate = new Date(control.value);
      const today = new Date();
      let age = today.getFullYear() - birthDate.getFullYear();
      const monthDiff = today.getMonth() - birthDate.getMonth();

      if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
        age--;
      }
      return age >= minAge ? null : { minAge: { requiredAge: minAge, actualAge: age } };
    };
  }

  //MANEJO ERRORES
getControl(controlName: string): AbstractControl | null {
  return this.editForm.get(controlName);
}

//metodos toggles
  toggleVip(): void {
    this.isVip = !this.isVip;
    this.editForm.get('vip')?.setValue(this.isVip);
  }

  togglePhotoOptions(): void {
    this.showPhotoOptions = !this.showPhotoOptions;
  }

  selectPhoto(imageID: number): void {
    this.selectedPhoto = imageID;
    this.editForm.get('imageId')?.setValue(imageID);
    this.showPhotoOptions = false;
  }

  // DESHABILITAR TODO para modo lectura
  private setReadMode() {
    this.editMode = false;
    this.backup = { ...this.editForm.getRawValue() }; // copia por si cancela
    Object.keys(this.editForm.controls).forEach(k => {
      this.editForm.get(k)!.disable({ emitEvent: false });
      this.editForm.get(k)!.clearValidators();
      this.editForm.get(k)!.updateValueAndValidity({ emitEvent: false });
    });
  }

  // INICIAR EDICIÓN: habilitar solo controles permitidos y añadir validadores dinámicos
  startEdit() {
    this.editMode = true;

    // primero deshabilitamos todo (estado limpio)
    Object.keys(this.editForm.controls).forEach(k => {
      const c = this.editForm.get(k)!;
      c.disable({ emitEvent: false });
      c.clearValidators();
      c.updateValueAndValidity({ emitEvent: false });
    });

    // campos comunes
    this.enable('name', [Validators.required, Validators.maxLength(50)]);
    this.enable('lastName', [Validators.required, Validators.maxLength(100)]);
    this.enable('alias', [Validators.minLength(3), Validators.maxLength(20)]);
    this.enable('foto');

    if (this.role === 'user') {
      this.enable('birthDate', [Validators.required, this.minAgeValidator(4)]);
      if (this.isSelf) this.enable('vip'); // VIP editable solo por sí mismo
    }

    if (this.role === 'creator') {
      this.enable('description', [Validators.maxLength(500)]);
      this.enable('especialidad', [Validators.required]);
      if (this.isSelf) this.enable('contraseña', [Validators.minLength(8)]);
    }

    if (this.role === 'admin') {
      this.enable('department', [Validators.required]);
      if (this.isSelf) this.enable('contraseña', [Validators.minLength(8)]);
    }

    // opción: también podrías pre-focar el primer input
  }

  private enable(name: string, validators: any[] = []) {
    const c = this.editForm.get(name);
    if (!c) return;
    c.setValidators(validators);
    c.enable({ emitEvent: false });
    c.updateValueAndValidity({ emitEvent: false });
  }

  cancelEdit() {
    // restaurar valores a los anteriores (backup)
    this.editForm.reset({ ...this.backup, contraseña: '' }, { emitEvent: false });
    this.setReadMode();
  }

  save() {
    // marcar para mostrar errores si los hay
    Object.keys(this.editForm.controls).forEach(k => {
      const c = this.editForm.get(k);
      if (c && !c.disabled) c.markAsTouched();
    });

    if (this.editForm.invalid) return;

    // CONSTRUIR PAYLOAD: solo controles habilitados
    const payload: any = { id: this.editedUser.id };
    Object.entries(this.editForm.controls).forEach(([k, ctrl]: any) => {
      if (!ctrl.disabled) payload[k] = ctrl.value;
    });

    // quitar contraseña vacía
    if (!payload.contraseña) delete payload.contraseña;

    // enviar al backend
    console.log('Payload a enviar:', payload);
    // this.userService.update(payload).subscribe(...)

    // actualizar vista local y volver a lectura
    this.editedUser = { ...this.editedUser, ...payload };
    // parchar el form con los valores guardados
    this.editForm.patchValue(this.editedUser);
    this.setReadMode();
  }

}
