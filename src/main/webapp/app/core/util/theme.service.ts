import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class ThemeService {
  private readonly darkClass = 'dark-mode';

  applyTheme(setting: string | null | undefined): void {
    const isDark = setting === 'dark';
    document.body.classList.toggle(this.darkClass, isDark);
  }

  getThemeStorageKey(login: string): string {
    return `theme_${login}`;
  }

  saveThemeForUser(login: string, theme: string): void {
    localStorage.setItem(this.getThemeStorageKey(login), theme);
  }

  getThemeForUser(login: string): string | null {
    return localStorage.getItem(this.getThemeStorageKey(login));
  }

  clearTheme(): void {
    document.body.classList.remove(this.darkClass);
  }
}
