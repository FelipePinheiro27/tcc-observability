import Navigator from "./routes/Navigator";
import { BrowserRouter as Router } from "react-router-dom";

function App() {
  return (
    <div className="App">
      <Router>
        <Navigator />
      </Router>
    </div>
  );
}

export default App;
