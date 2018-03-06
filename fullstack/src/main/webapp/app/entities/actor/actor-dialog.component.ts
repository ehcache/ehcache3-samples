import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';

import { Observable } from 'rxjs/Observable';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { Actor } from './actor.model';
import { ActorPopupService } from './actor-popup.service';
import { ActorService } from './actor.service';

@Component({
    selector: 'jhi-actor-dialog',
    templateUrl: './actor-dialog.component.html'
})
export class ActorDialogComponent implements OnInit {

    actor: Actor;
    isSaving: boolean;
    birthDateDp: any;

    constructor(
        public activeModal: NgbActiveModal,
        private actorService: ActorService,
        private eventManager: JhiEventManager
    ) {
    }

    ngOnInit() {
        this.isSaving = false;
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.isSaving = true;
        if (this.actor.id !== undefined) {
            this.subscribeToSaveResponse(
                this.actorService.update(this.actor));
        } else {
            this.subscribeToSaveResponse(
                this.actorService.create(this.actor));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<Actor>>) {
        result.subscribe((res: HttpResponse<Actor>) =>
            this.onSaveSuccess(res.body), (res: HttpErrorResponse) => this.onSaveError());
    }

    private onSaveSuccess(result: Actor) {
        this.eventManager.broadcast({ name: 'actorListModification', content: 'OK'});
        this.isSaving = false;
        this.activeModal.dismiss(result);
    }

    private onSaveError() {
        this.isSaving = false;
    }
}

@Component({
    selector: 'jhi-actor-popup',
    template: ''
})
export class ActorPopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private actorPopupService: ActorPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.actorPopupService
                    .open(ActorDialogComponent as Component, params['id']);
            } else {
                this.actorPopupService
                    .open(ActorDialogComponent as Component);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
