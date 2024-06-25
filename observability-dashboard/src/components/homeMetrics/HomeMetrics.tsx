import { useEffect, useRef, useState } from "react";
import InfoOutlinedIcon from "@mui/icons-material/InfoOutlined";
import DialogSystemInfo from "../servicesInfo/dialog/DialogSystemInfo";
import { retrievePrometheusMetrics } from "../../api/metricAPI";
import { prometheusMetricsTypes } from "../../types/metricTypes";
import "./HomeMetrics.scss";

interface IHomeMetrics {
  prometheusMetricsValue: prometheusMetricsTypes | null;
}

const HomeMetrics = ({ prometheusMetricsValue }: IHomeMetrics) => {
  const [open, setOpen] = useState(false);
  const { cpuUsage, memory, throughput } = prometheusMetricsValue || {};

  return (
    <>
      <div className="HomeMetrics">
        <div className="HomeMetrics_values">
          {prometheusMetricsValue && (
            <>
              <div>CPU: {cpuUsage}%</div>
              <div>Memory Used: {memory} bytes</div>
              <div>Throughput: {throughput}</div>
            </>
          )}
        </div>
        <div className="HomeMetrics_system-info" onClick={() => setOpen(true)}>
          <InfoOutlinedIcon />
          SYSTEM INFO
        </div>
      </div>
      <DialogSystemInfo open={open} setOpen={setOpen} />
    </>
  );
};

export default HomeMetrics;
