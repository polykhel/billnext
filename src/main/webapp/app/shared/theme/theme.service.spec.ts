import { ThemeService, ThemeType } from 'app/shared/theme/theme.service';

describe('Service Tests', () => {
  describe('Theme Service', () => {
    let service: ThemeService;

    beforeEach(() => {
      service = new ThemeService();
    });

    describe('loadTheme', () => {
      it('should add default theme on first load', () => {
        service.loadTheme(true);

        expect(document.documentElement.classList.contains('default')).toBe(true);
      });

      it('should add default class on NOT first load', () => {
        service.currentTheme = ThemeType.LIGHT;

        service.loadTheme(false).then(() => {
          expect(document.documentElement.classList.contains('default')).toBe(true);
          expect(document.documentElement.classList.contains('dark')).toBe(false);
        });
      });

      it('should add dark class on NOT first load', () => {
        service.currentTheme = ThemeType.DARK;

        service.loadTheme(false).then(() => {
          expect(document.documentElement.classList.contains('dark')).toBe(true);
          expect(document.documentElement.classList.contains('default')).toBe(false);
        });
      });
    });

    describe('toggleTheme', () => {
      it('should toggle default theme to dark theme', () => {
        service.toggleTheme();

        expect(service.currentTheme).toEqual('dark');
      });

      it('should call loadTheme with false', () => {
        let spy = jest.spyOn(service, 'loadTheme');

        service.toggleTheme();

        expect(spy.mock.calls.length).toEqual(1);
      });
    });
  });
});
