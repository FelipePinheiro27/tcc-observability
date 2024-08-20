import { useState } from "react";
import Dialog from "@mui/material/Dialog";
import DialogContent from "@mui/material/DialogContent";
import DialogTitle from "@mui/material/DialogTitle";
import MetricInfoTable from "../metricInfoTable/MetricInfoTable";

const colorByRisk = {
  low: "rgba(21, 81, 237, 0.7)",
  medium: "rgba(211, 114, 0, 0.7)",
  high: "rgba(255, 19, 19, 0.7)",
  none: "rgba(112, 112, 112, 0.7)",
};

interface IStatusCircle {
  metricName: string;
  risk: "low" | "medium" | "high" | "none";
  max: string;
  maxSpanId: string;
  min: string;
  minSpanId: string;
  median: string;
  overflows: number;
}

const StatusCircle = ({
  risk,
  metricName,
  max,
  maxSpanId,
  min,
  minSpanId,
  median,
  overflows,
}: IStatusCircle) => {
  const [open, setOpen] = useState(false);

  if (!risk) return <></>;

  return (
    <>
      <div
        className="StatusCircle"
        onMouseEnter={() => setOpen(true)}
        style={{
          backgroundColor: colorByRisk[risk],
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
            width: risk === "none" ? "400px" : "80%",
            maxWidth: "800px",
            margin: "auto",
          },
        }}
      >
        {risk === "none" ? (
          <>
            <DialogContent
              style={{
                backgroundColor: "#ececec",
                textAlign: "center",
                alignItems: "center",
                justifyContent: "center",
                display: "flex",
              }}
            >
              <div className="jacques-francois-regular">
                This metric ins't available yet :( <br />
                Only metrics with more than 2 seconds are available!!
              </div>
            </DialogContent>
          </>
        ) : (
          <>
            <DialogTitle
              style={{ cursor: "move", backgroundColor: "#ececec" }}
              id="draggable-dialog-title"
            >
              {metricName}
            </DialogTitle>
            <DialogContent style={{ backgroundColor: "#ececec" }}>
              <MetricInfoTable
                rows={[
                  { description: "Max", value: max, spanId: maxSpanId },
                  { description: "Min", value: min, spanId: minSpanId },
                  { description: "Median", value: median, spanId: "" },
                  {
                    description: "Quantity Overflows",
                    value: String(overflows),
                    spanId: "",
                  },
                ]}
              />
            </DialogContent>
          </>
        )}
      </Dialog>
    </>
  );
};

export default StatusCircle;
