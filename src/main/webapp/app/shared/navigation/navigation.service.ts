import { Injectable } from '@angular/core';
import { Location } from '@angular/common';
import { NavigationEnd, Router } from '@angular/router';

/**
 * Navigation service for back button
 */
@Injectable({
  providedIn: 'root',
})
export class NavigationService {
  private history: string[] = [];

  constructor(private router: Router, private location: Location) {
    /**
     * Listen to router events of type NavigationEnd to manage an app-specific
     * navigation history.
     */
    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        this.history.push(event.urlAfterRedirects);
      }
    });
  }

  /**
   * If the history still contains entries after popping the current URL
   * off of the stack, we can safely navigate back. Otherwise we're falling
   * back to the application route.
   */
  back(): void {
    this.history.pop();
    if (this.history.length > 0) {
      this.location.back();
    } else {
      this.router.navigateByUrl('/').then();
    }
  }
}
