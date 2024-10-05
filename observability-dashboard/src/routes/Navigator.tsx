import { useEffect, useRef, useState } from "react";
import { Routes, Route } from "react-router-dom";
import Home from "../pages/Home";
import Detail from "../pages/Detail";
import {
  retrieveAllMetrics,
  retrievePrometheusMetrics,
} from "../api/metricAPI";
import {
  prometheusMetricsTypes,
  serviceMetricsTypes,
} from "../types/metricTypes";

const Navigator = () => {
  const [allMetrics, setAllMetrics] = useState<serviceMetricsTypes[]>([]);
  const prometheusMetricsRef = useRef<prometheusMetricsTypes>();
  const [prometheusMetricsValue, setPrometheusMetricsValue] =
    useState<prometheusMetricsTypes | null>(null);

  useEffect(() => {
    const fetchDataAndUpdate = async () => {
      const metrics = await retrievePrometheusMetrics();
      if (!prometheusMetricsRef.current) prometheusMetricsRef.current = metrics;
      else {
        const cpuUsage =
          Number(
            (metrics.cpuUsage - prometheusMetricsRef.current.cpuUsage).toFixed(
              1
            )
          ) * 100;
        const memory = metrics.memory - prometheusMetricsRef.current.memory;
        const throughput =
          metrics.throughput - prometheusMetricsRef.current.throughput;
        if (cpuUsage >= 0 && memory >= 0 && throughput >= 0)
          setPrometheusMetricsValue({
            cpuUsage,
            memory,
            throughput,
          });
        prometheusMetricsRef.current = metrics;
      }
    };

    const intervalId = setInterval(fetchDataAndUpdate, 15000);

    return () => clearInterval(intervalId);
  }, []);

  useEffect(() => {
    const getAllMetrics = async () => {
      try {
        const metrics = await retrieveAllMetrics();
        setAllMetrics(metrics);
      } catch (error) {
        console.error("Failed to retrieve metrics:", error);
      }
    };
    getAllMetrics();

    const intervalId = setInterval(getAllMetrics, 30000);

    return () => clearInterval(intervalId);
  }, []);

  return (
    <Routes>
      <Route
        index
        element={
          <Home
            allMetrics={allMetrics}
            prometheusMetricsValue={prometheusMetricsValue}
          />
        }
      />
      <Route
        path="detail/:serviceName"
        element={<Detail allMetrics={allMetrics} />}
      />
    </Routes>
  );
};

export default Navigator;
