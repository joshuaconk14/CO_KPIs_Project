# Instagram KPI Dashboard

A real-time Instagram KPI dashboard built with React, Spring Boot, and WebSocket technology.

## Project Structure

```
instagram-kpi-dashboard/
├── frontend/                 # React frontend application
│   ├── public/              # Static files
│   └── src/                 # React source code
├── backend/                 # Spring Boot backend application
│   ├── src/                # Java source code
│   └── pom.xml             # Maven dependencies
└── docker/                 # Docker configuration files
```

## Technology Stack

- **Frontend**: React.js with TypeScript
- **Backend**: Spring Boot 3.x
- **Database**: PostgreSQL
- **Cloud**: Azure
- **Real-time Updates**: WebSocket
- **Containerization**: Docker

## Prerequisites

- Node.js (v18 or higher)
- Java 17 or higher
- Maven
- PostgreSQL
- Docker (optional)

## Getting Started

### Backend Setup

1. Navigate to the backend directory:
   ```bash
   cd backend
   ```

2. Build the project:
   ```bash
   ./mvnw clean install
   ```

3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

### Frontend Setup

1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the development server:
   ```bash
   npm start
   ```

## Development

- Backend runs on: http://localhost:8080
- Frontend runs on: http://localhost:3000
- WebSocket endpoint: ws://localhost:8080/ws/kpi

## Production Deployment

The application is containerized using Docker and can be deployed to Azure:

1. Build the Docker images:
   ```bash
   docker-compose build
   ```

2. Run the containers:
   ```bash
   docker-compose up -d
   ```

## Environment Variables

Create `.env` files in both frontend and backend directories with the following variables:

### Backend (.env)
```
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/instagram_kpi
SPRING_DATASOURCE_USERNAME=your_username
SPRING_DATASOURCE_PASSWORD=your_password
INSTAGRAM_API_KEY=your_instagram_api_key
```

### Frontend (.env)
```
REACT_APP_API_URL=http://localhost:8080
REACT_APP_WS_URL=ws://localhost:8080/ws/kpi
```

## Contributing

1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a new Pull Request