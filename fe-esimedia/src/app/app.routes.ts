import { Routes } from '@angular/router';
import { RegisteruserComponent } from './components/registeruser/registeruser.component';

export const routes: Routes = [
    { path: 'register', component: RegisteruserComponent },
    { path: '', redirectTo: '/register', pathMatch: 'full' }
];
