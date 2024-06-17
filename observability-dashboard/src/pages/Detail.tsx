import { useParams } from "react-router";
import DetailedInformation from "../components/detailedInformation/DetailedInformation";
import Header from "../components/header/Header";
import { ServiceMetrics } from "../types/metricTypes";

interface IDetail {
  allMetrics: ServiceMetrics[];
}

const Detail = ({ allMetrics }: IDetail) => {
  const { serviceName } = useParams();
  console.log(serviceName);
  return (
    <>
      <Header label="SERVICE DETAIL" />
      <div style={{ marginLeft: 45, marginRight: 45 }}>
        <DetailedInformation />
      </div>
    </>
  );
};

export default Detail;
