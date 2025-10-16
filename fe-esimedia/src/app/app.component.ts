import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { RegisterComponent} from "./registrouser/registrouser.component";

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RegisterComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'fe-esimedia';
}
