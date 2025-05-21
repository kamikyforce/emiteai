import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { ThemeProvider } from '@mui/material/styles';
import { Box, Container } from '@mui/material';
import { Layout } from './components/Layout';
import { PersonForm } from './components/PersonForm';

import { theme } from './styles/theme';
import ReportPage from 'components/ReportPage';

function App() {
  return (
    <ThemeProvider theme={theme}>
      <BrowserRouter>
        <Layout>
          <Box sx={{ py: 4 }}>
            <Container maxWidth="md">
              <Routes>
                <Route path="/" element={<PersonForm />} />
                <Route path="/relatorio" element={<ReportPage />} />
              </Routes>
            </Container>
          </Box>
        </Layout>
      </BrowserRouter>
    </ThemeProvider>
  );
}

export default App;
