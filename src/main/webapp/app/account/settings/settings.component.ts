import { Component, OnInit, inject, signal } from '@angular/core';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';

import SharedModule from 'app/shared/shared.module';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/auth/account.model';
import { ThemeService } from 'app/core/util/theme.service';
import { UserProfileService } from 'app/entities/user-profile/service/user-profile.service';
import { IUserProfile } from 'app/entities/user-profile/user-profile.model';

const initialAccount: Account = {} as Account;

@Component({
  standalone: true,
  selector: 'jhi-settings',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
  templateUrl: './settings.component.html',
})
export default class SettingsComponent implements OnInit {
  success = signal(false);
  currentUserProfileId?: number | null = null;
  currentUserProfile?: IUserProfile | null = null;
  currentLogin?: string | null = null;

  settingsForm = new FormGroup({
    firstName: new FormControl(initialAccount.firstName, {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(1), Validators.maxLength(50)],
    }),
    lastName: new FormControl(initialAccount.lastName, {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(1), Validators.maxLength(50)],
    }),
    email: new FormControl(initialAccount.email, {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(5), Validators.maxLength(254), Validators.email],
    }),
    langKey: new FormControl(initialAccount.langKey, { nonNullable: true }),
    activated: new FormControl(initialAccount.activated, { nonNullable: true }),
    authorities: new FormControl(initialAccount.authorities, { nonNullable: true }),
    imageUrl: new FormControl(initialAccount.imageUrl, { nonNullable: true }),
    login: new FormControl(initialAccount.login, { nonNullable: true }),
    settings: new FormControl('light', { nonNullable: true }),
  });

  private readonly accountService = inject(AccountService);
  private readonly themeService = inject(ThemeService);
  private readonly userProfileService = inject(UserProfileService);

  ngOnInit(): void {
    this.accountService.identity().subscribe(account => {
      if (account) {
        this.currentLogin = account.login;
        this.settingsForm.patchValue(account);
      }
    });

    this.getCurrentUser();
  }

  save(): void {
    this.success.set(false);

    const account = this.settingsForm.getRawValue();
    const selectedTheme = this.settingsForm.get('settings')?.value ?? 'light';
    const login = this.currentLogin ?? this.settingsForm.get('login')?.value ?? null;

    this.accountService.save(account).subscribe(() => {
      if (this.currentUserProfile) {
        const updatedProfile: IUserProfile = {
          ...this.currentUserProfile,
          settings: selectedTheme,
        };

        this.userProfileService.update(updatedProfile).subscribe(() => {
          this.success.set(true);
          this.accountService.authenticate(account);
          this.themeService.applyTheme(selectedTheme);

          if (login) {
            this.themeService.saveThemeForUser(login, selectedTheme);
          }
        });
      } else {
        this.success.set(true);
        this.accountService.authenticate(account);
        this.themeService.applyTheme(selectedTheme);

        if (login) {
          this.themeService.saveThemeForUser(login, selectedTheme);
        }
      }
    });
  }

  onThemeToggle(enabled: boolean): void {
    const newTheme = enabled ? 'dark' : 'light';

    this.settingsForm.patchValue({
      settings: newTheme,
    });

    this.themeService.applyTheme(newTheme);
  }

  getCurrentUser(): void {
    this.accountService.identity().subscribe(account => {
      if (!account?.login) {
        this.currentUserProfile = null;
        this.currentUserProfileId = null;
        this.currentLogin = null;

        this.settingsForm.patchValue({
          settings: 'light',
        });

        this.themeService.applyTheme('light');
        return;
      }

      this.currentLogin = account.login;

      const queryObj = { eagerload: true };

      this.userProfileService.query(queryObj).subscribe({
        next: resp => {
          const profiles = resp.body ?? [];
          const myProfile = profiles.find(p => p.user?.login === account.login) ?? null;

          this.currentUserProfile = myProfile;
          this.currentUserProfileId = myProfile?.id ?? null;

          const savedTheme = myProfile?.settings ?? 'light';

          this.settingsForm.patchValue({
            settings: savedTheme,
          });

          this.themeService.applyTheme(savedTheme);
          this.themeService.saveThemeForUser(account.login, savedTheme);
        },
        error: () => {
          this.currentUserProfile = null;
          this.currentUserProfileId = null;

          this.settingsForm.patchValue({
            settings: 'light',
          });

          this.themeService.applyTheme('light');
          this.themeService.saveThemeForUser(account.login, 'light');
        },
      });
    });
  }
}
