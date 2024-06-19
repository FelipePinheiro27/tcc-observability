import { useParams } from "react-router";
import DetailedInformation from "../components/detailedInformation/DetailedInformation";
import Header from "../components/header/Header";
import { ServiceMetrics } from "../types/metricTypes";
import { hasLength } from "../utils/serviceUtils";

interface IDetail {
  allMetrics: ServiceMetrics[];
}

const Detail = ({ allMetrics }: IDetail) => {
  const { serviceName } = useParams();
  const serviceMetrics = allMetrics.find((metric) => metric.id === serviceName);

  return (
    <>
      <Header label="SERVICE DETAIL" />
      {hasLength(allMetrics) && (
        <div style={{ marginLeft: 45, marginRight: 45 }}>
          <DetailedInformation serviceMetrics={serviceMetrics} />
        </div>
      )}
    </>
  );
};

export default Detail;
