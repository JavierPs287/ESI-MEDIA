import { Routes } from '@angular/router';
import { RegisteruserComponent } from './components/register/registeruser/registeruser.component';
import { RegistercreatorComponent } from './components/register/registercreator/registercreator.component';
import { RegisteradminComponent } from './components/register/registeradmin/registeradmin.component';
import { HomeComponent } from './components/home/home.component';
import { LoginuserComponent } from './components/loginuser/loginuser.component';
import { UploadContentComponent } from './components/creator-pages/uploadcontent/uploadcontent.component';
import { MainMenuCreatorComponent } from './components/menus/main-menu-creator/main-menu-creator.component';
import { MainMenuAdminComponent } from './components/menus/main-menu-admin/main-menu-admin.component';
import { UserManagementComponent } from './components/admin-pages/user-management/user-management.component';

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
        path: 'login',
        component: LoginuserComponent
    },

    {
        path: 'menu/creator',
        component: MainMenuCreatorComponent,
        children: [
            {
                path: 'uploadContent',
                component: UploadContentComponent
            }
        ]
    },

    {
        path: 'menu/admin',
        component: MainMenuAdminComponent,
        children: [
            {
                path: 'userManagement',
                component: UserManagementComponent
            },
            {
                path: 'register/admin',
                component: RegisteradminComponent
            },
            {
                path: 'register/creator',
                component: RegistercreatorComponent
            },
        ]
    }
];
