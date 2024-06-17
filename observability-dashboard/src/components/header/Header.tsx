import { Link, useNavigate } from "react-router-dom";
import { createSvgIcon } from "@mui/material/utils";
import "./Header.scss";

interface IHeader {
  label?: string;
  homePage?: boolean;
}

const HomeIcon = createSvgIcon(
  <path d="M10 20v-6h4v6h5v-8h3L12 3 2 12h3v8z" />,
  "Home"
);

const Header = ({ label = "DASHBOARD", homePage = false }: IHeader) => {
  const navigate = useNavigate();

  const handleHomeClick = () => {
    navigate("/");
  };

  return (
    <div className="Header">
      {!homePage ? (
        <div onClick={handleHomeClick} className="Header_home">
          <HomeIcon /> &nbsp;<p className="jacques-francois-regular">Home</p>
        </div>
      ) : (
        <div />
      )}
      <p className="jacques-francois-regular">{label}</p>
      <div />
    </div>
  );
};

export default Header;
