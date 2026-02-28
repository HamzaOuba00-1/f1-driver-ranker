import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CompareResponse } from '../models/compare.models';

@Injectable({ providedIn: 'root' })
export class CompareApiService {
  constructor(private readonly http: HttpClient) {}

  compare(driverIds: string[], from?: number, to?: number, mode?: string) {
    const drivers = driverIds.join(',');

    let params = new HttpParams().set('drivers', drivers);
    if (from != null) params = params.set('from', String(from));
    if (to != null) params = params.set('to', String(to));
    if (mode) params = params.set('mode', mode);

    return this.http.get<CompareResponse>('/api/compare', { params });
  }
}
