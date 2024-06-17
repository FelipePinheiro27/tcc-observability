import Dialog from "@mui/material/Dialog";
import DialogContent from "@mui/material/DialogContent";
import "./DialogSystemInfo.scss";

interface IDialogSystemInfo {
  open: boolean;
  setOpen: React.Dispatch<React.SetStateAction<boolean>>;
}

const DialogSystemInfo = ({ open, setOpen }: IDialogSystemInfo) => {
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
            <strong>Requests/Second (last 5 min): </strong> 100
          </div>
          <div>
            <strong>Total Requests: </strong> 1000
          </div>
          <div>
            <strong>Total Errors: </strong> 20
          </div>
          <div>
            <strong>Errors Percentage: </strong> 2%
          </div>
        </div>
      </DialogContent>
    </Dialog>
  );
};

export default DialogSystemInfo;
