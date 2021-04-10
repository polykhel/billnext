import { Component, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-fixed-widgets',
  templateUrl: './fixed-widgets.component.html',
  styleUrls: ['./fixed-widgets.component.less'],
})
export class FixedWidgetsComponent {
  @Output() clickWidget: EventEmitter<any> = new EventEmitter();

  click(): void {
    this.clickWidget.emit(true);
  }
}
