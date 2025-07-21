# FileHub - Full Stack File Management System

A modern full-stack web application for file management built with Spring Boot (Backend) and Vue.js (Frontend).

## 📁 Project Structure

```
filehub/
├── filehub-back/          # Spring Boot Backend
│   ├── src/main/java/
│   │   └── com/duongdat/filehub/
│   │       ├── config/         # Configuration classes
│   │       ├── controller/     # REST Controllers
│   │       ├── dto/           # Data Transfer Objects
│   │       ├── entity/        # JPA Entities
│   │       ├── repository/    # JPA Repositories
│   │       └── service/       # Business Logic
│   └── src/main/resources/
│       └── application.properties
├── filehub-front/         # Vue.js Frontend
│   ├── src/
│   │   ├── components/    # Reusable Vue Components
│   │   ├── views/         # Page Components
│   │   ├── router/        # Vue Router Configuration
│   │   ├── stores/        # Pinia State Management
│   │   └── services/      # API Services
│   └── public/
└── schema.sql            # Database Schema
```

## 🚀 Technologies Used

### Backend
- **Spring Boot 3.5.3** - Java framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Database abstraction
- **MySQL** - Database
- **Lombok** - Reduce boilerplate code
- **Gradle** - Build tool

### Frontend
- **Vue.js 3** - Progressive JavaScript framework
- **TypeScript** - Type-safe JavaScript
- **Vite** - Fast build tool
- **Vue Router** - Client-side routing
- **Pinia** - State management
- **Tailwind CSS** - Utility-first CSS framework
- **Axios** - HTTP client

## 📋 Prerequisites

- **Java 21** or higher
- **Node.js 18** or higher
- **MySQL 8.0** or higher
- **npm** or **yarn**

## ⚙️ Setup Instructions

### 1. Database Setup

1. Create a MySQL database:
```sql
CREATE DATABASE filehub_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. Run the schema.sql file:
```bash
mysql -u root -p filehub_db < schema.sql
```

### 2. Backend Setup

1. Navigate to the backend directory:
```bash
cd filehub-back
```

2. Update `src/main/resources/application.properties` with your database credentials:
```properties
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password
```

3. Build and run the application:
```bash
# Windows
./gradlew bootRun

# Linux/Mac
./gradlew bootRun
```

The backend will start on `http://localhost:8080`

### 3. Frontend Setup

1. Navigate to the frontend directory:
```bash
cd filehub-front
```

2. Install dependencies:
```bash
npm install
```

3. Start the development server:
```bash
npm run dev
```

The frontend will start on `http://localhost:5173`

## 🔑 Default Credentials

The application comes with a default admin account:
- **Username:** `admin`
- **Password:** `admin123`

## 📚 API Documentation

### Authentication Endpoints

#### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "fullName": "John Doe",
  "password": "password123"
}
```

#### Login User
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "password123"
}
```

### Response Format
All API responses follow this format:
```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... }
}
```

## 🎨 Frontend Features

- **Authentication:** Login/Register with form validation
- **Responsive Design:** Mobile-first design with Tailwind CSS
- **State Management:** Pinia stores for user authentication
- **Routing:** Protected routes with navigation guards
- **User Interface:** Modern UI with dropdown menus and user avatars

## 🛠️ Available Scripts

### Backend
```bash
./gradlew bootRun        # Run the application
./gradlew build          # Build the application
./gradlew test           # Run tests
```

### Frontend
```bash
npm run dev              # Start development server
npm run build            # Build for production
npm run preview          # Preview production build
```

## 🔧 Development

### Code Structure

#### Backend Layers
1. **Controller Layer:** REST API endpoints
2. **Service Layer:** Business logic
3. **Repository Layer:** Data access
4. **Entity Layer:** JPA entities
5. **DTO Layer:** Data transfer objects

#### Frontend Structure
1. **Views:** Page-level components
2. **Components:** Reusable UI components
3. **Stores:** Pinia state management
4. **Services:** API communication
5. **Router:** Navigation configuration

### Security Features
- Password encryption using BCrypt
- CORS configuration for cross-origin requests
- Protected routes on both frontend and backend
- JWT-ready authentication structure

## 🚀 Deployment

### Backend Deployment
1. Build the JAR file:
```bash
./gradlew build
```

2. Run the JAR file:
```bash
java -jar build/libs/filehub-0.0.1-SNAPSHOT.jar
```

### Frontend Deployment
1. Build for production:
```bash
npm run build
```

2. Deploy the `dist` folder to your web server

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📄 License

This project is licensed under the MIT License.

## 📞 Support

For support, email duongdat030@gmail.com or create an issue on GitHub.
