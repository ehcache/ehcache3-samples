import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IActor } from 'app/shared/model/actor.model';

@Component({
    selector: 'jhi-actor-detail',
    templateUrl: './actor-detail.component.html'
})
export class ActorDetailComponent implements OnInit {
    actor: IActor;

    constructor(private activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ actor }) => {
            this.actor = actor;
        });
    }

    previousState() {
        window.history.back();
    }
}
