import { NgModule } from '@angular/core';

import { DemoSharedLibsModule, JhiAlertComponent, JhiAlertErrorComponent } from './';

@NgModule({
    imports: [DemoSharedLibsModule],
    declarations: [JhiAlertComponent, JhiAlertErrorComponent],
    exports: [DemoSharedLibsModule, JhiAlertComponent, JhiAlertErrorComponent]
})
export class DemoSharedCommonModule {}
