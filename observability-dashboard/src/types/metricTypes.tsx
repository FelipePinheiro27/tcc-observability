interface GeneralMetrics {
  requestsQtt: number;
  errorsQtt: number;
  requestsBySecond: number;
}

interface SpecificMetrics {
  maxResponseTime: number;
  maxCpuStorage: number;
  maxMemoryUsage: number;
  minResponseTime: number;
  minCpuStorage: number;
  minMemoryUsage: number;
  spanMaxResponseTime: string;
  spanMaxCpuStorage: string;
  spanMaxMemoryUsage: string;
  spanMinResponseTime: string;
  spanMinCpuStorage: string;
  spanMinMemoryUsage: string;
  responseTimeOverflows: number;
  cpuStorageOverflows: number;
  memoryUsageOverflows: number;
  allOverflows: number;
  expectedCpuUsage: number;
  expectedMemoryUsage: number;
  expectedResponseTime: number;
  averageCpuUsage: number;
  averageMemoryUsage: number;
  averageResponseTime: number;
}

export interface ServiceMetrics {
  generalMetrics: GeneralMetrics;
  specificMetrics: SpecificMetrics;
  serviceName: string;
  id: string;
}

export interface SystemInfo {
  requestsBySecond: number;
  requestsQtt: number;
  errorsQtt: number;
}
