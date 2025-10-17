import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { RegisteruserComponent } from './components/registeruser/registeruser.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RegisteruserComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'fe-esimedia';
}
