import { useEffect, useState } from "react";
import { Routes, Route } from "react-router-dom";
import Home from "../pages/Home";
import Detail from "../pages/Detail";
import { retrieveAllMetrics } from "../api/metricAPI";
import { ServiceMetrics } from "../types/metricTypes";

const Navigator = () => {
  const [allMetrics, setAllMetrics] = useState<ServiceMetrics[]>([]);

  useEffect(() => {
    const getAllMetrics = async () => {
      try {
        const metrics = await retrieveAllMetrics();
        setAllMetrics(metrics);
      } catch (error) {
        console.error("Failed to retrieve metrics:", error);
      }
    };

    // Chame a função imediatamente ao montar o componente
    getAllMetrics();

    // Defina o intervalo para chamar a função a cada 30 segundos
    const intervalId = setInterval(getAllMetrics, 30000);

    // Limpe o intervalo ao desmontar o componente
    return () => clearInterval(intervalId);
  }, []);

  console.log(allMetrics);

  return (
    <Routes>
      <Route index element={<Home allMetrics={allMetrics} />} />
      <Route
        path="detail/:serviceName"
        element={<Detail allMetrics={allMetrics} />}
      />
    </Routes>
  );
};

export default Navigator;
