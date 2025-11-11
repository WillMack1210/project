import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IAvailiabilityBlock, NewAvailiabilityBlock } from '../availiability-block.model';

export type PartialUpdateAvailiabilityBlock = Partial<IAvailiabilityBlock> & Pick<IAvailiabilityBlock, 'id'>;

type RestOf<T extends IAvailiabilityBlock | NewAvailiabilityBlock> = Omit<T, 'startDateTime' | 'endDateTime'> & {
  startDateTime?: string | null;
  endDateTime?: string | null;
};

export type RestAvailiabilityBlock = RestOf<IAvailiabilityBlock>;

export type NewRestAvailiabilityBlock = RestOf<NewAvailiabilityBlock>;

export type PartialUpdateRestAvailiabilityBlock = RestOf<PartialUpdateAvailiabilityBlock>;

export type EntityResponseType = HttpResponse<IAvailiabilityBlock>;
export type EntityArrayResponseType = HttpResponse<IAvailiabilityBlock[]>;

@Injectable({ providedIn: 'root' })
export class AvailiabilityBlockService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/availiability-blocks');

  create(availiabilityBlock: NewAvailiabilityBlock): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(availiabilityBlock);
    return this.http
      .post<RestAvailiabilityBlock>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(availiabilityBlock: IAvailiabilityBlock): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(availiabilityBlock);
    return this.http
      .put<RestAvailiabilityBlock>(`${this.resourceUrl}/${this.getAvailiabilityBlockIdentifier(availiabilityBlock)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(availiabilityBlock: PartialUpdateAvailiabilityBlock): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(availiabilityBlock);
    return this.http
      .patch<RestAvailiabilityBlock>(`${this.resourceUrl}/${this.getAvailiabilityBlockIdentifier(availiabilityBlock)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestAvailiabilityBlock>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestAvailiabilityBlock[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getAvailiabilityBlockIdentifier(availiabilityBlock: Pick<IAvailiabilityBlock, 'id'>): number {
    return availiabilityBlock.id;
  }

  compareAvailiabilityBlock(o1: Pick<IAvailiabilityBlock, 'id'> | null, o2: Pick<IAvailiabilityBlock, 'id'> | null): boolean {
    return o1 && o2 ? this.getAvailiabilityBlockIdentifier(o1) === this.getAvailiabilityBlockIdentifier(o2) : o1 === o2;
  }

  addAvailiabilityBlockToCollectionIfMissing<Type extends Pick<IAvailiabilityBlock, 'id'>>(
    availiabilityBlockCollection: Type[],
    ...availiabilityBlocksToCheck: (Type | null | undefined)[]
  ): Type[] {
    const availiabilityBlocks: Type[] = availiabilityBlocksToCheck.filter(isPresent);
    if (availiabilityBlocks.length > 0) {
      const availiabilityBlockCollectionIdentifiers = availiabilityBlockCollection.map(availiabilityBlockItem =>
        this.getAvailiabilityBlockIdentifier(availiabilityBlockItem),
      );
      const availiabilityBlocksToAdd = availiabilityBlocks.filter(availiabilityBlockItem => {
        const availiabilityBlockIdentifier = this.getAvailiabilityBlockIdentifier(availiabilityBlockItem);
        if (availiabilityBlockCollectionIdentifiers.includes(availiabilityBlockIdentifier)) {
          return false;
        }
        availiabilityBlockCollectionIdentifiers.push(availiabilityBlockIdentifier);
        return true;
      });
      return [...availiabilityBlocksToAdd, ...availiabilityBlockCollection];
    }
    return availiabilityBlockCollection;
  }

  protected convertDateFromClient<T extends IAvailiabilityBlock | NewAvailiabilityBlock | PartialUpdateAvailiabilityBlock>(
    availiabilityBlock: T,
  ): RestOf<T> {
    return {
      ...availiabilityBlock,
      startDateTime: availiabilityBlock.startDateTime?.toJSON() ?? null,
      endDateTime: availiabilityBlock.endDateTime?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restAvailiabilityBlock: RestAvailiabilityBlock): IAvailiabilityBlock {
    return {
      ...restAvailiabilityBlock,
      startDateTime: restAvailiabilityBlock.startDateTime ? dayjs(restAvailiabilityBlock.startDateTime) : undefined,
      endDateTime: restAvailiabilityBlock.endDateTime ? dayjs(restAvailiabilityBlock.endDateTime) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestAvailiabilityBlock>): HttpResponse<IAvailiabilityBlock> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestAvailiabilityBlock[]>): HttpResponse<IAvailiabilityBlock[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
