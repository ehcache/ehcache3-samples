import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';

import { IActor } from 'app/shared/model/actor.model';
import { ActorService } from './actor.service';

@Component({
    selector: 'jhi-actor-update',
    templateUrl: './actor-update.component.html'
})
export class ActorUpdateComponent implements OnInit {
    actor: IActor;
    isSaving: boolean;
    birthDateDp: any;

    constructor(private actorService: ActorService, private activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ actor }) => {
            this.actor = actor;
        });
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        if (this.actor.id !== undefined) {
            this.subscribeToSaveResponse(this.actorService.update(this.actor));
        } else {
            this.subscribeToSaveResponse(this.actorService.create(this.actor));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<IActor>>) {
        result.subscribe((res: HttpResponse<IActor>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    private onSaveSuccess() {
        this.isSaving = false;
        this.previousState();
    }

    private onSaveError() {
        this.isSaving = false;
    }
}
