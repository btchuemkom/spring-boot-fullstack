import { Component } from '@angular/core';
import {AuthenticationService} from "../../services/authentication/authentication.service";
import {AuthenticationRequest} from "../../models/authentication-request";
import {Router} from "@angular/router";
import {CustomerService} from "../../services/customer/customer.service";
import {CustomerDTO} from "../../models/customer-dto";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {

  authenticationRequest: AuthenticationRequest = {};
  errorMsg = '';

  constructor(
    private authenticationService: AuthenticationService,
    private router: Router
  ) {}

  login() {
    this.errorMsg = '';
    this.authenticationService.login(this.authenticationRequest)
      .subscribe(
        {next: (authenticationResponse) => {
            localStorage.setItem('user', JSON.stringify(authenticationResponse))
            this.router.navigate(['customers'])

          },
          error: (err) => {
            if(err.error.statusCode === 401) {
              this.errorMsg = 'Login and / or password is incorrect';
            }
          }
        });
  }

  register() {
    this.router.navigate(['register']);
  }
}
