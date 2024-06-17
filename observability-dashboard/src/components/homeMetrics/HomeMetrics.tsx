import { useState } from "react";
import InfoOutlinedIcon from "@mui/icons-material/InfoOutlined";
import DialogSystemInfo from "../servicesInfo/dialog/DialogSystemInfo";
import "./HomeMetrics.scss";

const HomeMetrics = () => {
  const [open, setOpen] = useState(false);

  return (
    <>
      <div className="HomeMetrics">
        <div className="HomeMetrics_values">
          <div>CPU : 20000</div>
          <div>Memory Used: 9803024</div>
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
