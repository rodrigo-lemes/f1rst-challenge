import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AddressResponse } from './address-response';

@Injectable({
  providedIn: 'root'
})
export class AddressService {

  private readonly apiUrl = 'http://localhost:8080/address/zip';

  constructor(private readonly httpClient: HttpClient) {
  }

  findByZipCode(zipCode: string): Observable<AddressResponse> {
    return this.httpClient.get<AddressResponse>(`${this.apiUrl}/${zipCode}`);
  }
}
