import Header from "../components/header/Header";
import HomeMetrics from "../components/homeMetrics/HomeMetrics";
import ServicesInfo from "../components/servicesInfo/ServicesInfo";

const Home = () => {
  return (
    <>
      <Header />
      <div style={{ marginLeft: 45, marginRight: 45 }}>
        <HomeMetrics />
        <ServicesInfo />
      </div>
    </>
  );
};

export default Home;
