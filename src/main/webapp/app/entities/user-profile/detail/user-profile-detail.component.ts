import { Component, inject, input, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { DataUtils } from 'app/core/util/data-util.service';
import { IUserProfile } from '../user-profile.model';
import { AccountService } from 'app/core/auth/account.service';
import { UserProfileService } from '../service/user-profile.service';

@Component({
  standalone: true,
  selector: 'jhi-user-profile-detail',
  templateUrl: './user-profile-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class UserProfileDetailComponent implements OnInit {
  userProfile = input<IUserProfile | null>(null);
  isAdmin: boolean;
  currentUserProfileId?: number | null = null;

  protected dataUtils = inject(DataUtils);
  protected readonly userProfileService = inject(UserProfileService);

  constructor(private accountService: AccountService) {
    this.isAdmin = this.accountService.hasAnyAuthority('ROLE_ADMIN');
  }

  ngOnInit(): void {
    this.getCurrentUser();
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  previousState(): void {
    window.history.back();
  }

  getCurrentUser(): void {
    this.accountService.identity().subscribe(account => {
      if (account?.login) {
        const queryObj: any = { eagerload: true };
        this.userProfileService.query(queryObj).subscribe(resp => {
          const profiles = resp.body ?? [];
          const myProfile = profiles.find(p => p.user?.login === account.login);
          this.currentUserProfileId = myProfile?.id ?? null;
        });
      }
    });
  }
}
