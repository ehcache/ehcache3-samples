import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { DemoSharedModule } from 'app/shared';
import {
    ActorComponent,
    ActorDetailComponent,
    ActorUpdateComponent,
    ActorDeletePopupComponent,
    ActorDeleteDialogComponent,
    actorRoute,
    actorPopupRoute
} from './';

const ENTITY_STATES = [...actorRoute, ...actorPopupRoute];

@NgModule({
    imports: [DemoSharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [ActorComponent, ActorDetailComponent, ActorUpdateComponent, ActorDeleteDialogComponent, ActorDeletePopupComponent],
    entryComponents: [ActorComponent, ActorUpdateComponent, ActorDeleteDialogComponent, ActorDeletePopupComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class DemoActorModule {}
