import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ConnectTotpService } from '../../services/connect-totp.service';
import { Router } from '@angular/router';
import { NavbarComponent } from '../navbar/navbar.component';
import * as QRCode from 'qrcode';

@Component({
  selector: 'app-connect-totp',
  standalone: true,
  imports: [CommonModule, NavbarComponent],
  templateUrl: './connect-totp.component.html',
  styleUrls: ['./connect-totp.component.css']
})
export class ConnectTotpComponent implements OnInit {
  qrUrl: string = '';
  secret: string = '';
  email: string = '';
  loading: boolean = false;
  error: string = '';

  constructor(
    private readonly connectTotpService: ConnectTotpService, 
    private readonly router: Router,
    private readonly cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    const nav = this.router.getCurrentNavigation();
    this.email = nav?.extras?.state?.['email'] || '';
    
    if (!this.email) {
      const match = document.cookie.match(/(?:^|; )esi_email=([^;]*)/);
      if (match) {
        try {
          this.email = atob(decodeURIComponent(match[1]));
        } catch (e) {
          this.email = '';
        }
      }
    }
    
    if (this.email) {
      this.loading = true;
      this.connectTotpService.activar2FA(this.email).subscribe({
        next: (data: any) => {
          this.secret = data.secret || '';
          
          if (this.secret) {
            this.generateQRCode();
          }
          
          this.loading = false;
          this.cdr.detectChanges();
        },
        error: (err) => {
          this.error = 'Error al activar 2FA';
          this.loading = false;
          this.cdr.detectChanges();
        }
      });
    } else {
      this.error = 'No se encontró el email del usuario';
    }
  }

  private generateQRCode(): void {
    const otpauthUrl = `otpauth://totp/ESIMEDIA:${this.email}?secret=${this.secret}&issuer=ESIMEDIA`;
    
    QRCode.toDataURL(otpauthUrl, {
      width: 300,
      margin: 2,
      color: {
        dark: '#000000',
        light: '#FFFFFF'
      }
    })
    .then((dataUrl: string) => {
      this.qrUrl = dataUrl;
      this.cdr.detectChanges();
    })
    .catch((err: Error) => {
      this.error = 'Error al generar el código QR';
    });
  }

  goToHome(): void {
    // Obtener el email guardado en la cookie (último registro admin/creator)
    const match = document.cookie.match(/(?:^|; )esi_email=([^;]*)/);
    let cookieEmail = '';
    if (match) {
      try {
        cookieEmail = atob(decodeURIComponent(match[1]));
      } catch (e) {
        cookieEmail = '';
      }
    }

    // Si el email actual coincide con el de la cookie, es admin/creator
    if (this.email && cookieEmail && this.email === cookieEmail) {
      this.router.navigate(['/menu/admin']);
    } else {
      this.router.navigate(['/home']);
    }
  }
}