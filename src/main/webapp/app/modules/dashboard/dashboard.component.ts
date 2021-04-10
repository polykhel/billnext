import { Component } from '@angular/core';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.less'],
})
export class DashboardComponent {
  date = '2021 Mar';

  addTransaction(): void {
    // TODO: open modal for add transaction
  }
}
