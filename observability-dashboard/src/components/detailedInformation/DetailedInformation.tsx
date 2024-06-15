import CustomMetricsTable from "../customMetricsTable/CustomMetricsTable";
import GeneralnfoTable from "../generalnfoTable/GeneralnfoTable";
import StatusCircle from "../statusCircle/StatusCircle";
import "./DetailedInformation.scss";

const colorByRisk = {
  low: "rgba(21, 81, 237, 0.7)",
  medium: "rgba(211, 114, 0, 0.7)",
  high: "rgba(255, 19, 19, 0.7)",
};

const DetailedInformation = () => {
  return (
    <>
      <div
        style={{ backgroundColor: colorByRisk["low"] }}
        className="DetailedInformationHeader"
      >
        <div>getNumberOfUsers: /api/testGet/test</div>
        <div>Low Risk</div>
      </div>
      <div className="DetailedInformation">
        <div className="DetailedInformation_content">
          <h4>GENERAL INFO</h4>
          <br />
          <GeneralnfoTable
            rows={[
              { description: "requests", value: "20" },
              { description: "Errors", value: "1" },
              { description: "Response Time Median", value: "183.10ms" },
              { description: "Throughput Median", value: "102133" },
            ]}
          />
          <br />
          <br />
          <h4>CUSTOM METRICS</h4>
          <br />
          <CustomMetricsTable
            rows={[
              {
                description: "requests",
                expected: "20",
                received: "12",
                status: <StatusCircle />,
              },
              {
                description: "requests",
                expected: "20",
                received: "12",
                status: <StatusCircle />,
              },
            ]}
          />
        </div>
      </div>
    </>
  );
};

export default DetailedInformation;
