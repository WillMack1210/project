import { AfterViewInit, Component, ElementRef, inject, signal, viewChild } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { RouterModule } from '@angular/router';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';

import { EMAIL_ALREADY_USED_TYPE, LOGIN_ALREADY_USED_TYPE } from 'app/config/error.constants';
import SharedModule from 'app/shared/shared.module';
import PasswordStrengthBarComponent from '../password/password-strength-bar/password-strength-bar.component';
import { RegisterService } from './register.service';

@Component({
  standalone: true,
  selector: 'jhi-register',
  imports: [SharedModule, RouterModule, FormsModule, ReactiveFormsModule, PasswordStrengthBarComponent],
  templateUrl: './register.component.html',
})
export default class RegisterComponent implements AfterViewInit {
  login = viewChild.required<ElementRef>('login');

  doNotMatch = signal(false);
  error = signal(false);
  errorEmailExists = signal(false);
  errorUserExists = signal(false);
  success = signal(false);

  selectedImageName = signal('');
  imageError = signal('');

  profilePictureBase64: string | null = null;
  profilePictureContentType: string | null = null;

  registerForm = new FormGroup({
    login: new FormControl('', {
      nonNullable: true,
      validators: [
        Validators.required,
        Validators.minLength(1),
        Validators.maxLength(50),
        Validators.pattern('^[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$|^[_.@A-Za-z0-9-]+$'),
      ],
    }),
    email: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(5), Validators.maxLength(254), Validators.email],
    }),
    fullName: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(1), Validators.maxLength(100)],
    }),
    password: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(4), Validators.maxLength(50)],
    }),
    confirmPassword: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(4), Validators.maxLength(50)],
    }),
  });

  private readonly registerService = inject(RegisterService);

  ngAfterViewInit(): void {
    this.login().nativeElement.focus();
  }

  setFileData(event: Event): void {
    this.imageError.set('');

    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) {
      this.profilePictureBase64 = null;
      this.profilePictureContentType = null;
      this.selectedImageName.set('');
      return;
    }

    const file = input.files[0];

    if (!file.type.startsWith('image/')) {
      this.imageError.set('Please select a valid image file.');
      this.profilePictureBase64 = null;
      this.profilePictureContentType = null;
      this.selectedImageName.set('');
      return;
    }

    this.selectedImageName.set(file.name);
    this.profilePictureContentType = file.type;

    const reader = new FileReader();
    reader.onload = () => {
      const result = reader.result as string;
      const base64 = result.split(',')[1];
      this.profilePictureBase64 = base64;
    };
    reader.onerror = () => {
      this.imageError.set('Could not read the selected image.');
      this.profilePictureBase64 = null;
      this.profilePictureContentType = null;
      this.selectedImageName.set('');
    };

    reader.readAsDataURL(file);
  }

  register(): void {
    this.doNotMatch.set(false);
    this.error.set(false);
    this.errorEmailExists.set(false);
    this.errorUserExists.set(false);

    const { password, confirmPassword } = this.registerForm.getRawValue();
    if (password !== confirmPassword) {
      this.doNotMatch.set(true);
    } else {
      const { login, email, fullName } = this.registerForm.getRawValue();
      this.registerService
        .save({
          login,
          email,
          fullName,
          password,
          langKey: 'en',
          profilePicture: this.profilePictureBase64,
          profilePictureContentType: this.profilePictureContentType,
        })
        .subscribe({ next: () => this.success.set(true), error: response => this.processError(response) });
    }
  }

  private processError(response: HttpErrorResponse): void {
    if (response.status === 400 && response.error.type === LOGIN_ALREADY_USED_TYPE) {
      this.errorUserExists.set(true);
    } else if (response.status === 400 && response.error.type === EMAIL_ALREADY_USED_TYPE) {
      this.errorEmailExists.set(true);
    } else {
      this.error.set(true);
    }
  }
}
