import {Component, Input, OnInit} from '@angular/core';
import {CustomerService} from "../../services/customer/customer.service";
import {CustomerDTO} from "../../models/customer-dto";
import {CustomerRegistrationRequest} from "../../models/customer-registration-request";
import {ConfirmationService, MessageService} from "primeng/api";

@Component({
  selector: 'app-customer',
  templateUrl: './customer.component.html',
  styleUrls: ['./customer.component.scss']
})
export class CustomerComponent implements OnInit {

  private customerService: CustomerService;
  private messageService: MessageService;
  private confirmationService:  ConfirmationService

  display: boolean = false;
  customers: Array<CustomerDTO> = [];
  customer: CustomerRegistrationRequest = {};
  operation: 'create' | 'update' = 'create';

  constructor(customerService: CustomerService, messageService: MessageService, confirmationService:  ConfirmationService) {
    this.customerService = customerService;
    this.messageService = messageService;
    this.confirmationService = confirmationService;
  }

  ngOnInit(): void {
    this.findAllCustomers();
  }

  private findAllCustomers() {
     return this.customerService.findAll().subscribe({
       next: (data) => {
         this.customers = data;
       },
       error: () => {

       }
     });
  }

  saveCustomer(customerRequest: CustomerRegistrationRequest) {
    if (customerRequest) {
      if (this.operation === 'create') {
        this.customerService.registerCustomer(customerRequest)
          .subscribe(
            {
              next: (data) => {
                this.findAllCustomers();
                this.display = false; // remove the sidebar
                this.customer = {};
                this.messageService.add(
                  {
                    severity: 'success',
                    summary: 'Customer saved',
                    detail: `Customer ${customerRequest.name} was successfully saved`
                  }
                );
              },
              error: () => {

              }
            }
          );
      } else if (this.operation === 'update') {
        this.customerService.updateCustomer(customerRequest.id, customerRequest)
          .subscribe({
            next: () => {
              this.findAllCustomers();
              this.display = false; // remove the sidebar
              this.customer = {};
              this.messageService.add(
                {
                  severity: 'success',
                  summary: 'Customer updated',
                  detail: `Customer ${customerRequest.name} was successfully updated`
                }
              );
            }
          });
      }
    }
  }

  deleteCustomer(evenCustomer: CustomerDTO) {
    this.confirmationService.confirm({
      header: 'Delete customer',
      message: `Are you sure you want to delete ${evenCustomer.name}? You can\'t undo this action afterwords`,
      accept: () => {
        this.customerService.deleteCustomer(evenCustomer.id).subscribe({
          next: () => {
            this.findAllCustomers();
            this.display = false; // remove the sidebar
            this.messageService.add(
              {
                severity: 'success',
                summary: 'Customer deleted',
                detail: `Customer ${evenCustomer.name} was successfully deleted`
              }
            );
          }
        });
      }
    })
  }

  updateCustomer(evenCustomer: CustomerDTO) {
    this.display = true;
    this.customer = evenCustomer;
    this.operation = 'update';
  }

  createCustomer() {
    this.display = true;
    this.customer = {};
    this.operation = 'create';
  }

  cancel() {
    this.display = false;
    this.customer = {};
    this.operation = 'create';
  }
}
