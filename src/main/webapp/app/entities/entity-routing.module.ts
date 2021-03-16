import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'wallet',
        data: { pageTitle: 'billiesApp.wallet.home.title' },
        loadChildren: () => import('./wallet/wallet.module').then(m => m.WalletModule),
      },
      {
        path: 'category',
        data: { pageTitle: 'billiesApp.category.home.title' },
        loadChildren: () => import('./category/category.module').then(m => m.CategoryModule),
      },
      {
        path: 'subcategory',
        data: { pageTitle: 'billiesApp.subcategory.home.title' },
        loadChildren: () => import('./subcategory/subcategory.module').then(m => m.SubcategoryModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
