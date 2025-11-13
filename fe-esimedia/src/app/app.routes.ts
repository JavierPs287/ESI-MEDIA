import { Routes } from '@angular/router';
import { RegisteruserComponent } from './components/register/registeruser/registeruser.component';
import { RegistercreatorComponent } from './components/register/registercreator/registercreator.component';
import { RegisteradminComponent } from './components/register/registeradmin/registeradmin.component';
import { HomeComponent } from './components/home/home.component';
import { LoginuserComponent } from './components/loginuser/loginuser.component';
import { MainMenuCreatorComponent } from './components/menus/main-menu-creator/main-menu-creator.component';
import { MainMenuAdminComponent } from './components/menus/main-menu-admin/main-menu-admin.component';
import { UserManagementComponent } from './components/admin-pages/user-management/user-management.component';
import { UploadAudioComponent } from './components/creator-pages/uploadcontent/uploadaudio/uploadaudio.component';
import { UploadVideoComponent } from './components/creator-pages/uploadcontent/uploadvideo/uploadvideo.component';
import { MainMenuUserComponent } from './components/menus/main-menu-user/main-menu-user.component';
import { ShowContentComponent } from './components/show-content/show-content.component';
import { ReproduceContentComponent } from './components/reproduce-content/reproduce-content.component';

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
                path: '',
                component: ShowContentComponent
            },
            {
                path: 'uploadContent/audio',
                component: UploadAudioComponent
            },
            {
                path: 'uploadContent/video',
                component: UploadVideoComponent
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
    },

    {
        path: 'menu/user',
        component: MainMenuUserComponent,
        children: [
            {
                path: '',
                component: ShowContentComponent
            }
        ]
    },
    {
        path: 'reproduce/:title',
        component: ReproduceContentComponent
    }
];
