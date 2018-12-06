import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { Actor } from 'app/shared/model/actor.model';
import { ActorService } from './actor.service';
import { ActorComponent } from './actor.component';
import { ActorDetailComponent } from './actor-detail.component';
import { ActorUpdateComponent } from './actor-update.component';
import { ActorDeletePopupComponent } from './actor-delete-dialog.component';
import { IActor } from 'app/shared/model/actor.model';

@Injectable({ providedIn: 'root' })
export class ActorResolve implements Resolve<IActor> {
    constructor(private service: ActorService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Actor> {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(
                filter((response: HttpResponse<Actor>) => response.ok),
                map((actor: HttpResponse<Actor>) => actor.body)
            );
        }
        return of(new Actor());
    }
}

export const actorRoute: Routes = [
    {
        path: 'actor',
        component: ActorComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Actors'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'actor/:id/view',
        component: ActorDetailComponent,
        resolve: {
            actor: ActorResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Actors'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'actor/new',
        component: ActorUpdateComponent,
        resolve: {
            actor: ActorResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Actors'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'actor/:id/edit',
        component: ActorUpdateComponent,
        resolve: {
            actor: ActorResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Actors'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const actorPopupRoute: Routes = [
    {
        path: 'actor/:id/delete',
        component: ActorDeletePopupComponent,
        resolve: {
            actor: ActorResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Actors'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
