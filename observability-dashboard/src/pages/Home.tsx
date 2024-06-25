import Header from "../components/header/Header";
import HomeMetrics from "../components/homeMetrics/HomeMetrics";
import ServicesInfo from "../components/servicesInfo/ServicesInfo";
import {
  prometheusMetricsTypes,
  serviceMetricsTypes,
} from "../types/metricTypes";

interface IHome {
  allMetrics: serviceMetricsTypes[];
  prometheusMetricsValue: prometheusMetricsTypes | null;
}

const Home = ({ allMetrics, prometheusMetricsValue }: IHome) => {
  return (
    <>
      <Header homePage />
      <div style={{ marginLeft: 45, marginRight: 45 }}>
        <HomeMetrics prometheusMetricsValue={prometheusMetricsValue} />
        <ServicesInfo allMetrics={allMetrics} />
      </div>
    </>
  );
};

export default Home;
