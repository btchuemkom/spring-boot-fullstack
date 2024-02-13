import {Component, EventEmitter, Input, Output} from '@angular/core';
import {CustomerRegistrationRequest} from "../../models/customer-registration-request";

@Component({
  selector: 'app-manage-customer',
  templateUrl: './manage-customer.component.html',
  styleUrls: ['./manage-customer.component.scss']
})
export class ManageCustomerComponent {


  @Input()
  customerRegistrationRequest: CustomerRegistrationRequest = {};

  @Input()
  operation: 'create' | 'update' = 'create';


  @Output() // Send the event from child to the parent using EventEmitter
  submit: EventEmitter<CustomerRegistrationRequest> = new EventEmitter<CustomerRegistrationRequest>();

  @Output()
  cancel: EventEmitter<void> = new EventEmitter<void>();

  get isCustomerValid(): boolean {
    return this.hasLength(this.customerRegistrationRequest.name) &&
      this.hasLength(this.customerRegistrationRequest.email) &&
      this.customerRegistrationRequest.age !== undefined && this.customerRegistrationRequest.age > 0 &&
      (
        this.operation === 'update' ||
        this.hasLength(this.customerRegistrationRequest.password) &&
        this.hasLength(this.customerRegistrationRequest.gender)
      );


  }

  private hasLength(input: string | undefined): boolean{
    return input !== null && input !== undefined && input.length > 0;
  }

  onSubmit() {
    this.submit.emit(this.customerRegistrationRequest)
  }


  onCancel() {
    this.cancel.emit();
  }
}
