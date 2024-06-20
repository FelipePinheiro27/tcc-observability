import { ServiceMetrics } from "../../types/metricTypes";
import {
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
  serviceMetrics?: ServiceMetrics;
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
                value: `${averageResponseTime}s`,
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
                expected: `${expectedResponseTime}s`,
                received: `${averageResponseTime}s`,
                status: (
                  <StatusCircle
                    metricName="Response Time"
                    risk={getRiskByMetricAttributes(
                      expectedResponseTime,
                      averageResponseTime
                    )}
                    max={`${maxResponseTime} seconds`}
                    maxSpanId={spanMaxResponseTime || ""}
                    min={`${minResponseTime} seconds`}
                    minSpanId={spanMinResponseTime || ""}
                    median={`${averageResponseTime} seconds`}
                    overflows={responseTimeOverflows || 0}
                  />
                ),
              },
              {
                description: "Memory Usage",
                expected: String(expectedMemoryUsage),
                received: String(averageMemoryUsage),
                status: (
                  <StatusCircle
                    metricName="Memory Usage"
                    risk={getRiskByMetricAttributes(
                      expectedMemoryUsage,
                      averageMemoryUsage
                    )}
                    max={`${maxMemoryUsage} bytes`}
                    maxSpanId={spanMaxMemoryUsage || ""}
                    min={`${minMemoryUsage} bytes`}
                    minSpanId={spanMinMemoryUsage || ""}
                    median={`${averageMemoryUsage} bytes`}
                    overflows={memoryUsageOverflows || 0}
                  />
                ),
              },
              {
                description: "Cpu Usage",
                expected: String(expectedCpuUsage),
                received: String(averageCpuUsage),
                status: (
                  <StatusCircle
                    metricName="Cpu Usage"
                    risk={getRiskByMetricAttributes(
                      expectedCpuUsage,
                      averageCpuUsage
                    )}
                    max={String(maxCpuUsage)}
                    maxSpanId={spanMaxCpuUsage || ""}
                    min={String(minCpuUsage)}
                    minSpanId={spanMinCpuUsage || ""}
                    median={String(averageCpuUsage)}
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
