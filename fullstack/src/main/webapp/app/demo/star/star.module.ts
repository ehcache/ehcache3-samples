import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { DemoSharedModule } from '../../shared';
import {
    StarService,
    StarComponent,
    StarDetailComponent,
    starRoute,
} from './';

const ENTITY_STATES = [
    ...starRoute
];

@NgModule({
    imports: [
        DemoSharedModule,
        RouterModule.forChild(ENTITY_STATES)
    ],
    declarations: [
        StarComponent,
        StarDetailComponent,
    ],
    entryComponents: [
        StarComponent,
    ],
    providers: [
        StarService,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class DemoStarModule {}
