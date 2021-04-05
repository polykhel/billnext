import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'wallet',
        data: { pageTitle: 'billnextApp.wallet.home.title' },
        loadChildren: () => import('./wallet/wallet.module').then(m => m.WalletModule),
      },
      {
        path: 'activity',
        data: { pageTitle: 'billnextApp.activity.home.title' },
        loadChildren: () => import('./activity/activity.module').then(m => m.ActivityModule),
      },
      {
        path: 'category',
        data: { pageTitle: 'billnextApp.category.home.title' },
        loadChildren: () => import('./category/category.module').then(m => m.CategoryModule),
      },
      {
        path: 'subcategory',
        data: { pageTitle: 'billnextApp.subcategory.home.title' },
        loadChildren: () => import('./subcategory/subcategory.module').then(m => m.SubcategoryModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
