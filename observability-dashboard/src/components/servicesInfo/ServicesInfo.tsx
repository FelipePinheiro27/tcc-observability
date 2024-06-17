import SelectPriority from "../select/SelectPriority";
import ServiceContent from "../serviceContent/ServiceContent";
import "./ServicesInfo.scss";

const ServicesInfo = () => {
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
          <ServiceContent />
          <br />
          <ServiceContent />
          <br />
          <ServiceContent />
          <br />
          <ServiceContent />
          <br />
          <ServiceContent />
          <br />
          <ServiceContent />
        </div>
      </div>
    </div>
  );
};

export default ServicesInfo;
