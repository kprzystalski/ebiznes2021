type Dispatch<A> = (value: A) => void;

export interface IProduct {
  id: number;
  name: string;
  description: string;
  category: number;
}

export type IShopContextState = {
  products: IProduct[];
}
