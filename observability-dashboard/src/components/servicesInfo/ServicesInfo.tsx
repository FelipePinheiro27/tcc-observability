import { useNavigate } from "react-router-dom";
import SelectPriority from "../select/SelectPriority";
import ServiceContent from "../serviceContent/ServiceContent";
import "./ServicesInfo.scss";

const ServicesInfo = () => {
  const navigate = useNavigate();

  const onNavigateToDetail = (serviceName: string) => {
    navigate(`/detail/${serviceName}`);
  };
  return (
    <div className="ServicesInfo">
      <div className="ServicesInfo_content">
        <div className="ServicesInfo_content-header">
          <h3 className="">6 Services Calleds</h3>
          <div>
            <SelectPriority />
          </div>
        </div>
        <div className="ServicesInfo_content-list">
          <ServiceContent onNavigateToDetail={onNavigateToDetail} />
          <br />
          <ServiceContent onNavigateToDetail={onNavigateToDetail} />
          <br />
          <ServiceContent onNavigateToDetail={onNavigateToDetail} />
          <br />
          <ServiceContent onNavigateToDetail={onNavigateToDetail} />
          <br />
          <ServiceContent onNavigateToDetail={onNavigateToDetail} />
          <br />
          <ServiceContent onNavigateToDetail={onNavigateToDetail} />
        </div>
      </div>
    </div>
  );
};

export default ServicesInfo;
