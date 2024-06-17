import Header from "../components/header/Header";
import HomeMetrics from "../components/homeMetrics/HomeMetrics";
import ServicesInfo from "../components/servicesInfo/ServicesInfo";
import { ServiceMetrics } from "../types/metricTypes";

interface IHome {
  allMetrics: ServiceMetrics[];
}

const Home = ({ allMetrics }: IHome) => {
  return (
    <>
      <Header homePage />
      <div style={{ marginLeft: 45, marginRight: 45 }}>
        <HomeMetrics />
        <ServicesInfo allMetrics={allMetrics} />
      </div>
    </>
  );
};

export default Home;
