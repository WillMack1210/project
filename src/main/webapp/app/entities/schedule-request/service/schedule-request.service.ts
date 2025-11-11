import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IScheduleRequest, NewScheduleRequest } from '../schedule-request.model';

export type PartialUpdateScheduleRequest = Partial<IScheduleRequest> & Pick<IScheduleRequest, 'id'>;

type RestOf<T extends IScheduleRequest | NewScheduleRequest> = Omit<T, 'startDate' | 'endDate'> & {
  startDate?: string | null;
  endDate?: string | null;
};

export type RestScheduleRequest = RestOf<IScheduleRequest>;

export type NewRestScheduleRequest = RestOf<NewScheduleRequest>;

export type PartialUpdateRestScheduleRequest = RestOf<PartialUpdateScheduleRequest>;

export type EntityResponseType = HttpResponse<IScheduleRequest>;
export type EntityArrayResponseType = HttpResponse<IScheduleRequest[]>;

@Injectable({ providedIn: 'root' })
export class ScheduleRequestService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/schedule-requests');

  create(scheduleRequest: NewScheduleRequest): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(scheduleRequest);
    return this.http
      .post<RestScheduleRequest>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(scheduleRequest: IScheduleRequest): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(scheduleRequest);
    return this.http
      .put<RestScheduleRequest>(`${this.resourceUrl}/${this.getScheduleRequestIdentifier(scheduleRequest)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(scheduleRequest: PartialUpdateScheduleRequest): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(scheduleRequest);
    return this.http
      .patch<RestScheduleRequest>(`${this.resourceUrl}/${this.getScheduleRequestIdentifier(scheduleRequest)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestScheduleRequest>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestScheduleRequest[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getScheduleRequestIdentifier(scheduleRequest: Pick<IScheduleRequest, 'id'>): number {
    return scheduleRequest.id;
  }

  compareScheduleRequest(o1: Pick<IScheduleRequest, 'id'> | null, o2: Pick<IScheduleRequest, 'id'> | null): boolean {
    return o1 && o2 ? this.getScheduleRequestIdentifier(o1) === this.getScheduleRequestIdentifier(o2) : o1 === o2;
  }

  addScheduleRequestToCollectionIfMissing<Type extends Pick<IScheduleRequest, 'id'>>(
    scheduleRequestCollection: Type[],
    ...scheduleRequestsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const scheduleRequests: Type[] = scheduleRequestsToCheck.filter(isPresent);
    if (scheduleRequests.length > 0) {
      const scheduleRequestCollectionIdentifiers = scheduleRequestCollection.map(scheduleRequestItem =>
        this.getScheduleRequestIdentifier(scheduleRequestItem),
      );
      const scheduleRequestsToAdd = scheduleRequests.filter(scheduleRequestItem => {
        const scheduleRequestIdentifier = this.getScheduleRequestIdentifier(scheduleRequestItem);
        if (scheduleRequestCollectionIdentifiers.includes(scheduleRequestIdentifier)) {
          return false;
        }
        scheduleRequestCollectionIdentifiers.push(scheduleRequestIdentifier);
        return true;
      });
      return [...scheduleRequestsToAdd, ...scheduleRequestCollection];
    }
    return scheduleRequestCollection;
  }

  protected convertDateFromClient<T extends IScheduleRequest | NewScheduleRequest | PartialUpdateScheduleRequest>(
    scheduleRequest: T,
  ): RestOf<T> {
    return {
      ...scheduleRequest,
      startDate: scheduleRequest.startDate?.toJSON() ?? null,
      endDate: scheduleRequest.endDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restScheduleRequest: RestScheduleRequest): IScheduleRequest {
    return {
      ...restScheduleRequest,
      startDate: restScheduleRequest.startDate ? dayjs(restScheduleRequest.startDate) : undefined,
      endDate: restScheduleRequest.endDate ? dayjs(restScheduleRequest.endDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestScheduleRequest>): HttpResponse<IScheduleRequest> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestScheduleRequest[]>): HttpResponse<IScheduleRequest[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
