import { Routes } from '@angular/router';
import { RegisteruserComponent } from './components/register/registeruser/registeruser.component';
import { RegistercreatorComponent } from './components/register/registercreator/registercreator.component';
import { RegisteradminComponent } from './components/register/registeradmin/registeradmin.component';
import { HomeComponent } from './components/home/home.component';
import { LoginuserComponent } from './components/loginuser/loginuser.component';
import { UploadContentComponent } from './components/creator-pages/uploadcontent/uploadcontent.component';
import { MainMenuCreator } from './components/menus/main-menu-creator/main-menu-creator.component';

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
        path: 'login',
        component: LoginuserComponent
    },

    {
        path: 'menu/creator',
        component: MainMenuCreator,
        children: [
            {
                path: 'uploadContent',
                component: UploadContentComponent
            }
        ]
    }
];
