import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IFindTime, NewFindTime } from '../find-time.model';

export type PartialUpdateFindTime = Partial<IFindTime> & Pick<IFindTime, 'id'>;

type RestOf<T extends IFindTime | NewFindTime> = Omit<T, 'requestStart' | 'requestEnd'> & {
  requestStart?: string | null;
  requestEnd?: string | null;
};

export type RestFindTime = RestOf<IFindTime>;

export type NewRestFindTime = RestOf<NewFindTime>;

export type PartialUpdateRestFindTime = RestOf<PartialUpdateFindTime>;

export type EntityResponseType = HttpResponse<IFindTime>;
export type EntityArrayResponseType = HttpResponse<IFindTime[]>;

@Injectable({ providedIn: 'root' })
export class FindTimeService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/find-times');

  create(findTime: NewFindTime): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(findTime);
    return this.http
      .post<RestFindTime>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(findTime: IFindTime): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(findTime);
    return this.http
      .put<RestFindTime>(`${this.resourceUrl}/${this.getFindTimeIdentifier(findTime)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(findTime: PartialUpdateFindTime): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(findTime);
    return this.http
      .patch<RestFindTime>(`${this.resourceUrl}/${this.getFindTimeIdentifier(findTime)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestFindTime>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestFindTime[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getFindTimeIdentifier(findTime: Pick<IFindTime, 'id'>): number {
    return findTime.id;
  }

  compareFindTime(o1: Pick<IFindTime, 'id'> | null, o2: Pick<IFindTime, 'id'> | null): boolean {
    return o1 && o2 ? this.getFindTimeIdentifier(o1) === this.getFindTimeIdentifier(o2) : o1 === o2;
  }

  addFindTimeToCollectionIfMissing<Type extends Pick<IFindTime, 'id'>>(
    findTimeCollection: Type[],
    ...findTimesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const findTimes: Type[] = findTimesToCheck.filter(isPresent);
    if (findTimes.length > 0) {
      const findTimeCollectionIdentifiers = findTimeCollection.map(findTimeItem => this.getFindTimeIdentifier(findTimeItem));
      const findTimesToAdd = findTimes.filter(findTimeItem => {
        const findTimeIdentifier = this.getFindTimeIdentifier(findTimeItem);
        if (findTimeCollectionIdentifiers.includes(findTimeIdentifier)) {
          return false;
        }
        findTimeCollectionIdentifiers.push(findTimeIdentifier);
        return true;
      });
      return [...findTimesToAdd, ...findTimeCollection];
    }
    return findTimeCollection;
  }

  protected convertDateFromClient<T extends IFindTime | NewFindTime | PartialUpdateFindTime>(findTime: T): RestOf<T> {
    return {
      ...findTime,
      requestStart: findTime.requestStart?.toJSON() ?? null,
      requestEnd: findTime.requestEnd?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restFindTime: RestFindTime): IFindTime {
    return {
      ...restFindTime,
      requestStart: restFindTime.requestStart ? dayjs(restFindTime.requestStart) : undefined,
      requestEnd: restFindTime.requestEnd ? dayjs(restFindTime.requestEnd) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestFindTime>): HttpResponse<IFindTime> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestFindTime[]>): HttpResponse<IFindTime[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
