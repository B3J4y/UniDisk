import React from 'react';
import DefaultDrawer, { DefaultHeader } from './Drawer';

export default function DefaultLayout({ children }: { children: any }) {
  return <DefaultDrawer>{children}</DefaultDrawer>;
}

export function HeaderLayout(component: JSX.Element): JSX.Element {
  return <DefaultHeader>{component}</DefaultHeader>;
}
