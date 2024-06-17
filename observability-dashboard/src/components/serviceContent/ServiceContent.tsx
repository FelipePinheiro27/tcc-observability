import "./ServiceContent.scss";

interface IServiceContent {
  onNavigateToDetail: (serviceName: string) => void;
}

const ServiceContent = ({ onNavigateToDetail }: IServiceContent) => {
  return (
    <div
      className="ServiceContent"
      onClick={() => onNavigateToDetail("testing")}
    >
      <div className="ServiceContent-risk">
        <div
          style={{ width: "100%", height: "100%" }}
          className="ServiceContent-risk-low"
        />
      </div>
      <div className="ServiceContent-value">
        <div>getNumberOfUsers: /api/testGet/name</div>
        <div>Low Risk</div>
      </div>
    </div>
  );
};

export default ServiceContent;
