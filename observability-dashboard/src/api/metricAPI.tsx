import axios from "axios";
import {
  prometheusMetricsTypes,
  serviceMetricsTypes,
  systemInfoTypes,
} from "../types/metricTypes";

export const retrieveAllMetrics = async (): Promise<serviceMetricsTypes[]> => {
  try {
    const response = await axios.get<serviceMetricsTypes[]>(
      "http://localhost:8084/api/metrics/all"
    );
    return response.data;
  } catch (error) {
    console.error("Error retrieving metrics:", error);
    throw error;
  }
};

export const retrieveSystemInfo = async (): Promise<systemInfoTypes> => {
  try {
    const response = await axios.get<systemInfoTypes>(
      "http://localhost:8084/api/metrics/system-info"
    );

    return response.data;
  } catch (error) {
    console.error(error);
    throw error;
  }
};

export const retrievePrometheusMetrics =
  async (): Promise<prometheusMetricsTypes> => {
    try {
      const response = await axios.get<prometheusMetricsTypes>(
        "http://localhost:8084/api/metrics/prometheus-metrics"
      );

      return response.data;
    } catch (error) {
      console.error(error);
      throw error;
    }
  };
