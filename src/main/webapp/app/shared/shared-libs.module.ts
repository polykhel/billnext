import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { InfiniteScrollModule } from 'ngx-infinite-scroll';
import { TranslateModule } from '@ngx-translate/core';
import { NZ_ICONS, NzIconModule } from 'ng-zorro-antd/icon';
import { NzLayoutModule } from 'ng-zorro-antd/layout';
import { NzFormModule } from 'ng-zorro-antd/form';
import { NzDatePickerModule } from 'ng-zorro-antd/date-picker';
import { NzNotificationModule } from 'ng-zorro-antd/notification';
import { nzIcons } from 'app/config/nz-icons';
import { en_US, NZ_I18N } from 'ng-zorro-antd/i18n';
import { NzMenuModule } from 'ng-zorro-antd/menu';
import { NzAlertModule } from 'ng-zorro-antd/alert';
import { NzDropDownModule } from 'ng-zorro-antd/dropdown';
import { NzImageModule } from 'ng-zorro-antd/image';

@NgModule({
  exports: [
    FormsModule,
    CommonModule,
    InfiniteScrollModule,
    ReactiveFormsModule,
    TranslateModule,
    NzIconModule,
    NzLayoutModule,
    NzFormModule,
    NzDatePickerModule,
    NzNotificationModule,
    NzMenuModule,
    NzAlertModule,
    NzDropDownModule,
    NzImageModule,
  ],
  providers: [
    { provide: NZ_ICONS, useValue: nzIcons },
    { provide: NZ_I18N, useValue: en_US },
  ],
})
export class SharedLibsModule {}
