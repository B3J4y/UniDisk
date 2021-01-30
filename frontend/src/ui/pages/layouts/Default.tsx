import React from "react";
import DefaultDrawer from "./Drawer";

export default function DefaultLayout({ children }: { children: any }) {
  return <DefaultDrawer>{children}</DefaultDrawer>;
}
