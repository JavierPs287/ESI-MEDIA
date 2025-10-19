import { Routes } from '@angular/router';
import { RegisteruserComponent } from './components/registeruser/registeruser.component';
import { RegistercreatorComponent } from './components/registercreator/registercreator.component';
import { RegisteradminComponent } from './components/registeradmin/registeradmin.component';
import { HomeComponent } from './components/home/home.component';
import { LoginuserComponent } from './components/loginuser/loginuser.component';
import { UploadContentComponent } from './components/uploadcontent/uploadcontent.component';

export const routes: Routes = [

    {
        path: '',
        redirectTo: '/home',
        pathMatch: 'full'
    },

    {
        path: 'home',
        component: HomeComponent
    },

    {
        path: 'register/user',
        component: RegisteruserComponent
    },

    {
        path: 'register/creator',
        component: RegistercreatorComponent
    },

    {
        path: 'register/admin',
        component: RegisteradminComponent
    },

    {
        path: 'uploadContent',
        component: UploadContentComponent
    },

    {
        path: 'login',
        component: LoginuserComponent
    }
];
