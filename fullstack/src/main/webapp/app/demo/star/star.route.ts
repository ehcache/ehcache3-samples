import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core';
import { of } from 'rxjs';
import { map } from 'rxjs/operators';
import { Star } from 'app/demo/star/star.model';
import { StarService } from 'app/demo/star/star.service';
import { StarComponent } from './star.component';
import { StarDetailComponent } from './star-detail.component';

@Injectable({ providedIn: 'root' })
export class StarResolve implements Resolve<Star> {
    constructor(private service: StarService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(map((actor: HttpResponse<Star>) => actor.body));
        }
        return of(new Star());
    }
}

export const starRoute: Routes = [
    {
        path: 'star',
        component: StarComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Stars'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'star/:id/view',
        component: StarDetailComponent,
        resolve: {
            star: StarResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Stars'
        },
        canActivate: [UserRouteAccessService]
    }
];
