import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { Star } from './star.model';

@Component({
    selector: 'jhi-star-detail',
    templateUrl: './star-detail.component.html'
})
export class StarDetailComponent implements OnInit {
    star: Star;

    constructor(private activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ star }) => {
            this.star = star;
        });
    }

    previousState() {
        window.history.back();
    }
}
