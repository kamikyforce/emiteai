import { createTheme } from '@mui/material';

export const theme = createTheme({
  palette: {
    primary: {
      main: '#ff69ac',
      light: '#422BFF',
      dark: '#422BFF',
    },
    secondary: {
      main: '#0288D1',
      light: '#5EB8FF',
      dark: '#005B9F',
    },
    background: {
      default: '#FAFAFA',
      paper: '#FFFFFF',
    },
  },
  typography: {
    fontFamily: '"Inter", "Roboto", "Helvetica", "Arial", sans-serif',
    h1: { fontWeight: 700, fontSize: '2.5rem' },
    h2: { fontWeight: 600, fontSize: '2rem' },
    button: { textTransform: 'none', fontWeight: 500 },
  },
});