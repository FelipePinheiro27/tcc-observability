import { useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import SelectPriority from "../select/SelectPriority";
import ServiceContent from "../serviceContent/ServiceContent";
import { serviceMetricsTypes } from "../../types/metricTypes";
import { getRiskByMetrics } from "../../utils/serviceUtils";
import "./ServicesInfo.scss";

interface IServicesInfo {
  allMetrics: serviceMetricsTypes[];
}

const ServicesInfo = ({ allMetrics }: IServicesInfo) => {
  const navigate = useNavigate();
  const [priority, setPriority] = useState("");

  const onNavigateToDetail = (serviceName: string) => {
    navigate(`/detail/${serviceName}`);
  };

  const filteredMetrics = useMemo(() => {
    if (priority !== "")
      return allMetrics.filter(
        (metric) => getRiskByMetrics(metric) === priority
      );

    return allMetrics;
  }, [allMetrics, priority]);

  return (
    <div className="ServicesInfo">
      <div className="ServicesInfo_content">
        <div className="ServicesInfo_content-header">
          <h3 className="">{allMetrics.length} Services Calleds</h3>
          <div>
            <SelectPriority priority={priority} setPriority={setPriority} />
          </div>
        </div>
        <div className="ServicesInfo_content-list">
          {filteredMetrics.map((metric) => (
            <>
              <ServiceContent
                risk={getRiskByMetrics(metric) || ""}
                serviceName={metric.serviceName}
                serviceId={metric.id}
                onNavigateToDetail={onNavigateToDetail}
              />
              <br />
            </>
          ))}
        </div>
      </div>
    </div>
  );
};

export default ServicesInfo;
