import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AddressPageResponse, AddressResponse } from './address-response';

@Injectable({
  providedIn: 'root'
})
export class AddressService {

  private readonly addressUrl = 'http://localhost:8080/address';

  constructor(private readonly httpClient: HttpClient) {
  }

  findByZipCode(zipCode: string): Observable<AddressResponse> {
    return this.httpClient.get<AddressResponse>(`${this.addressUrl}/zip/${zipCode}`);
  }

  findAll(page: number, size: number): Observable<AddressPageResponse> {
    return this.httpClient.get<AddressPageResponse>(
      `${this.addressUrl}?page=${page}&size=${size}`
    );
  }
}
