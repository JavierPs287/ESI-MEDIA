import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, AbstractControl, ValidationErrors} from '@angular/forms';
import { FIELDS, DEPARTMENTS } from '../../constants/form-constants';
import { AVATAR_OPTIONS } from '../../constants/avatar-constants';
import { Admin, Creator, Usuario } from '../../models/user.model';
import { UserService } from '../../services/user.service';
import { UsuarioService} from '../../services/usuario.service';
import { CreatorService} from '../../services/creator.service';
import { AdminService} from '../../services/admin.service';
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
  private readonly usuarioService = inject(UsuarioService);
  private readonly creatorService = inject(CreatorService);
  private readonly adminService = inject(AdminService);
  private readonly router = inject(Router);
  editedUser!: Usuario | Creator | Admin | null;

  fb = inject(FormBuilder);
   editForm!: FormGroup;

  editMode = false;
  backup: any = {};
  user!: Admin | Creator | Usuario;
  fields = FIELDS;
  isMe = true;
  isSubmitting = false;

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
  const url = this.router.url || '';
  // Si la ruta empieza por /editar usamos el state enviado por la navegación
  if (url ==('/menu/admin/modify')) {
    this.isMe = false;
    const navigation = this.router.getCurrentNavigation();
    const state = navigation?.extras.state || history.state;
    
    if (state && state['user']) {
      this.user = state['user'];
    }
    if (this.user) {
      this.initializeFromState(this.user);
      this.initForm();
      this.setReadMode();
      return;
    }
  }
  this.isMe = true;
  this.initUser();
  this.initForm();
  this.setReadMode();
}

private initializeFromState(user: any): void {
  this.name = user.name;
  this.lastName = user.lastName;
  this.email = user.email;
  this.imageId = user.imageId || null;
  const role = user.role;

    if (role === 'USUARIO') {
      this.editedUser = user as Usuario;
      this.vip = this.editedUser.vip ? 'Eres VIP' : 'No eres VIP';
      this.birthDate = this.getbirthDate();
      this.alias = this.editedUser.alias || '';
    } else if (role === 'CREADOR') {
      this.editedUser = user as Creator;
      this.alias = this.editedUser.alias;
      this.description = this.editedUser.description || '';
      this.field = this.editedUser.field ;
      this.type = this.editedUser.type || '';
    } else if (role === 'ADMIN') {
      this.editedUser = user as Admin;
      this.department = this.editedUser.department ;
    } else {
      alert ('Rol de usuario no reconocido');
      return;
    }
  this.role = user.role;
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
              this.vip = this.editedUser.vip ? 'Eres VIP' : 'No eres VIP';
              this.birthDate = this.getbirthDate();
              this.alias = this.editedUser.alias || '';
            } else if (user.role === 'CREADOR') {
              this.editedUser = user as Creator;
              this.alias = this.editedUser.alias || '';
              this.description = this.editedUser.description || '';
              this.field = this.editedUser.field;
              this.type = this.editedUser.type || '';
            } else if (user.role === 'ADMIN') {
              this.editedUser = user as Admin;
              this.department = this.editedUser.department;
            }else {
              this.router.navigate(['/unauthorized']);
              return;
            }
            this.role = user.role;

          // Inicializar form sólo después de tener editedUser
          this.initForm();
          this.setReadMode();
        },
        error: err => {
          alert('Error al cargar el usuario actual');
        }
      });
  }

  private initForm(): void {
    this.editForm = this.fb.group({
      name: [ '' ],
      lastName: [ ''],
      alias: [  ''],
      birthDate: [ '' ],
      vip: [ false ],
      description: [ '' ],
      field: [ '' ],
      department: [ '' ],
      imageId: [ null ],
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

  // Construye un Usuario a partir del formulario y del usuario cargado
  private buildUsuarioFromForm(): Usuario {
    const v = this.editForm.getRawValue(); // incluye campos deshabilitados
    return {
      name: v.name || this.name,
      lastName: v.lastName || this.lastName,
      email: this.email,
      alias: v.alias || this.alias,
      birthDate: v.birthDate ? new Date(v.birthDate).toISOString() : this.birthDate,
      vip: v.vip,
      imageId: v.imageId || this.selectedPhoto,
      role: 'USUARIO',
    };
  }

  // Construye un Creator a partir del formulario y del usuario cargado
  private buildCreatorFromForm(): Creator {
    const v = this.editForm.getRawValue();
    return {
      name: v.name || this.name,
      lastName: v.lastName || this.lastName,
      email: this.email,
      alias: v.alias || this.alias,
      imageId: v.imageId || this.selectedPhoto || null,
      role: 'CREADOR',
      description: v.description || this.description,
      field: v.field || this.field,
    };
  }

  // Construye un Admin a partir del formulario y del usuario cargado
  private buildAdminFromForm(): Admin {
    const v = this.editForm.getRawValue();
    return {
      name: v.name || this.name,
      lastName: v.lastName || this.lastName,
      email: this.email,
      imageId: v.imageId || this.selectedPhoto,
      role: 'ADMIN',
      department: v.department || this.department
    };
  }

  canSave(): boolean {
    return this.editForm.valid && this.editForm.dirty;
  }

  save() {
    this.isSubmitting = true;
    if (this.isMe){
      if (this.role === 'USUARIO') {
        const userData: Usuario = this.buildUsuarioFromForm();
        this.usuarioService.updateProfile(userData).subscribe({
          next: (response) => {
            alert('Perfil de usuario actualizado correctamente');
            this.isSubmitting = false;
          },
          error: (error) => {
            alert('Error al actualizar el perfil de usuario');
            this.isSubmitting = false;
          }
        });
      } else if (this.role === 'CREADOR') {
        const creatorData: Creator = this.buildCreatorFromForm();
        this.creatorService.updateProfile(creatorData).subscribe({
          next: (response) => {
            alert('Perfil de creador actualizado correctamente');
          this.isSubmitting = false;
          },
          error: (error) => {
            alert('Error al actualizar el perfil de creador');
            this.isSubmitting = false;
          }
        });
      } else if (this.role === 'ADMIN') {
        const adminData: Admin = this.buildAdminFromForm();
        this.adminService.updateProfile(adminData).subscribe({
          next: (response) => {
            alert('Perfil de administrador actualizado correctamente');
            this.isSubmitting = false;
          },
          error: (error) => {
            alert('Error al actualizar el perfil de administrador');
            this.isSubmitting = false;
          }
        });
      } else {
        alert('Rol de usuario no reconocido');
      }
    }
  }
}
