import { useParams } from "react-router";
import DetailedInformation from "../components/detailedInformation/DetailedInformation";
import Header from "../components/header/Header";

const Detail = () => {
  const { id } = useParams();

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
