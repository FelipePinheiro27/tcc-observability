import axios from "axios";
import { ServiceMetrics, SystemInfo } from "../types/metricTypes";

export const retrieveAllMetrics = async (): Promise<ServiceMetrics[]> => {
  try {
    const response = await axios.get<ServiceMetrics[]>(
      "http://localhost:8081/api/metrics/all"
    );
    return response.data;
  } catch (error) {
    console.error("Error retrieving metrics:", error);
    throw error;
  }
};

export const retrieveSystemInfo = async (): Promise<SystemInfo> => {
  try {
    const response = await axios.get<SystemInfo>(
      "http://localhost:8081/api/metrics/system-info"
    );

    return response.data;
  } catch (error) {
    console.error(error);
    throw error;
  }
};
