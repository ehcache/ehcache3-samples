/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { Observable } from 'rxjs/Observable';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { DemoTestModule } from '../../../test.module';
import { ActorComponent } from '../../../../../../main/webapp/app/entities/actor/actor.component';
import { ActorService } from '../../../../../../main/webapp/app/entities/actor/actor.service';
import { Actor } from '../../../../../../main/webapp/app/entities/actor/actor.model';

describe('Component Tests', () => {

    describe('Actor Management Component', () => {
        let comp: ActorComponent;
        let fixture: ComponentFixture<ActorComponent>;
        let service: ActorService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [DemoTestModule],
                declarations: [ActorComponent],
                providers: [
                    ActorService
                ]
            })
            .overrideTemplate(ActorComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(ActorComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(ActorService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN
                const headers = new HttpHeaders().append('link', 'link;link');
                spyOn(service, 'query').and.returnValue(Observable.of(new HttpResponse({
                    body: [new Actor(123)],
                    headers
                })));

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(service.query).toHaveBeenCalled();
                expect(comp.actors[0]).toEqual(jasmine.objectContaining({id: 123}));
            });
        });
    });

});
