import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClientModule, HttpErrorResponse } from '@angular/common/http';
import { AddressService } from './address.service';
import { AddressResponse } from './address-response';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    HttpClientModule
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {

  zipCode = '';
  address?: AddressResponse;
  loading = false;
  errorMessage = '';

  constructor(private readonly addressService: AddressService) {
  }

  searchAddress(): void {
    this.errorMessage = '';
    this.address = undefined;

    const normalizedZipCode = this.zipCode.trim();

    if (!normalizedZipCode) {
      this.errorMessage = 'Informe um CEP para consulta.';
      return;
    }

    this.loading = true;

    this.addressService.findByZipCode(normalizedZipCode).subscribe({
      next: (response) => {
        this.address = response;
        this.loading = false;
      },
      error: (error: HttpErrorResponse) => {
        this.errorMessage = this.resolveErrorMessage(error);
        this.loading = false;
      }
    });
  }

  private resolveErrorMessage(error: HttpErrorResponse): string {
    if (error.status === 400) {
      return 'CEP inválido. Use o formato 13458870 ou 13458-870.';
    }

    if (error.status === 404) {
      return 'CEP não encontrado.';
    }

    if (error.status === 502) {
      return 'Erro ao consultar a API externa de endereço.';
    }

    return 'Erro inesperado ao buscar o endereço.';
  }
}
