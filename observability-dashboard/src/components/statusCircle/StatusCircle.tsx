const colorByRisk = {
  low: "rgba(21, 81, 237, 0.7)",
  medium: "rgba(211, 114, 0, 0.7)",
  high: "rgba(255, 19, 19, 0.7)",
};

const StatusCircle = () => {
  return (
    <div
      className="StatusCircle"
      onMouseEnter={() => console.log("here")}
      onMouseLeave={() => console.log("out")}
      style={{
        backgroundColor: colorByRisk["low"],
        width: 16,
        height: 16,
        borderRadius: "50%",
        zIndex: 1,
      }}
    />
  );
};

export default StatusCircle;
