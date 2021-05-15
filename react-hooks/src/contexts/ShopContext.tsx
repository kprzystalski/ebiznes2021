import React, { useEffect, useState } from "react";
import { IProduct, IShopContextState } from "../interface";
import { fetchProducts } from "../api/products"

const defaultValue: IShopContextState = {
  products: []
}

export const ShopContext = React.createContext(defaultValue);

export const ShopContextProvider: React.FC = ({ children }) => {
  const [products, setProducts] = useState<IProduct[]>([]);

  const providerValue: IShopContextState = {
    products,
  }

  useEffect(() => {
    fetchProducts()
      .then((products) => {
        console.log(products)
        setProducts(products)
      })
  }, []);

  return (
    <ShopContext.Provider value={providerValue}>{children}</ShopContext.Provider>
  );
};

export default ShopContext;
