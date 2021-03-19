import { APP_INITIALIZER } from '@angular/core';
import { ThemeService } from 'app/shared/theme/theme.service';

export const AppInitializerProvider = {
  provide: APP_INITIALIZER,
  useFactory: (themeService: ThemeService) => () => themeService.loadTheme(),
  deps: [ThemeService],
  multi: true,
};
