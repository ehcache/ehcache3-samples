import { Injectable, Component } from '@angular/core';
import { Router } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { HttpResponse } from '@angular/common/http';
import { Actor } from './actor.model';
import { ActorService } from './actor.service';

@Injectable()
export class ActorPopupService {
    private ngbModalRef: NgbModalRef;

    constructor(
        private modalService: NgbModal,
        private router: Router,
        private actorService: ActorService

    ) {
        this.ngbModalRef = null;
    }

    open(component: Component, id?: number | any): Promise<NgbModalRef> {
        return new Promise<NgbModalRef>((resolve, reject) => {
            const isOpen = this.ngbModalRef !== null;
            if (isOpen) {
                resolve(this.ngbModalRef);
            }

            if (id) {
                this.actorService.find(id)
                    .subscribe((actorResponse: HttpResponse<Actor>) => {
                        const actor: Actor = actorResponse.body;
                        if (actor.birthDate) {
                            actor.birthDate = {
                                year: actor.birthDate.getFullYear(),
                                month: actor.birthDate.getMonth() + 1,
                                day: actor.birthDate.getDate()
                            };
                        }
                        this.ngbModalRef = this.actorModalRef(component, actor);
                        resolve(this.ngbModalRef);
                    });
            } else {
                // setTimeout used as a workaround for getting ExpressionChangedAfterItHasBeenCheckedError
                setTimeout(() => {
                    this.ngbModalRef = this.actorModalRef(component, new Actor());
                    resolve(this.ngbModalRef);
                }, 0);
            }
        });
    }

    actorModalRef(component: Component, actor: Actor): NgbModalRef {
        const modalRef = this.modalService.open(component, { size: 'lg', backdrop: 'static'});
        modalRef.componentInstance.actor = actor;
        modalRef.result.then((result) => {
            this.router.navigate([{ outlets: { popup: null }}], { replaceUrl: true, queryParamsHandling: 'merge' });
            this.ngbModalRef = null;
        }, (reason) => {
            this.router.navigate([{ outlets: { popup: null }}], { replaceUrl: true, queryParamsHandling: 'merge' });
            this.ngbModalRef = null;
        });
        return modalRef;
    }
}
