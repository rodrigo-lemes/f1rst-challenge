export interface AddressResponse {
  zipCode: string;
  street?: string;
  complement?: string;
  neighborhood?: string;
  city: string;
  state: string;
  covered: boolean;
  source: 'DATABASE' | 'EXTERNAL_API';
}

export interface AddressPageResponse {
  content: AddressResponse[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}
