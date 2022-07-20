import './App.css';
import React from 'react';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import { Home } from './pages/Home';
import { DocGenerator } from './pages/DocGenerator';
import { HtmlGenerator } from './pages/HtmlGenerator';

function App() {
  return (
    <BrowserRouter>
        <Routes>
          <Route path={'/'} exact element={<Home />} />
          <Route path={'/doc'} element={<DocGenerator />} />
          <Route path={'/html'} element={<HtmlGenerator />} />
        </Routes>
    </BrowserRouter>
  );
}

export default App;
