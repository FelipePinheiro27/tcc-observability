import { useState } from "react";
import InfoOutlinedIcon from "@mui/icons-material/InfoOutlined";
import DialogSystemInfo from "../servicesInfo/dialog/DialogSystemInfo";
import { prometheusMetricsTypes } from "../../types/metricTypes";
import { bytesToMegaBytes } from "../../utils/serviceUtils";
import "./HomeMetrics.scss";

interface IHomeMetrics {
  prometheusMetricsValue: prometheusMetricsTypes | null;
}

const HomeMetrics = ({ prometheusMetricsValue }: IHomeMetrics) => {
  const [open, setOpen] = useState(false);
  const { cpuUsage, memory, throughput } = prometheusMetricsValue || {};
  const megaByteMemory = bytesToMegaBytes(memory);

  return (
    <>
      <div className="HomeMetrics">
        <div className="HomeMetrics_values">
          {prometheusMetricsValue && (
            <>
              <div style={{ fontSize: 14 }}>
                <strong>Last 5 seconds:</strong>
              </div>
              <div>CPU: {(cpuUsage || 0).toFixed(1)}%</div>
              <div>Memory Used: {megaByteMemory.toFixed(1)} MB</div>
              <div>Throughput: {throughput} Bytes</div>
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
