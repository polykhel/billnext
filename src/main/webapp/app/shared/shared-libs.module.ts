import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { TranslateModule } from '@ngx-translate/core';
import { NzCheckboxModule } from 'ng-zorro-antd/checkbox';
import { NzIconModule } from 'ng-zorro-antd/icon';
import { NzLayoutModule } from 'ng-zorro-antd/layout';
import { NzFormModule } from 'ng-zorro-antd/form';
import { NzDatePickerModule } from 'ng-zorro-antd/date-picker';
import { NzNotificationModule } from 'ng-zorro-antd/notification';
import { en_US, NZ_I18N } from 'ng-zorro-antd/i18n';
import { NzMenuModule } from 'ng-zorro-antd/menu';
import { NzAlertModule } from 'ng-zorro-antd/alert';
import { NzDropDownModule } from 'ng-zorro-antd/dropdown';
import { NzImageModule } from 'ng-zorro-antd/image';
import { NzGridModule } from 'ng-zorro-antd/grid';
import { NzSpaceModule } from 'ng-zorro-antd/space';
import { NzButtonModule } from 'ng-zorro-antd/button';
import { NzResultModule } from 'ng-zorro-antd/result';
import { NzTabsModule } from 'ng-zorro-antd/tabs';
import { NzDividerModule } from 'ng-zorro-antd/divider';
import { NzAvatarModule } from 'ng-zorro-antd/avatar';
import { NzModalModule } from 'ng-zorro-antd/modal';
import { NzInputModule } from 'ng-zorro-antd/input';
import { NzTableModule } from 'ng-zorro-antd/table';
import { DragDropModule } from '@angular/cdk/drag-drop';

@NgModule({
  exports: [
    FormsModule,
    CommonModule,
    ReactiveFormsModule,
    TranslateModule,
    DragDropModule,
    NzIconModule,
    NzLayoutModule,
    NzFormModule,
    NzDatePickerModule,
    NzNotificationModule,
    NzMenuModule,
    NzAlertModule,
    NzDropDownModule,
    NzImageModule,
    NzGridModule,
    NzSpaceModule,
    NzButtonModule,
    NzResultModule,
    NzTabsModule,
    NzDividerModule,
    NzAvatarModule,
    NzModalModule,
    NzInputModule,
    NzTableModule,
    NzCheckboxModule,
  ],
  providers: [{ provide: NZ_I18N, useValue: en_US }],
})
export class SharedLibsModule {}
