import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs/Subscription';
import { JhiEventManager } from 'ng-jhipster';

import { Actor } from './actor.model';
import { ActorService } from './actor.service';

@Component({
    selector: 'jhi-actor-detail',
    templateUrl: './actor-detail.component.html'
})
export class ActorDetailComponent implements OnInit, OnDestroy {

    actor: Actor;
    private subscription: Subscription;
    private eventSubscriber: Subscription;

    constructor(
        private eventManager: JhiEventManager,
        private actorService: ActorService,
        private route: ActivatedRoute
    ) {
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
        this.registerChangeInActors();
    }

    load(id) {
        this.actorService.find(id)
            .subscribe((actorResponse: HttpResponse<Actor>) => {
                this.actor = actorResponse.body;
            });
    }
    previousState() {
        window.history.back();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.eventManager.destroy(this.eventSubscriber);
    }

    registerChangeInActors() {
        this.eventSubscriber = this.eventManager.subscribe(
            'actorListModification',
            (response) => this.load(this.actor.id)
        );
    }
}
