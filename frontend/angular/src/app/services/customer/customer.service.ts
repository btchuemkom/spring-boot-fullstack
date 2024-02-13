import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {CustomerDTO} from "../../models/customer-dto";
import {Observable} from "rxjs";
import {environment} from "../../../environments/environment";
import {CustomerRegistrationRequest} from "../../models/customer-registration-request";
import {CustomerUpdateRequest} from "../../models/customer-update-request";

@Injectable({
  providedIn: 'root'
})
export class CustomerService {

  private http: HttpClient;

  private readonly  customerUrl = `${environment.api.baseUrl}/${environment.api.customerUrl}`
  constructor(http:HttpClient) {
    this.http = http;
  }

  findAll(): Observable<Array<CustomerDTO>> {
    // The token is automatically added to the request thanks to the service Interceptor
    return this.http.get<Array<CustomerDTO>>(this.customerUrl);
  }

  registerCustomer(customerRegistrationRequest: CustomerRegistrationRequest): Observable<void>{
    return this.http.post<void>(this.customerUrl, customerRegistrationRequest);
  }

  deleteCustomer(id: number | undefined): Observable<void>{
    return this.http.delete<void>(`${this.customerUrl}/${id}`);
  }

  updateCustomer(id: number | undefined, customer: CustomerUpdateRequest): Observable<void> {
    return this.http.put<void>(`${this.customerUrl}/${id}`, customer);
  }

}
