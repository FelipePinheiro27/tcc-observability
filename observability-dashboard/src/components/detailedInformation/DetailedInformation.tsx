import { serviceMetricsTypes } from "../../types/metricTypes";
import {
  bytesToMegaBytes,
  getRiskByMetricAttributes,
  getRiskByMetrics,
} from "../../utils/serviceUtils";
import CustomMetricsTable from "../customMetricsTable/CustomMetricsTable";
import GeneralnfoTable from "../generalnfoTable/GeneralnfoTable";
import StatusCircle from "../statusCircle/StatusCircle";
import "./DetailedInformation.scss";

const colorByRisk = {
  low: "rgba(21, 81, 237, 0.7)",
  medium: "rgba(211, 114, 0, 0.7)",
  high: "rgba(255, 19, 19, 0.7)",
};

interface IDetailedInformation {
  serviceMetrics?: serviceMetricsTypes;
}

const DetailedInformation = ({ serviceMetrics }: IDetailedInformation) => {
  const { generalMetrics, specificMetrics, serviceName } = serviceMetrics || {};
  const { requestsQtt, errorsQtt } = generalMetrics || {};
  const {
    minCpuUsage,
    minMemoryUsage,
    minResponseTime,
    maxCpuUsage,
    maxMemoryUsage,
    maxResponseTime,
    spanMaxMemoryUsage,
    spanMaxResponseTime,
    spanMaxCpuUsage,
    spanMinCpuUsage,
    spanMinMemoryUsage,
    spanMinResponseTime,
    averageCpuUsage,
    averageMemoryUsage,
    averageResponseTime,
    expectedCpuUsage,
    expectedMemoryUsage,
    expectedResponseTime,
    cpuUsageOverflows,
    memoryUsageOverflows,
    responseTimeOverflows,
  } = specificMetrics || {};
  const risk = getRiskByMetrics(serviceMetrics);

  return (
    <>
      <div
        style={{ backgroundColor: colorByRisk[risk || "low"] }}
        className="DetailedInformationHeader"
      >
        <div>{serviceName}</div>
        <div>{risk} risk</div>
      </div>
      <div className="DetailedInformation">
        <div className="DetailedInformation_content">
          <h4>GENERAL INFO</h4>
          <br />
          <GeneralnfoTable
            rows={[
              {
                description: "Requests",
                value: String(requestsQtt),
              },
              { description: "Errors", value: String(errorsQtt) },
              {
                description: "Response Time Median",
                value: `${averageResponseTime?.toFixed(1)}s`,
              },
            ]}
          />
          <br />
          <br />
          <h4>CUSTOM METRICS</h4>
          <br />
          <CustomMetricsTable
            rows={[
              {
                description: "Response Time",
                expected: `${expectedResponseTime?.toFixed(1)}s`,
                received: `${averageResponseTime?.toFixed(1)}s`,
                status: (
                  <StatusCircle
                    metricName="Response Time"
                    risk={getRiskByMetricAttributes(
                      expectedResponseTime,
                      averageResponseTime
                    )}
                    max={`${maxResponseTime?.toFixed(1)} seconds`}
                    maxSpanId={spanMaxResponseTime || ""}
                    min={`${minResponseTime?.toFixed(1)} seconds`}
                    minSpanId={spanMinResponseTime || ""}
                    median={`${averageResponseTime?.toFixed(1)} seconds`}
                    overflows={responseTimeOverflows || 0}
                  />
                ),
              },
              {
                description: "Memory Usage",
                expected: `${expectedMemoryUsage} MB`,
                received: `${bytesToMegaBytes(averageMemoryUsage)} MB`,
                status: (
                  <StatusCircle
                    metricName="Memory Usage"
                    risk={getRiskByMetricAttributes(
                      expectedMemoryUsage,
                      bytesToMegaBytes(averageMemoryUsage)
                    )}
                    max={`${bytesToMegaBytes(maxMemoryUsage)} MB`}
                    maxSpanId={spanMaxMemoryUsage || ""}
                    min={`${bytesToMegaBytes(minMemoryUsage)} MB`}
                    minSpanId={spanMinMemoryUsage || ""}
                    median={`${bytesToMegaBytes(averageMemoryUsage)} MB`}
                    overflows={memoryUsageOverflows || 0}
                  />
                ),
              },
              {
                description: "Cpu Usage",
                expected: `${expectedCpuUsage?.toFixed(1)}%`,
                received: `${averageCpuUsage?.toFixed(1)}%`,
                status: (
                  <StatusCircle
                    metricName="Cpu Usage"
                    risk={getRiskByMetricAttributes(
                      expectedCpuUsage,
                      averageCpuUsage
                    )}
                    max={String(maxCpuUsage?.toFixed(1))}
                    maxSpanId={spanMaxCpuUsage || ""}
                    min={String(minCpuUsage?.toFixed(1))}
                    minSpanId={spanMinCpuUsage || ""}
                    median={String(averageCpuUsage?.toFixed(1))}
                    overflows={cpuUsageOverflows || 0}
                  />
                ),
              },
            ]}
          />
        </div>
      </div>
    </>
  );
};

export default DetailedInformation;
