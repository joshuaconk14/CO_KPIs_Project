const config = {
  development: {
    apiUrl: 'http://localhost:8080',
    wsUrl: 'ws://localhost:8080/ws/kpi'
  },
  production: {
    apiUrl: process.env.REACT_APP_API_URL || 'https://co-kpi-backend.herokuapp.com',
    wsUrl: process.env.REACT_APP_WS_URL || 'https://co-kpi-backend.herokuapp.com/ws/kpi'
  }
};

const environment = process.env.NODE_ENV || 'development';
export const apiUrl = config[environment].apiUrl;
export const wsUrl = config[environment].wsUrl; 