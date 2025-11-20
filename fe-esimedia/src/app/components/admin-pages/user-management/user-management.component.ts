import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { ConfirmDialogComponent } from '../../confirm-dialog/confirm-dialog.component';
import { UserService } from '../../../services/user.service';
import { Usuario as User } from '../../../models/user.model';
import { getAvatarUrlById } from '../../../services/image.service';

@Component({
  selector: 'app-user-management',
  imports: [FormsModule, CommonModule, MatIconModule, MatDialogModule],
  templateUrl: './user-management.component.html',
  styleUrl: './user-management.component.css'
})
export class UserManagementComponent implements OnInit {

  searchTerm: string = '';
  selectedRole: string = '';
  selectedStatus: string = '';
  selectedVipFilter: string = '';

  users: User[] = [];
  filteredUsers: User[] = [];
  isLoading = false;
  errorMessage = '';

  constructor(
    public dialogo: MatDialog,
    private readonly userService: UserService
  ) { }

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.userService.getAllUsers().subscribe({
      next: (users) => {
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error al cargar usuarios:', error);
        this.errorMessage = 'Error al cargar los usuarios. Por favor, intenta de nuevo.';
        this.isLoading = false;
      }
    });
  }

  onSearch(): void {
    this.applyFilters();
  }

  onFilterChange(): void {
    this.applyFilters();
  }

  applyFilters(): void {
    this.filteredUsers = this.users.filter(user => {
      const matchesSearch = !this.searchTerm ||
        user.name?.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        user.email?.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        (user.alias?.toLowerCase() || '').includes(this.searchTerm.toLowerCase());

      const matchesRole = !this.selectedRole || user.role === this.selectedRole;

      const matchesStatus = !this.selectedStatus ||
        (this.selectedStatus === 'bloqueado' ? user.blocked : !user.blocked);

      const matchesVip = !this.selectedVipFilter ||
        (this.selectedVipFilter === 'vip' ? user.vip : !user.vip);

      return matchesSearch && matchesRole && matchesStatus && matchesVip;
    });
  }

  toggleBlockUser(email: string): void {
    const user = this.users.find(u => u.email === email);
    if (!user) return;

    const action = user.blocked ? 'desbloquear' : 'bloquear';

    this.dialogo.open(ConfirmDialogComponent, {
      data: {
        title: `¿${action.charAt(0).toUpperCase() + action.slice(1)} usuario?`,
        message: `¿Estás seguro de que deseas ${action} a ${user.name}?`,
        confirmText: action.charAt(0).toUpperCase() + action.slice(1),
        cancelText: 'Cancelar',
        type: user.blocked ? 'success' : 'danger'
      }
    }).afterClosed().subscribe((result: boolean) => {
      if (result) {
        user.blocked = !user.blocked;
        this.applyFilters();
      }
    });
  }

  editUser(email: string): void {
    // Implementa la lógica de edición
  }

  deleteUser(email: string): void {
    const user = this.users.find(u => u.email === email);
    if (!user) return;

    this.dialogo.open(ConfirmDialogComponent, {
      data: {
        title: '¿Eliminar usuario?',
        message: `¿Estás seguro de que deseas eliminar a ${user.name}? Esta acción no se puede deshacer.`,
        confirmText: 'Eliminar',
        cancelText: 'Cancelar',
        type: 'danger'
      }
    }).afterClosed().subscribe((result: boolean) => {
      if (result) {
        // Eliminar del array
        const index = this.users.findIndex(u => u.email === email);
        if (index !== -1) {
          this.users.splice(index, 1);
          this.applyFilters();
          // Aquí llamarías a tu API para eliminar el usuario del backend
        }
      }
    });
  }

  viewUser(email: string): void {
    // Implementa la lógica de visualización
  }

  getAvatar(user: User): string {
    return getAvatarUrlById(user.imageId || 0);
  }

  setRole(user: User): void {
    this.selectedRole = user.role;
  }
}
