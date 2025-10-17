import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
<<<<<<< HEAD

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
=======
import { RegisteruserComponent } from './components/registeruser/registeruser.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RegisteruserComponent],
>>>>>>> a7b2ac1 (FE v1.2 RegistroUsuario)
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'fe-esimedia';
}
