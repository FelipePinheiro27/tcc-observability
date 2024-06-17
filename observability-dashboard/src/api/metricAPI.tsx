import axios from "axios";
import { ServiceMetrics } from "../types/metricTypes";

export const retrieveAllMetrics = async (): Promise<ServiceMetrics[]> => {
  try {
    const response = await axios.get<ServiceMetrics[]>(
      "http://localhost:8081/api/metrics/all"
    );

    console.log(response);
    return response.data;
  } catch (error) {
    console.error("Error retrieving metrics:", error);
    throw error;
  }
};
