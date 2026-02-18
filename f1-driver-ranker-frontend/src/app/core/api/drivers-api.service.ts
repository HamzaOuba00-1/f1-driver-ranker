import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DriverSuggestion } from '../models/driver.models';

@Injectable({ providedIn: 'root' })
export class DriversApiService {
  constructor(private readonly http: HttpClient) {}

  search(query: string): Observable<DriverSuggestion[]> {
    const sanitized = this.sanitizeQuery(query);
    const params = new HttpParams().set('query', sanitized);

    return this.http.get<DriverSuggestion[]>('/api/drivers', { params });
  }

  private sanitizeQuery(raw: string): string {
    const q = (raw ?? '').trim();
    // Keep same “safe” character set as backend; if it becomes empty, backend will return []
    return q.replace(/[^\p{L}0-9 .\-']/gu, '').slice(0, 50);
  }
}
