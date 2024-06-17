import { useState } from "react";
import InputLabel from "@mui/material/InputLabel";
import MenuItem from "@mui/material/MenuItem";
import FormControl from "@mui/material/FormControl";
import Select, { SelectChangeEvent } from "@mui/material/Select";

const SelectPriority = () => {
  const [priority, setPriority] = useState("");

  const handleChange = (event: SelectChangeEvent) => {
    const value = event.target.value;
    if (value === "none") setPriority("");
    else setPriority(event.target.value);
  };

  return (
    <FormControl sx={{ m: 1, width: 200 }} size="small">
      <InputLabel id="demo-select-small-label">Select by Priority</InputLabel>
      <Select
        labelId="demo-select-small-label"
        id="demo-select-small"
        value={priority}
        label="Select by Priority"
        onChange={handleChange}
      >
        <MenuItem value={"none"}>None</MenuItem>
        <MenuItem value={"low"}>Low</MenuItem>
        <MenuItem value={"medium"}>Medium</MenuItem>
        <MenuItem value={"high"}>High</MenuItem>
      </Select>
    </FormControl>
  );
};

export default SelectPriority;
