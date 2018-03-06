import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs/Subscription';
import { JhiEventManager } from 'ng-jhipster';

import { Star } from './star.model';
import { StarService } from './star.service';

@Component({
    selector: 'jhi-star-detail',
    templateUrl: './star-detail.component.html'
})
export class StarDetailComponent implements OnInit, OnDestroy {

    star: Star;
    private subscription: Subscription;
    private eventSubscriber: Subscription;

    constructor(
        private eventManager: JhiEventManager,
        private starService: StarService,
        private route: ActivatedRoute
    ) {
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
        this.registerChangeInStars();
    }

    load(id) {
        this.starService.find(id)
            .subscribe((starResponse: HttpResponse<Star>) => {
                this.star = starResponse.body;
            });
    }
    previousState() {
        window.history.back();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.eventManager.destroy(this.eventSubscriber);
    }

    registerChangeInStars() {
        this.eventSubscriber = this.eventManager.subscribe(
            'starListModification',
            (response) => this.load(this.star.id)
        );
    }
}
