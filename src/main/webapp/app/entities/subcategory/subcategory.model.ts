import { ICategory } from 'app/entities/category/category.model';

export interface ISubcategory {
  id?: number;
  name?: string;
  category?: ICategory;
}

export class Subcategory implements ISubcategory {
  constructor(public id?: number, public name?: string, public category?: ICategory) {}
}
