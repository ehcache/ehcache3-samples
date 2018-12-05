/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';

import { DemoTestModule } from '../../../test.module';
import { ActorUpdateComponent } from 'app/entities/actor/actor-update.component';
import { ActorService } from 'app/entities/actor/actor.service';
import { Actor } from 'app/shared/model/actor.model';

describe('Component Tests', () => {
    describe('Actor Management Update Component', () => {
        let comp: ActorUpdateComponent;
        let fixture: ComponentFixture<ActorUpdateComponent>;
        let service: ActorService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [DemoTestModule],
                declarations: [ActorUpdateComponent]
            })
                .overrideTemplate(ActorUpdateComponent, '')
                .compileComponents();

            fixture = TestBed.createComponent(ActorUpdateComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(ActorService);
        });

        describe('save', () => {
            it(
                'Should call update service on save for existing entity',
                fakeAsync(() => {
                    // GIVEN
                    const entity = new Actor(123);
                    spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
                    comp.actor = entity;
                    // WHEN
                    comp.save();
                    tick(); // simulate async

                    // THEN
                    expect(service.update).toHaveBeenCalledWith(entity);
                    expect(comp.isSaving).toEqual(false);
                })
            );

            it(
                'Should call create service on save for new entity',
                fakeAsync(() => {
                    // GIVEN
                    const entity = new Actor();
                    spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
                    comp.actor = entity;
                    // WHEN
                    comp.save();
                    tick(); // simulate async

                    // THEN
                    expect(service.create).toHaveBeenCalledWith(entity);
                    expect(comp.isSaving).toEqual(false);
                })
            );
        });
    });
});
