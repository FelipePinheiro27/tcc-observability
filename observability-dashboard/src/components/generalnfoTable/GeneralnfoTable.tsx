import { Table, TableContainer } from "@mui/material";
import TableBody from "@mui/material/TableBody";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import Paper from "@mui/material/Paper";
import { StyledTableCell, StyledTableRow } from "../table/TableData";

type dataType = { description: string; value: string };

interface IGeneralnfoTable {
  rows: dataType[];
}

const GeneralnfoTable = ({ rows }: IGeneralnfoTable) => {
  return (
    <TableContainer component={Paper}>
      <Table sx={{ minWidth: 700 }} aria-label="customized table">
        <TableHead>
          <TableRow sx={{ height: "10px " }}>
            <StyledTableCell>Description</StyledTableCell>
            <StyledTableCell align="left">Value</StyledTableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {rows.map((row) => (
            <StyledTableRow key={row.description}>
              <StyledTableCell component="th" scope="row">
                {row.description}
              </StyledTableCell>
              <StyledTableCell align="left">{row.value}</StyledTableCell>
            </StyledTableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
};

export default GeneralnfoTable;
