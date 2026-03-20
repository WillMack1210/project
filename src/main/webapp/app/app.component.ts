import { Component, OnInit, inject } from '@angular/core';
import { registerLocaleData } from '@angular/common';
import dayjs from 'dayjs/esm';
import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { NgbDatepickerConfig } from '@ng-bootstrap/ng-bootstrap';
import locale from '@angular/common/locales/en';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { fontAwesomeIcons } from './config/font-awesome-icons';
import { ThemeService } from './core/util/theme.service';
import { AccountService } from 'app/core/auth/account.service';
import { UserProfileService } from 'app/entities/user-profile/service/user-profile.service';
import MainComponent from './layouts/main/main.component';

@Component({
  standalone: true,
  selector: 'jhi-app',
  template: '<jhi-main></jhi-main>',
  imports: [MainComponent],
})
export default class AppComponent implements OnInit {
  private readonly applicationConfigService = inject(ApplicationConfigService);
  private readonly iconLibrary = inject(FaIconLibrary);
  private readonly dpConfig = inject(NgbDatepickerConfig);
  private readonly themeService = inject(ThemeService);
  private readonly accountService = inject(AccountService);
  private readonly userProfileService = inject(UserProfileService);

  constructor() {
    this.applicationConfigService.setEndpointPrefix(SERVER_API_URL);
    registerLocaleData(locale);
    this.iconLibrary.addIcons(...fontAwesomeIcons);
    this.dpConfig.minDate = { year: dayjs().subtract(100, 'year').year(), month: 1, day: 1 };
  }

  ngOnInit(): void {
    this.accountService.getAuthenticationState().subscribe(account => {
      this.loadThemeForAccount(account);
    });
  }

  private loadThemeForAccount(account: { login?: string | null } | null): void {
    if (!account?.login) {
      this.themeService.applyTheme('light');
      return;
    }

    const login = account.login;
    const savedTheme = this.themeService.getThemeForUser(login) ?? 'light';

    // Apply user-specific cached theme immediately
    this.themeService.applyTheme(savedTheme);

    const queryObj = { eagerload: true };

    this.userProfileService.query(queryObj).subscribe({
      next: resp => {
        const profiles = resp.body ?? [];
        const myProfile = profiles.find(profile => profile.user?.login === login);

        const profileTheme = myProfile?.settings ?? 'light';

        this.themeService.applyTheme(profileTheme);
        this.themeService.saveThemeForUser(login, profileTheme);
      },
      error: () => {
        this.themeService.applyTheme(savedTheme);
      },
    });
  }
}
