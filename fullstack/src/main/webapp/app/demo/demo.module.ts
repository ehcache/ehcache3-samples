import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

import { DemoStarModule } from './star/star.module';

@NgModule({
    imports: [
        DemoStarModule,
    ],
    declarations: [],
    entryComponents: [],
    providers: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class DemoDemoModule {}
