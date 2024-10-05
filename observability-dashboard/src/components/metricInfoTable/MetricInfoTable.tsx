import { Table, TableContainer } from "@mui/material";
import TableBody from "@mui/material/TableBody";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import Paper from "@mui/material/Paper";
import { StyledTableCell, StyledTableRow } from "../table/TableData";

type dataType = { description: string; value: string; spanId: string };

interface IMetricInfoTable {
  rows: dataType[];
}

const MetricInfoTable = ({ rows }: IMetricInfoTable) => {
  return (
    <TableContainer component={Paper}>
      <Table sx={{ minWidth: 700 }} aria-label="customized table">
        <TableHead>
          <TableRow sx={{ height: "10px" }}>
            <StyledTableCell>Description</StyledTableCell>
            <StyledTableCell align="left">Value</StyledTableCell>
            <StyledTableCell align="left">Trace</StyledTableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {rows.map((row) => (
            <StyledTableRow key={row.description}>
              <StyledTableCell component="th" scope="row">
                {row.description}
              </StyledTableCell>
              <StyledTableCell align="left">{row.value}</StyledTableCell>
              <StyledTableCell align="left">
                {row.spanId ? (
                  <a href={`http://localhost:16686/trace/${row.spanId}`}>
                    Show Distributed Trace
                  </a>
                ) : (
                  ""
                )}
              </StyledTableCell>
            </StyledTableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
};

export default MetricInfoTable;
