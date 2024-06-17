import "./Header.scss";

interface IHeader {
  label?: string;
}

const Header = ({ label = "DASHBOARD" }: IHeader) => {
  return (
    <div className="Header">
      <p className="jacques-francois-regular">{label}</p>
    </div>
  );
};

export default Header;
