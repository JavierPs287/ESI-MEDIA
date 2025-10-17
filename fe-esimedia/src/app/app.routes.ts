import { Routes } from '@angular/router';
import { LoginuserComponent } from './components/loginuser/loginuser.component';
import { RegisteruserComponent } from './components/registeruser/registeruser.component';

export const routes: Routes = [
  { path: 'login', component: LoginuserComponent },
  { path: 'register', component: RegisteruserComponent }
];
