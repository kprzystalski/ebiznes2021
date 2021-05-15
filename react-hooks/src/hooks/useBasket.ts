import {useState} from "react";
import {IProduct} from "../interface";

function useBasket() {
  const [basket, setBasket] = useState<IProduct[]>([]);

  function addProduct(product: IProduct) {
    setBasket([
      ...basket,
      product
    ])
  }

  function removeProduct(id: number) {
    const filteredProducts = basket.filter(product => product.id !== id)

    setBasket([...filteredProducts])
  }

  return {
  basket,
  addProduct,
  removeProduct,
  }
}

export default useBasket;
