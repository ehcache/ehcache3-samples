import { Routes } from '@angular/router';

import { UserRouteAccessService } from '../../shared';
import { ActorComponent } from './actor.component';
import { ActorDetailComponent } from './actor-detail.component';
import { ActorPopupComponent } from './actor-dialog.component';
import { ActorDeletePopupComponent } from './actor-delete-dialog.component';

export const actorRoute: Routes = [
    {
        path: 'actor',
        component: ActorComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Actors'
        },
        canActivate: [UserRouteAccessService]
    }, {
        path: 'actor/:id',
        component: ActorDetailComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Actors'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const actorPopupRoute: Routes = [
    {
        path: 'actor-new',
        component: ActorPopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Actors'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'actor/:id/edit',
        component: ActorPopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Actors'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'actor/:id/delete',
        component: ActorDeletePopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Actors'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
