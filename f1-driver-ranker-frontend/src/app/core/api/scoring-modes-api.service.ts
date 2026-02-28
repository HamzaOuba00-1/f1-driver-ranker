import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ScoringModeDto } from '../models/scoring-mode.models';

@Injectable({ providedIn: 'root' })
export class ScoringModesApiService {
  constructor(private readonly http: HttpClient) {}

  list(): Observable<ScoringModeDto[]> {
    return this.http.get<ScoringModeDto[]>('/api/scoring-modes');
  }
}
