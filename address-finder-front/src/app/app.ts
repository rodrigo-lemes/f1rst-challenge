import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { AddressService } from './address.service';
import { AddressResponse } from './address-response';

@Component({
  selector: 'app-root',
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {

  zipCode = signal('');
  address = signal<AddressResponse | undefined>(undefined);
  loading = signal(false);
  errorMessage = signal('');

  constructor(private readonly addressService: AddressService) {
  }

  searchAddress(): void {
    this.errorMessage.set('');
    this.address.set(undefined);

    const normalizedZipCode = this.zipCode().trim();

    if (!normalizedZipCode) {
      this.errorMessage.set('Informe um CEP para consulta.');
      return;
    }

    this.loading.set(true);

    this.addressService.findByZipCode(normalizedZipCode).subscribe({
      next: (response) => {
        console.log('Address response:', response);

        this.address.set(response);
        this.loading.set(false);
      },
      error: (error: HttpErrorResponse) => {
        console.error('Address search error:', error);

        this.errorMessage.set(this.resolveErrorMessage(error));
        this.loading.set(false);
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
