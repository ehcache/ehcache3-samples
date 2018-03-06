import { Routes } from '@angular/router';

import { UserRouteAccessService } from '../../shared';
import { StarComponent } from './star.component';
import { StarDetailComponent } from './star-detail.component';

export const starRoute: Routes = [
    {
        path: 'star',
        component: StarComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Stars'
        },
        canActivate: [UserRouteAccessService]
    }, {
        path: 'star/:id',
        component: StarDetailComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Stars'
        },
        canActivate: [UserRouteAccessService]
    }
];
