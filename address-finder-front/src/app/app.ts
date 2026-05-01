import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { AddressService } from './address.service';
import { AddressPageResponse, AddressResponse } from './address-response';

type ActiveTab = 'SEARCH' | 'LIST';

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

  activeTab = signal<ActiveTab>('SEARCH');

  zipCode = signal('');
  address = signal<AddressResponse | undefined>(undefined);

  addressPage = signal<AddressPageResponse | undefined>(undefined);

  loading = signal(false);
  listLoading = signal(false);

  errorMessage = signal('');
  listErrorMessage = signal('');

  currentPage = signal(0);
  pageSize = signal(10);

  constructor(private readonly addressService: AddressService) {
  }

  showSearchTab(): void {
    this.activeTab.set('SEARCH');
  }

  showListTab(): void {
    this.activeTab.set('LIST');
    this.loadAddresses(0);
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
        this.address.set(response);
        this.loading.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.errorMessage.set(this.resolveErrorMessage(error));
        this.loading.set(false);
      }
    });
  }

  loadAddresses(page: number = this.currentPage()): void {
    this.listErrorMessage.set('');
    this.listLoading.set(true);

    this.addressService.findAll(page, this.pageSize()).subscribe({
      next: (response) => {
        this.addressPage.set(response);
        this.currentPage.set(response.page);
        this.listLoading.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.listErrorMessage.set(this.resolveErrorMessage(error));
        this.listLoading.set(false);
      }
    });
  }

  previousPage(): void {
    if (this.currentPage() === 0) {
      return;
    }

    this.loadAddresses(this.currentPage() - 1);
  }

  nextPage(): void {
    const page = this.addressPage();

    if (!page || page.last) {
      return;
    }

    this.loadAddresses(this.currentPage() + 1);
  }

  private resolveErrorMessage(error: HttpErrorResponse): string {
    if (error.status === 400) {
      return 'Requisição inválida. Verifique os dados informados.';
    }

    if (error.status === 404) {
      return 'CEP não encontrado.';
    }

    if (error.status === 502) {
      return 'Erro ao consultar a API externa de endereço.';
    }

    return 'Erro inesperado ao buscar os dados.';
  }
}
