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
  medianReponseTime: number;
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
}

export interface ServiceMetrics {
  generalMetrics: GeneralMetrics;
  specificMetrics: SpecificMetrics;
  serviceName: string;
}
