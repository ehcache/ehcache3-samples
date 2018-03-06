import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { Actor } from './actor.model';
import { ActorPopupService } from './actor-popup.service';
import { ActorService } from './actor.service';

@Component({
    selector: 'jhi-actor-delete-dialog',
    templateUrl: './actor-delete-dialog.component.html'
})
export class ActorDeleteDialogComponent {

    actor: Actor;

    constructor(
        private actorService: ActorService,
        public activeModal: NgbActiveModal,
        private eventManager: JhiEventManager
    ) {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.actorService.delete(id).subscribe((response) => {
            this.eventManager.broadcast({
                name: 'actorListModification',
                content: 'Deleted an actor'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-actor-delete-popup',
    template: ''
})
export class ActorDeletePopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private actorPopupService: ActorPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.actorPopupService
                .open(ActorDeleteDialogComponent as Component, params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
