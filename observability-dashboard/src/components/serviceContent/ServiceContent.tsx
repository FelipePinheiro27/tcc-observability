import "./ServiceContent.scss";

interface IServiceContent {
  onNavigateToDetail: (serviceName: string) => void;
  serviceName: string;
  risk: string;
}

const ServiceContent = ({
  onNavigateToDetail,
  serviceName,
  risk,
}: IServiceContent) => {
  return (
    <div
      className="ServiceContent"
      onClick={() => onNavigateToDetail(serviceName)}
    >
      <div className="ServiceContent-risk">
        <div
          style={{ width: "100%", height: "100%" }}
          className={`ServiceContent-risk-${risk}`}
        />
      </div>
      <div className="ServiceContent-value">
        <div>{serviceName}</div>
        <div>{risk} risk</div>
      </div>
    </div>
  );
};

export default ServiceContent;
