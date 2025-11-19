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
import { MainMenuUserComponent } from './components/menus/main-menu-user/main-menu-user.component';
import { ShowContentComponent } from './components/show-content/show-content.component';
import { ReproduceContentComponent } from './components/reproduce-content/reproduce-content.component';
import { ConnectTotpComponent } from './components/connect-totp/connect-totp.component';
import { VerifyTotpComponent } from './components/verify-totp/verify-totp.component';

export const routes: Routes = [

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
        path: 'forgotpassword',
        component: ForgotpasswordComponent
    },

    {
        path: 'resetPassword',
        component: ResetpasswordComponent
    },

    {
        path: 'menu/creator',
        component: MainMenuCreatorComponent,
        canActivate: [authGuard, roleGuard],
        data: { roles: ['CREADOR'] },
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
        canActivate: [authGuard, roleGuard],
        data: { roles: ['ADMIN'] },
        children: [
            {
                path: '',
                component: ShowContentComponent
            },
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
        canActivate: [authGuard, roleGuard],
        data: { roles: ['USUARIO'] },
        children: [
            {
                path: '',
                component: ShowContentComponent
            },
            {
                path: 'reproduce/:urlId',
                component: ReproduceContentComponent
            }
        ]
    },
    {
        path: 'activar2FA',
        component: ConnectTotpComponent
    },
    {
        path: 'verify-totp',
        component: VerifyTotpComponent
    },
    {
        path: '**',
        redirectTo: 'home',
        pathMatch: 'full'
    },
];
