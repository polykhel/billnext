import { Injectable } from '@angular/core';

export enum ThemeType {
  DARK = 'dark',
  LIGHT = 'default',
}

@Injectable({
  providedIn: 'root',
})
export class ThemeService {
  currentTheme = ThemeType.LIGHT;

  private static reverseTheme(theme: string): ThemeType {
    return theme === ThemeType.DARK ? ThemeType.LIGHT : ThemeType.DARK;
  }

  private static removeUnusedTheme(theme: ThemeType): void {
    document.documentElement.classList.remove(theme);
    const removedThemeStyle = document.getElementById(theme);
    if (removedThemeStyle) {
      document.head.removeChild(removedThemeStyle);
    }
  }

  public loadTheme(firstLoad = true): Promise<Event> {
    const theme = this.currentTheme;
    if (firstLoad) {
      document.documentElement.classList.add(theme);
    }
    return new Promise<Event>((resolve, reject) => {
      this.loadCss(`${theme}.css`, theme).then(
        e => {
          if (!firstLoad) {
            document.documentElement.classList.add(theme);
          }
          ThemeService.removeUnusedTheme(ThemeService.reverseTheme(theme));
          resolve(e);
        },
        e => reject(e)
      );
    });
  }

  public toggleTheme(): Promise<Event> {
    this.currentTheme = ThemeService.reverseTheme(this.currentTheme);
    return this.loadTheme(false);
  }

  private loadCss(href: string, id: string): Promise<Event> {
    return new Promise<Event>((resolve, reject) => {
      const style = document.createElement('link');
      style.rel = 'stylesheet';
      style.href = href;
      style.id = id;
      style.onload = resolve;
      style.onerror = reject;
      document.head.append(style);
    });
  }
}
