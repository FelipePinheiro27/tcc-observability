import Dialog from "@mui/material/Dialog";
import DialogContent from "@mui/material/DialogContent";
import "./DialogSystemInfo.scss";
import { useEffect, useState } from "react";
import { SystemInfo } from "../../../types/metricTypes";
import { retrieveSystemInfo } from "../../../api/metricAPI";

interface IDialogSystemInfo {
  open: boolean;
  setOpen: React.Dispatch<React.SetStateAction<boolean>>;
}

const DialogSystemInfo = ({ open, setOpen }: IDialogSystemInfo) => {
  const [systemInfo, setSystemInfo] = useState<SystemInfo>({} as SystemInfo);

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
            {Math.round((systemInfo.errorsQtt / systemInfo.requestsQtt) * 100) /
              100}
            %
          </div>
        </div>
      </DialogContent>
    </Dialog>
  );
};

export default DialogSystemInfo;
