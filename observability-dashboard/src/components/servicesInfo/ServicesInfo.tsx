import { useNavigate } from "react-router-dom";
import SelectPriority from "../select/SelectPriority";
import ServiceContent from "../serviceContent/ServiceContent";
import { ServiceMetrics } from "../../types/metricTypes";
import "./ServicesInfo.scss";

interface IServicesInfo {
  allMetrics: ServiceMetrics[];
}

const ServicesInfo = ({ allMetrics }: IServicesInfo) => {
  const navigate = useNavigate();

  const onNavigateToDetail = (serviceName: string) => {
    navigate(`/detail/${serviceName}`);
  };
  return (
    <div className="ServicesInfo">
      <div className="ServicesInfo_content">
        <div className="ServicesInfo_content-header">
          <h3 className="">{allMetrics.length} Services Calleds</h3>
          <div>
            <SelectPriority />
          </div>
        </div>
        <div className="ServicesInfo_content-list">
          {allMetrics.map((metric) => (
            <>
              <ServiceContent
                risk="low"
                serviceName={metric.serviceName}
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
