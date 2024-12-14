import React from "react";
import { Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

import {
  NavigationMenu,
  NavigationMenuItem,
  NavigationMenuLink,
  NavigationMenuList,
  navigationMenuTriggerStyle,
} from "../components/ui/navigation-menu";

const Navbar = () => {
  const { logout } = useAuth(); // Access logout function from AuthContext

  return (
    <nav className="fixed top-0 left-0 w-full bg-gray-100 z-50 shadow-md">
      <div className="flex justify-center items-center p-4">
        <NavigationMenu>
          <NavigationMenuList className="flex space-x-8 text-lg">
            <NavigationMenuItem>
              <Link to="/home">
                <NavigationMenuLink
                  className={`${navigationMenuTriggerStyle()} text-xl`}
                >
                  Home
                </NavigationMenuLink>
              </Link>
            </NavigationMenuItem>
            <NavigationMenuItem>
              <Link to="/offers">
                <NavigationMenuLink
                  className={`${navigationMenuTriggerStyle()} text-xl`}
                >
                  Offers
                </NavigationMenuLink>
              </Link>
            </NavigationMenuItem>
            <NavigationMenuItem>
              <Link to="/transactions">
                <NavigationMenuLink
                  className={`${navigationMenuTriggerStyle()} text-xl`}
                >
                  Transactions
                </NavigationMenuLink>
              </Link>
            </NavigationMenuItem>
            <NavigationMenuItem>
              <Link to="/profile">
                <NavigationMenuLink
                  className={`${navigationMenuTriggerStyle()} text-xl`}
                >
                  Profile
                </NavigationMenuLink>
              </Link>
            </NavigationMenuItem>
            <NavigationMenuItem>
              <button
                onClick={logout}
                className={`${navigationMenuTriggerStyle()} bg-transparent border-0 text-xl`}
              >
                Logout
              </button>
            </NavigationMenuItem>
          </NavigationMenuList>
        </NavigationMenu>
      </div>
    </nav>
  );
};

export default Navbar;
