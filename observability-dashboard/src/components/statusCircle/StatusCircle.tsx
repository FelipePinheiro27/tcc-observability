import { useState } from "react";
import Dialog from "@mui/material/Dialog";
import DialogContent from "@mui/material/DialogContent";
import DialogTitle from "@mui/material/DialogTitle";
import GeneralnfoTable from "../generalnfoTable/GeneralnfoTable";

const colorByRisk = {
  low: "rgba(21, 81, 237, 0.7)",
  medium: "rgba(211, 114, 0, 0.7)",
  high: "rgba(255, 19, 19, 0.7)",
};

const StatusCircle = () => {
  const [open, setOpen] = useState(false);

  return (
    <>
      <div
        className="StatusCircle"
        onMouseEnter={() => setOpen(true)}
        style={{
          backgroundColor: colorByRisk["low"],
          width: 16,
          height: 16,
          borderRadius: "50%",
          zIndex: 1,
        }}
      />
      <Dialog
        open={open}
        onClose={() => setOpen(false)}
        aria-labelledby="draggable-dialog-title"
        PaperProps={{
          style: {
            width: "80%",
            maxWidth: "800px",
            margin: "auto",
          },
        }}
      >
        <DialogTitle
          style={{ cursor: "move", backgroundColor: "#ececec" }}
          id="draggable-dialog-title"
        >
          Response Time
        </DialogTitle>
        <DialogContent style={{ backgroundColor: "#ececec" }}>
          <GeneralnfoTable
            rows={[
              { description: "requests", value: "20" },
              { description: "Errors", value: "1" },
              { description: "Response Time Median", value: "183.10ms" },
              { description: "Throughput Median", value: "102133" },
            ]}
          />
        </DialogContent>
      </Dialog>
    </>
  );
};

export default StatusCircle;
