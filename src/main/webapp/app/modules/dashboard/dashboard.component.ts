import { Component } from '@angular/core';
import { addMonths, subMonths } from 'date-fns';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.less'],
})
export class DashboardComponent {
  selectedDate: Date = new Date();

  addTransaction(): void {
    // TODO: open modal for add transaction
  }

  goToPreviousMonth(): void {
    this.selectedDate = subMonths(this.selectedDate, 1);
  }

  goToNextMonth(): void {
    this.selectedDate = addMonths(this.selectedDate, 1);
  }
}
