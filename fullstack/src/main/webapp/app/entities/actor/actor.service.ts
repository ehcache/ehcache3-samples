import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { SERVER_API_URL } from '../../app.constants';

import { JhiDateUtils } from 'ng-jhipster';

import { Actor } from './actor.model';
import { createRequestOption } from '../../shared';

export type EntityResponseType = HttpResponse<Actor>;

@Injectable()
export class ActorService {

    private resourceUrl =  SERVER_API_URL + 'api/actors';

    constructor(private http: HttpClient, private dateUtils: JhiDateUtils) { }

    create(actor: Actor): Observable<EntityResponseType> {
        const copy = this.convert(actor);
        return this.http.post<Actor>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    update(actor: Actor): Observable<EntityResponseType> {
        const copy = this.convert(actor);
        return this.http.put<Actor>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<Actor>(`${this.resourceUrl}/${id}`, { observe: 'response'})
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    query(req?: any): Observable<HttpResponse<Actor[]>> {
        const options = createRequestOption(req);
        return this.http.get<Actor[]>(this.resourceUrl, { params: options, observe: 'response' })
            .map((res: HttpResponse<Actor[]>) => this.convertArrayResponse(res));
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response'});
    }

    private convertResponse(res: EntityResponseType): EntityResponseType {
        const body: Actor = this.convertItemFromServer(res.body);
        return res.clone({body});
    }

    private convertArrayResponse(res: HttpResponse<Actor[]>): HttpResponse<Actor[]> {
        const jsonResponse: Actor[] = res.body;
        const body: Actor[] = [];
        for (let i = 0; i < jsonResponse.length; i++) {
            body.push(this.convertItemFromServer(jsonResponse[i]));
        }
        return res.clone({body});
    }

    /**
     * Convert a returned JSON object to Actor.
     */
    private convertItemFromServer(actor: Actor): Actor {
        const copy: Actor = Object.assign({}, actor);
        copy.birthDate = this.dateUtils
            .convertLocalDateFromServer(actor.birthDate);
        return copy;
    }

    /**
     * Convert a Actor to a JSON which can be sent to the server.
     */
    private convert(actor: Actor): Actor {
        const copy: Actor = Object.assign({}, actor);
        copy.birthDate = this.dateUtils
            .convertLocalDateToServer(actor.birthDate);
        return copy;
    }
}
