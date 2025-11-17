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
import { UnauthorizedComponent } from './components/unauthorized/unauthorized.component';
import { authGuard } from './guards/auth.guard';
import { roleGuard } from './guards/role.guard';
import { ForgotpasswordComponent } from './components/forgotpassword/forgotpassword.component';
import { ResetpasswordComponent } from './components/resetpassword/resetpassword.component';
import { TemporalplaylistsComponent } from './components/temporalplaylists/temporalplaylists.component';

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
        path: 'unauthorized',
        component: UnauthorizedComponent
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
        canActivate: [authGuard, roleGuard],
        data: { roles: ['CREATOR'] },
        children: [
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
        canActivate: [authGuard, roleGuard],
        data: { roles: ['ADMIN'] },
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
        path: 'forgotpassword',
        component: ForgotpasswordComponent
    },
    {
        path: 'resetPassword',
        component: ResetpasswordComponent
    },
        {
        path: 'playlist',
        component: TemporalplaylistsComponent
    },
];
