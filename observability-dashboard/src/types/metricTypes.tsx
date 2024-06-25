interface generalMetricsTypes {
  requestsQtt: number;
  errorsQtt: number;
  requestsBySecond: number;
}

interface specificMetricsTypes {
  maxResponseTime: number;
  maxCpuUsage: number;
  maxMemoryUsage: number;
  minResponseTime: number;
  minCpuUsage: number;
  minMemoryUsage: number;
  spanMaxResponseTime: string;
  spanMaxCpuUsage: string;
  spanMaxMemoryUsage: string;
  spanMinResponseTime: string;
  spanMinCpuUsage: string;
  spanMinMemoryUsage: string;
  responseTimeOverflows: number;
  cpuUsageOverflows: number;
  memoryUsageOverflows: number;
  allOverflows: number;
  expectedCpuUsage: number;
  expectedMemoryUsage: number;
  expectedResponseTime: number;
  averageCpuUsage: number;
  averageMemoryUsage: number;
  averageResponseTime: number;
}

export interface serviceMetricsTypes {
  generalMetrics: generalMetricsTypes;
  specificMetrics: specificMetricsTypes;
  serviceName: string;
  id: string;
}

export interface systemInfoTypes {
  requestsBySecond: number;
  requestsQtt: number;
  errorsQtt: number;
}

export interface prometheusMetricsTypes {
  cpuUsage: number;
  memory: number;
  throughput: number;
}
