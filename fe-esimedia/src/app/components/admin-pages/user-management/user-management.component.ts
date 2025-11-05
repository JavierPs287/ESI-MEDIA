import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';



@Component({
  selector: 'app-user-management',
  imports: [FormsModule, CommonModule, MatIconModule],
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
    // Añade más usuarios aquí
  ];

  filteredUsers = this.users;

  constructor() { }

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

  editUser(userId: number): void {
    // Implementa la lógica de edición
  }

  deleteUser(userId: number): void {
    // Implementa la lógica de eliminación
  }

  viewUser(userId: number): void {
    // Implementa la lógica de visualización
  }
}
