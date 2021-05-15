import React from 'react';
import './App.css';
import { BrowserRouter, Link, Route} from 'react-router-dom';
import Welcome from './components/Welcome';
import { Products } from './components/Products';
import {ShopContextProvider} from "./contexts/ShopContext";

function App() {
  return (
    <div className="App">
      <div>
        <ShopContextProvider>
          <BrowserRouter>
            <ul>
              <li><Link to="/">Home</Link></li>
              <li><Link to="/basket">Basket</Link></li>
              <li><Link to="/products">Products</Link></li>
            </ul>
            <Route path="/" component={Welcome}/>
            <Route path="/products" component={Products}/>
          </BrowserRouter>
        </ShopContextProvider>
      </div>
    </div>
  );
}

export default App;
