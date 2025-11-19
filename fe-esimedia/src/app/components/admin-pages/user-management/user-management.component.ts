import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { ConfirmDialogComponent } from '../../confirm-dialog/confirm-dialog.component';

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

  // Mock data - reemplaza con datos de tu API
  users = [
    {
      id: 1,
      name: 'Maya Santos',
      email: 'maya.s@example.com',
      alias: 'mayasantos',
      role: 'usuario',
      avatar: 'assets/avatars/maya.jpg',
      isBlocked: false,
      isVip: true
    },
  ];

  filteredUsers = this.users;

  constructor(public dialogo: MatDialog) { }

  ngOnInit(): void {
    this.filteredUsers = [...this.users];
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
        user.name.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        user.email.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        user.alias.toLowerCase().includes(this.searchTerm.toLowerCase());

      const matchesRole = !this.selectedRole || user.role === this.selectedRole;
      
      const matchesStatus = !this.selectedStatus || 
        (this.selectedStatus === 'bloqueado' ? user.isBlocked : !user.isBlocked);

      const matchesVip = !this.selectedVipFilter || 
        (this.selectedVipFilter === 'vip' ? user.isVip : !user.isVip);

      return matchesSearch && matchesRole && matchesStatus && matchesVip;
    });
  }

  toggleBlockUser(userId: number): void {
    const user = this.users.find(u => u.id === userId);
    if (!user) return;

    const action = user.isBlocked ? 'desbloquear' : 'bloquear';
    
    this.dialogo.open(ConfirmDialogComponent, {
      data: {
        title: `¿${action.charAt(0).toUpperCase() + action.slice(1)} usuario?`,
        message: `¿Estás seguro de que deseas ${action} a ${user.name}?`,
        confirmText: action.charAt(0).toUpperCase() + action.slice(1),
        cancelText: 'Cancelar',
        type: user.isBlocked ? 'success' : 'danger'
      }
    }).afterClosed().subscribe((result: boolean) => {
      if (result) {
        user.isBlocked = !user.isBlocked;
        this.applyFilters();
        console.log(`Usuario ${user.name} ${user.isBlocked ? 'bloqueado' : 'desbloqueado'}`);
      }
    });
  }

  editUser(userId: number): void {
    // Implementa la lógica de edición
  }

  deleteUser(userId: number): void {
    const user = this.users.find(u => u.id === userId);
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
        const index = this.users.findIndex(u => u.id === userId);
        if (index !== -1) {
          this.users.splice(index, 1);
          this.applyFilters();
          console.log(`Usuario ${user.name} eliminado`);
          // Aquí llamarías a tu API para eliminar el usuario del backend
        }
      }
    });
  }

  viewUser(userId: number): void {
    // Implementa la lógica de visualización
  }
}
