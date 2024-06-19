import { ServiceMetrics } from "../types/metricTypes";

export const hasLength = (data: unknown[]) => data.length > 0;

export const getRiskByMetrics = (metrics?: ServiceMetrics) => {
    if(!metrics)
        return;
    const {specificMetrics} = metrics;
    const {expectedCpuUsage, expectedMemoryUsage, expectedResponseTime, averageCpuUsage, averageMemoryUsage, averageResponseTime} = specificMetrics;
    const cpuRange = expectedCpuUsage !== 0 ? (averageCpuUsage / expectedCpuUsage) * 100 : -1;
    const memoryRange = expectedMemoryUsage !== 0 ? (averageMemoryUsage / expectedMemoryUsage) * 100 : -1;
    const responseTimeRange = expectedResponseTime !== 0 ? (averageResponseTime / expectedResponseTime) * 100 : -1;

    console.log(cpuRange, memoryRange, responseTimeRange)
    if(cpuRange >= 100 || memoryRange >= 100 || responseTimeRange >= 100)
     return 'high';
    if(cpuRange >= 80 || memoryRange >= 80 || responseTimeRange >= 80)
     return 'medium';

    return 'low'
}