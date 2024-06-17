import { Routes, Route } from "react-router-dom";
import Home from "../pages/Home";
import Detail from "../pages/Detail";

const Navigator = () => {
  return (
    <Routes>
      <Route index element={<Home />} />
      <Route path="detail/:serviceName" element={<Detail />} />
    </Routes>
  );
};

export default Navigator;
