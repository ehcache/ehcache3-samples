/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { DemoTestModule } from '../../../test.module';
import { ActorDetailComponent } from '../../../../../../main/webapp/app/entities/actor/actor-detail.component';
import { ActorService } from '../../../../../../main/webapp/app/entities/actor/actor.service';
import { Actor } from '../../../../../../main/webapp/app/entities/actor/actor.model';

describe('Component Tests', () => {

    describe('Actor Management Detail Component', () => {
        let comp: ActorDetailComponent;
        let fixture: ComponentFixture<ActorDetailComponent>;
        let service: ActorService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [DemoTestModule],
                declarations: [ActorDetailComponent],
                providers: [
                    ActorService
                ]
            })
            .overrideTemplate(ActorDetailComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(ActorDetailComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(ActorService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                spyOn(service, 'find').and.returnValue(Observable.of(new HttpResponse({
                    body: new Actor(123)
                })));

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(service.find).toHaveBeenCalledWith(123);
                expect(comp.actor).toEqual(jasmine.objectContaining({id: 123}));
            });
        });
    });

});
