import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { SERVER_API_URL } from '../../app.constants';

import { JhiDateUtils } from 'ng-jhipster';

import { Star } from './star.model';
import { createRequestOption } from '../../shared';

export type EntityResponseType = HttpResponse<Star>;

@Injectable()
export class StarService {

    private resourceUrl =  SERVER_API_URL + 'api/stars';
    private actorUrl =  SERVER_API_URL + 'api/actors';

    constructor(private http: HttpClient, private dateUtils: JhiDateUtils) { }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<Star>(`${this.resourceUrl}/${id}`, { observe: 'response'})
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    query(req?: any): Observable<HttpResponse<Star[]>> {
        const options = createRequestOption(req);
        return this.http.get<Star[]>(this.actorUrl, { params: options, observe: 'response' })
            .map((res: HttpResponse<Star[]>) => this.convertArrayResponse(res));
    }

    private convertResponse(res: EntityResponseType): EntityResponseType {
        const body: Star = this.convertItemFromServer(res.body);
        return res.clone({body});
    }

    private convertArrayResponse(res: HttpResponse<Star[]>): HttpResponse<Star[]> {
        const jsonResponse: Star[] = res.body;
        const body: Star[] = [];
        for (let i = 0; i < jsonResponse.length; i++) {
            body.push(this.convertItemFromServer(jsonResponse[i]));
        }
        return res.clone({body});
    }

    /**
     * Convert a returned JSON object to Star.
     */
    private convertItemFromServer(star: Star): Star {
        const copy: Star = Object.assign({}, star);
        copy.birthDate = this.dateUtils
            .convertLocalDateFromServer(star.birthDate);
        return copy;
    }

}
