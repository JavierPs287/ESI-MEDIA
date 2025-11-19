import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../navbar/navbar.component';

@Component({
  selector: 'app-unauthorized',
  imports: [CommonModule, NavbarComponent],
  template: `
    <app-navbar></app-navbar>
    <div class="unauthorized-container">
      <div class="error-content">
        <div class="icon">🚫</div>
        <h1>Acceso Denegado</h1>
        <p class="error-code">403</p>
        <p class="error-message">
          No tienes los permisos necesarios para acceder a esta página.
        </p>
        <div class="info-box">
          <p><strong>Posibles razones:</strong></p>
          <ul>
            <li>No tienes el rol adecuado para esta sección</li>
            <li>Tu sesión puede haber expirado</li>
            <li>Intentaste acceder a una ruta administrativa</li>
          </ul>
          <p class="note">Si crees que esto es un error, contacta con un administrador.</p>
        </div>
        <div class="actions">
          <button class="btn-primary" (click)="goMenu()">
            🏠 Volver al inicio
          </button>
          <button class="btn-secondary" (click)="goBack()">
            ← Volver atrás
          </button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .unauthorized-container {
      display: flex;
      justify-content: center;
      align-items: center;
      min-height: 80vh;
      padding: 2rem;
      background: var(--linear-gradient);
    }

    .error-content {
      text-align: center;
      max-width: 600px;
      background: white;
      padding: 3rem;
      border-radius: 16px;
      box-shadow: 0 20px 60px rgba(0,0,0,0.3);
      animation: slideIn 0.3s ease-out;
    }

    @keyframes slideIn {
      from {
        opacity: 0;
        transform: translateY(-20px);
      }
      to {
        opacity: 1;
        transform: translateY(0);
      }
    }

    .icon {
      font-size: 5rem;
      margin-bottom: 1rem;
      animation: pulse 2s infinite;
    }

    @keyframes pulse {
      0%, 100% {
        transform: scale(1);
      }
      50% {
        transform: scale(1.1);
      }
    }

    h1 {
      font-size: 2rem;
      color: #333;
      margin-bottom: 0.5rem;
      font-weight: 600;
    }

    .error-code {
      font-size: 5rem;
      font-weight: bold;
      color: #dc3545;
      margin: 0.5rem 0;
      line-height: 1;
    }

    .error-message {
      font-size: 1.1rem;
      color: #666;
      margin: 1.5rem 0;
      line-height: 1.6;
    }

    .info-box {
      background: #f8f9fa;
      padding: 1.5rem;
      border-radius: 8px;
      margin: 1.5rem 0;
      border-left: 4px solid #dc3545;
      text-align: left;
    }

    .info-box p {
      margin: 0.5rem 0;
      color: #495057;
      font-size: 0.95rem;
    }

    .info-box strong {
      color: #333;
      font-size: 1rem;
    }

    .info-box ul {
      margin: 0.5rem 0;
      padding-left: 1.5rem;
      color: #666;
    }

    .info-box li {
      margin: 0.5rem 0;
      font-size: 0.9rem;
    }

    .note {
      margin-top: 1rem;
      font-style: italic;
      color: #6c757d !important;
      font-size: 0.85rem !important;
    }

    .actions {
      display: flex;
      gap: 1rem;
      justify-content: center;
      margin-top: 2rem;
      flex-wrap: wrap;
    }

    .btn-primary, .btn-secondary {
      padding: 0.75rem 1.5rem;
      border: none;
      border-radius: 8px;
      font-size: 1rem;
      cursor: pointer;
      transition: all 0.2s;
      font-weight: 500;
    }

    .btn-primary {
      background-color: var(--azul);
      color: white;
    }

    .btn-primary:hover {
      background-color: var(--azul-oscuro);
      transform: translateY(-2px);
      box-shadow: 0 4px 8px rgba(0,0,0,0.2);
    }

    .btn-secondary {
      background-color: var(--gris);
      color: white;
    }

    .btn-secondary:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 8px rgba(0,0,0,0.2);
    }
  `]
})
export class UnauthorizedComponent {
  constructor(private router: Router) {}

  goBack(): void {
    window.history.go(-2);
  }
  goMenu(): void {
    this.router.navigate(['/']);
  }
}
