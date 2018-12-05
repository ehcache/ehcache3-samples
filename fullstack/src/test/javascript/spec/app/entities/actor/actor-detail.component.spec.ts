/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { DemoTestModule } from '../../../test.module';
import { ActorDetailComponent } from 'app/entities/actor/actor-detail.component';
import { Actor } from 'app/shared/model/actor.model';

describe('Component Tests', () => {
    describe('Actor Management Detail Component', () => {
        let comp: ActorDetailComponent;
        let fixture: ComponentFixture<ActorDetailComponent>;
        const route = ({ data: of({ actor: new Actor(123) }) } as any) as ActivatedRoute;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [DemoTestModule],
                declarations: [ActorDetailComponent],
                providers: [{ provide: ActivatedRoute, useValue: route }]
            })
                .overrideTemplate(ActorDetailComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(ActorDetailComponent);
            comp = fixture.componentInstance;
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(comp.actor).toEqual(jasmine.objectContaining({ id: 123 }));
            });
        });
    });
});
