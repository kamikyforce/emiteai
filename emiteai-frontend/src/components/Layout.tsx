import React from 'react';
import { AppBar, Toolbar, Typography, Box, Button } from '@mui/material';
import { Link as RouterLink, useLocation } from 'react-router-dom';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

interface LayoutProps {
  children: React.ReactNode;
}

export const Layout: React.FC<LayoutProps> = ({ children }) => {
  const { pathname } = useLocation();

  return (
    <>
      <AppBar position="static">
        <Toolbar sx={{ gap: 2 }}>
          <Typography variant="h6" sx={{ flexGrow: 1 }}>
            EmiteAí
          </Typography>
          <Button
            component={RouterLink}
            to="/"
            color="inherit"
            disabled={pathname === '/'}
          >
            Cadastro
          </Button>
          <Button
            component={RouterLink}
            to="/relatorio"
            color="inherit"
            disabled={pathname === '/relatorio'}
          >
            Relatório
          </Button>
        </Toolbar>
      </AppBar>
      <Box component="main">{children}</Box>
      <ToastContainer 
        position="top-right"
        autoClose={3000}
        hideProgressBar={false}
        newestOnTop={false}
        closeOnClick
        rtl={false}
        pauseOnFocusLoss
        draggable
        pauseOnHover
        limit={3}
        theme="colored"
      />
    </>
  );
};
