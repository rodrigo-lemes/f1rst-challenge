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
