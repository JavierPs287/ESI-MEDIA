import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule} from '@angular/forms';
import { Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { FIELDS, DEPARTMENTS } from '../../constants/form-constants';
import { AVATAR_OPTIONS } from '../../constants/avatar-constants';
import { N } from '@angular/cdk/keycodes';
import { Admin, Creator, Usuario } from '../../models/user.model';
import { UserService } from '../../services/user.service';
import { Router, RouterLink } from '@angular/router';
import { getAvatarUrlById } from '../../services/image.service';

@Component({
  selector: 'app-edit-profiles',
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './edit-profiles.component.html',
  styleUrl: './edit-profiles.component.css'
})
export class EditProfilesComponent {
  private readonly userService = inject(UserService);
  private readonly router = inject(Router);
  editedUser!: Usuario | Creator | Admin | null;

  fb = inject(FormBuilder);
   editForm!: FormGroup;

  editMode = false;
  backup: any = {};
  fields = FIELDS;
  // Datos comunes
  name: string = '';
  lastName: string = ''
  email: string = '';
  imageId: number | null = null;
  // Datos usuario y creador
  alias: string = '';
  // Datos usuario
  vip: string = '';
  birthDate: string = '';
  // Datos creador
  description: string = '';
  field: string = '';
  type : string = '';
  // Datos admin
  department: string = '';

  isVip = false;
  role: string = '';
  departments = DEPARTMENTS;
  photoOptions = AVATAR_OPTIONS;
  showPhotoOptions = false;
  selectedPhoto: number | null = null;

  ngOnInit(): void {
    this.initUser();
    console.log(this.editedUser);
    this.initForm();
    this.setReadMode();
  }

  private initUser(): void {
    this.userService.getCurrentUser().subscribe({
        next: user => {
          this.name = user.name;
          this.lastName = user.lastName;
          this.email = user.email;
          this.imageId = user.imageId || null;
            if(user.role === 'USUARIO') {
              this.editedUser = user as Usuario;
              this.vip = (this.editedUser as Usuario).vip ? 'Eres VIP' : 'No eres VIP';
              this.birthDate = this.getbirthDate();
              this.alias = (this.editedUser as Usuario).alias || '';
              console.log(this.birthDate);
            } else if (user.role === 'CREADOR') {
              this.editedUser = user as Creator;
              this.alias = (this.editedUser as Creator).alias || '';
              this.description = (this.editedUser as Creator).description || '';
              this.field = (this.editedUser as Creator).field;
              this.type = (this.editedUser as Creator).type;
            } else if (user.role === 'ADMIN') {
              this.editedUser = user as Admin;
              this.department = (this.editedUser as Admin).department;
            }else {
              this.router.navigate(['/unauthorized']);
              return;
            }
            this.role = user.role;
        },
        error: err => {
          alert('Error al cargar el usuario actual');
        }
      });
  }

  private initForm(): void {
    this.editForm = this.fb.group({
      name: [ this.editedUser?.name || '' ],
      lastName: [ this.editedUser?.lastName || ''],
      alias: [ (this.editedUser as any)?.alias || ''],
      birthDate: [ (this.editedUser as any)?.birthDate || '' ],
      vip: [ !!(this.editedUser as any)?.vip ],
      description: [ (this.editedUser as any)?.description || '' ],
      field: [ (this.editedUser as any)?.field || '' ],
      department: [ (this.editedUser as any)?.department || '' ],
      imageId: [ this.selectedPhoto || null ],
    });
  }
//VALIDATORS
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

// CONTROL
getControl(controlName: string): AbstractControl | null {
  return this.editForm.get(controlName);
}

getAvatar(): string {
      return getAvatarUrlById(this.editedUser?.imageId || 0);
}
getbirthDate(): string {
  const date = (this.editedUser as Usuario)?.birthDate
  const formatted = date.toString().split('T')[0];
  return formatted;
}
//TOGGLE
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

  // RESET controles: deshabilitar y quitar validadores
  private resetControls() {
    Object.keys(this.editForm.controls).forEach(k => {
      const c = this.editForm.get(k)!;
      c.disable({ emitEvent: false });
      c.clearValidators();
      c.updateValueAndValidity({ emitEvent: false });
    });
  }

  // HABILITAR control que queramos con sus validadores
  private enable(name: string, validators: any[] = []) {
    const c = this.editForm.get(name);
    if (!c) return;
    c.setValidators(validators);
    c.enable({ emitEvent: false });
    c.updateValueAndValidity({ emitEvent: false });
  }

  // DESHABILITAR TODO para modo lectura
  private setReadMode() {
    this.editMode = false;
    this.backup = { ...this.editForm.getRawValue() };
    this.resetControls();
  }

  // INICIAR EDICIÓN: habilitar solo controles permitidos y añadir validadores dinámicos
  startEdit() {
    this.editMode = true;
    this.resetControls();

    // ENABLE campos comunes a los 3 roles
    this.enable('name', [Validators.required, Validators.maxLength(50)]);
    this.editForm.patchValue({ name: this.editedUser?.name || '' }, { emitEvent: false });
    this.enable('lastName', [Validators.required, Validators.maxLength(100)]);
    this.editForm.patchValue({ lastName: this.editedUser?.lastName || '' }, { emitEvent: false });
    this.enable('alias', [Validators.minLength(3), Validators.maxLength(20)]);
    this.editForm.patchValue({ alias: (this.editedUser as any)?.alias || '' }, { emitEvent: false });
    this.enable('imageId');
    this.editForm.patchValue({ imageId: this.editedUser?.imageId || null }, { emitEvent: false });

    if (this.role === 'USUARIO') {
      this.enable('birthDate', [Validators.required, this.minAgeValidator(4)]);
      this.editForm.patchValue({ birthDate: this.getbirthDate() || '' }, { emitEvent: false });
      this.enable('vip');
    }

    if (this.role === 'CREADOR') {
      this.enable('description', [Validators.maxLength(500)]);
      this.editForm.patchValue({ description: (this.editedUser as Creator).description || '' }, { emitEvent: false });
      this.enable('field', [Validators.required]);
      this.editForm.patchValue({ field: (this.editedUser as Creator).field || '' }, { emitEvent: false });
    }

    if (this.role === 'ADMIN') {
      this.enable('department', [Validators.required]);
      this.editForm.patchValue({ department: (this.editedUser as Admin).department || '' }, { emitEvent: false });
    }
  }

  cancelEdit() {
    this.editForm.reset({ ...this.backup, contraseña: '' }, { emitEvent: false });
    this.setReadMode();
  }

  canSave(): boolean {
    const anyTouched = Object.values(this.editForm.controls).some(c => c.touched);
    const anyChanged = this.editForm.dirty;
    return anyTouched && anyChanged;
  }

  save() {
    if (!this.canSave()) return;


  }

}
