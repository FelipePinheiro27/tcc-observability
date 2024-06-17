import { BrowserRouter as Router } from "react-router-dom";
import Navigator from "./routes/Navigator";

function App() {
  // const allData = ()
  return (
    <div className="App">
      <Router>
        <Navigator />
      </Router>
    </div>
  );
}

export default App;
