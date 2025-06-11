import React from 'react';
import { ThemeProvider, createTheme } from '@mui/material';
import TestComponent from './components/TestComponent';

const theme = createTheme({
  palette: {
    primary: {
      main: '#1976d2',
    },
    secondary: {
      main: '#dc004e',
    },
  },
});

const App: React.FC = () => {
  return (
    <ThemeProvider theme={theme}>
      <TestComponent />
    </ThemeProvider>
  );
};

export default App; 