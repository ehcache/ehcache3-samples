import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { DemoSharedModule } from '../../shared';
import {
    ActorService,
    ActorPopupService,
    ActorComponent,
    ActorDetailComponent,
    ActorDialogComponent,
    ActorPopupComponent,
    ActorDeletePopupComponent,
    ActorDeleteDialogComponent,
    actorRoute,
    actorPopupRoute,
} from './';

const ENTITY_STATES = [
    ...actorRoute,
    ...actorPopupRoute,
];

@NgModule({
    imports: [
        DemoSharedModule,
        RouterModule.forChild(ENTITY_STATES)
    ],
    declarations: [
        ActorComponent,
        ActorDetailComponent,
        ActorDialogComponent,
        ActorDeleteDialogComponent,
        ActorPopupComponent,
        ActorDeletePopupComponent,
    ],
    entryComponents: [
        ActorComponent,
        ActorDialogComponent,
        ActorPopupComponent,
        ActorDeleteDialogComponent,
        ActorDeletePopupComponent,
    ],
    providers: [
        ActorService,
        ActorPopupService,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class DemoActorModule {}
