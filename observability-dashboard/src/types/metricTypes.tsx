interface GeneralMetrics {
  requestsQtt: number;
  errorsQtt: number;
  requestsBySecond: number;
}

interface SpecificMetrics {
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
