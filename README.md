# 🌱 AI Smart Plant Care

AI Smart Plant Care is a full-stack application designed to help users manage and monitor their plants through a centralized digital platform. The system provides plant management, watering schedules, care history, and an interactive dashboard for keeping track of plant-related activities.

## 📌 Overview

Taking care of multiple plants can become difficult when watering schedules and care information are managed manually.

AI Smart Plant Care provides a structured solution where users can:

* 🌿 Add and manage plants
* 💧 Create and track watering schedules
* 📅 Maintain watering history
* 📊 View plant-related information through a dashboard
* 🔐 Secure user authentication
* 🔄 Communicate with the backend through REST APIs
* 🗄️ Store plant and user information in a relational database

## ✨ Features

### 🔐 User Authentication

* User registration and login
* Secure authentication using JWT
* Protected application resources

### 🌱 Plant Management

* Add new plants
* View plant information
* Update plant details
* Remove plants when they are no longer required

### 💧 Watering Management

* Create watering schedules
* Track watering activities
* Maintain watering history
* Monitor upcoming watering requirements

### 📊 Dashboard

The dashboard provides an overview of the user's plants and their care activities.

### 🔄 REST API

The frontend communicates with the backend using RESTful APIs for:

* Authentication
* Plant management
* Watering schedules
* Plant history
* User-related operations

## 🏗️ System Architecture

The application follows a layered full-stack architecture:

```text
┌──────────────────────┐
│      Frontend        │
│   React Application  │
└──────────┬───────────┘
           │
           │ REST API
           ▼
┌──────────────────────┐
│       Backend        │
│     Spring Boot      │
└──────────┬───────────┘
           │
           │ JPA / JDBC
           ▼
┌──────────────────────┐
│       MySQL          │
│      Database        │
└──────────────────────┘
```

## 🛠️ Technology Stack

### Frontend

* React
* JavaScript / TypeScript
* HTML5
* CSS3

### Backend

* Java
* Spring Boot
* Spring Security
* JWT Authentication
* REST APIs
* Maven

### Database

* MySQL

### Development Tools

* Git
* GitHub
* IntelliJ IDEA / VS Code
* Postman

## 📂 Project Structure

```text
AI-Smart-Plant-Care/
│
├── frontend/
│   ├── src/
│   ├── public/
│   └── package.json
│
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   └── resources/
│   │   └── test/
│   └── pom.xml
│
├── README.md
└── .gitignore
```

> The exact directory structure may vary depending on the current project configuration.

## ⚙️ Installation and Setup

### Prerequisites

Make sure the following are installed:

* Java 17+
* Node.js
* npm
* MySQL
* Git
* Maven

### 1. Clone the Repository

```bash
git clone <your-repository-url>
cd AI-Smart-Plant-Care
```

### 2. Configure MySQL

Create a database:

```sql
CREATE DATABASE smart_plant_care;
```

Update the backend database configuration with your local MySQL credentials.

Example:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/smart_plant_care
spring.datasource.username=your_username
spring.datasource.password=your_password
```

Do not commit real database passwords or other secrets to GitHub.

### 3. Start the Backend

Navigate to the backend directory:

```bash
cd backend
```

Run:

```bash
mvn spring-boot:run
```

The backend will start on the configured Spring Boot port.

### 4. Start the Frontend

Open another terminal:

```bash
cd frontend
```

Install dependencies:

```bash
npm install
```

Start the development server:

```bash
npm run dev
```

Open the local URL displayed by the frontend development server.

## 🔑 Authentication Flow

The application uses JWT-based authentication.

```text
User
  │
  ▼
Login / Register
  │
  ▼
Backend Authentication
  │
  ▼
JWT Token
  │
  ▼
Frontend
  │
  ▼
Authenticated API Requests
```

Protected backend endpoints validate the JWT before allowing access to protected resources.

## 🔄 Application Flow

```text
User
 │
 ▼
Frontend Dashboard
 │
 ├── Manage Plants
 │
 ├── Manage Watering Schedule
 │
 └── View History
 │
 ▼
REST API
 │
 ▼
Spring Boot Backend
 │
 ▼
MySQL Database
```

## 🧪 Testing

Backend APIs can be tested using tools such as:

* Postman
* Insomnia
* Browser developer tools

Example API categories:

```text
POST   /api/auth/register
POST   /api/auth/login
GET    /api/plants
POST   /api/plants
PUT    /api/plants/{id}
DELETE /api/plants/{id}
```

> Update the endpoint examples according to the actual routes implemented in the project.

## 🔒 Security

The project follows basic application security practices including:

* JWT-based authentication
* Protected API endpoints
* Password protection
* Input validation
* Separation of frontend and backend responsibilities
* Avoiding sensitive credentials in source control

## 🚀 Future Improvements

Possible future enhancements include:

* 🤖 AI-based plant health recommendations
* 📷 Plant disease detection using images
* 🌦️ Weather-based watering recommendations
* 🔔 Automated watering notifications
* 📱 Mobile application
* 📈 Plant growth analytics
* 🌡️ IoT sensor integration
* ☁️ Cloud deployment

## 🎯 Project Objective

The main objective of AI Smart Plant Care is to provide a simple digital platform for organizing plant-care activities while creating a foundation for future AI-assisted plant monitoring and recommendations.

## 👨‍💻 Author

**K Manjunath**

Ramaiah Institute of Technology

## 📄 License

This project is intended for educational and development purposes. Add the appropriate license file if the project is being distributed publicly.
