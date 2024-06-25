import Dialog from "@mui/material/Dialog";
import DialogContent from "@mui/material/DialogContent";
import { useEffect, useState } from "react";
import { systemInfoTypes } from "../../../types/metricTypes";
import { retrieveSystemInfo } from "../../../api/metricAPI";
import "./DialogSystemInfo.scss";

interface IDialogSystemInfo {
  open: boolean;
  setOpen: React.Dispatch<React.SetStateAction<boolean>>;
}

const DialogSystemInfo = ({ open, setOpen }: IDialogSystemInfo) => {
  const [systemInfo, setSystemInfo] = useState<systemInfoTypes>(
    {} as systemInfoTypes
  );

  useEffect(() => {
    const getSystemInfo = async () => {
      const systemInfoData = await retrieveSystemInfo();

      setSystemInfo(systemInfoData);
    };
    if (open) getSystemInfo();
  }, [open]);

  return (
    <Dialog
      open={open}
      onClose={() => setOpen(false)}
      aria-labelledby="draggable-dialog-title"
      PaperProps={{
        style: {
          width: "400px",
          maxWidth: "800px",
          margin: "auto",
        },
      }}
    >
      <DialogContent
        style={{
          backgroundColor: "#ececec",
          boxShadow:
            "0 4px 8px rgba(0, 0, 0, 0.1), 0 6px 20px rgba(0, 0, 0, 0.1)",
        }}
      >
        <div className="DialogSystemInfo">
          <div>
            <strong>Requests/Second (last 5 min): </strong>{" "}
            {Math.round(systemInfo.requestsBySecond * 100) / 100}
          </div>
          <div>
            <strong>Total Requests: </strong> {systemInfo.requestsQtt}
          </div>
          <div>
            <strong>Total Errors: </strong> {systemInfo.errorsQtt}
          </div>
          <div>
            <strong>Errors Percentage: </strong>{" "}
            {Number(
              (systemInfo.errorsQtt / systemInfo.requestsQtt).toFixed(1)
            ) * 100}
            %
          </div>
        </div>
      </DialogContent>
    </Dialog>
  );
};

export default DialogSystemInfo;
