# MyPlant Backend - Setup Guide

## Overview
MyPlant is a full-stack smart plant care application that helps users manage their indoor plants with automated watering reminders based on plant type and real-time weather data.

## Prerequisites
- Java 17+
- Maven 3.6+
- MySQL 8.0+
- Git

## Installation Steps

### 1. Clone Repository
```bash
git clone <repository-url>
cd backend
```

### 2. Configure Database
Create MySQL database:
```sql
CREATE DATABASE myplant;
```

Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/myplant?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_password
```

### 3. Install Database Schema
```bash
mysql -u root -p myplant < src/main/resources/schema.sql
```

### 4. Configure API Keys
Copy `.env.example` to `.env` and fill in:
```bash
cp .env.example .env
```

Configure in `application.properties`:
- **JWT Secret**: Change `jwt.secret` to a long random string
- **Email**: Setup Gmail with app password (see below)
- **Weather API**: Get free key from https://openweathermap.org/api
- **Twilio**: (Optional) For WhatsApp notifications

### 5. Setup Gmail for Email Notifications
1. Go to https://myaccount.google.com/
2. Select "Security" → Enable "2-Step Verification"
3. Generate "App password" for "Mail"
4. Use this password in `application.properties`

```properties
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
```

### 6. Build Project
```bash
mvn clean install
```

### 7. Run Application
```bash
mvn spring-boot:run
```

API will start on `http://localhost:8080`

## Project Structure

```
backend/
├── src/main/
│   ├── java/com/myplant/
│   │   ├── config/              # Spring configuration classes
│   │   ├── controller/          # REST API endpoints
│   │   ├── dto/                 # Data transfer objects
│   │   ├── entity/              # JPA entities
│   │   ├── exception/           # Custom exceptions
│   │   ├── repository/          # Data access layer
│   │   ├── scheduler/           # Scheduled tasks
│   │   ├── security/            # JWT authentication
│   │   ├── service/             # Business logic
│   │   └── MyPlantApplication   # Main Spring Boot class
│   └── resources/
│       ├── application.properties
│       ├── application-dev.properties
│       └── schema.sql
├── pom.xml                      # Maven configuration
└── .env.example                 # Environment variables template
```

## Technology Stack

### Framework
- **Spring Boot 3.1.5** - Application framework
- **Spring Data JPA** - ORM and database access
- **Spring Security** - Authentication and authorization

### Database
- **MySQL 8.0** - Relational database
- **Hibernate** - ORM

### Authentication & Security
- **JWT (JSON Web Tokens)** - Stateless authentication
- **BCrypt** - Password encryption
- **Spring Security Filters** - Request authentication

### Utilities
- **Lombok** - Reduce boilerplate code
- **WebClient** - HTTP client for external APIs
- **Spring Mail** - Email notifications
- **Twilio** - WhatsApp notifications (optional)

## API Endpoints

See `API_DOCUMENTATION.md` for complete API reference

## Key Features

### 1. User Authentication
- Registration with email validation
- JWT-based login
- Password encryption using BCrypt
- Token-based stateless sessions

### 2. Plant Management
- Create, read, update, delete plants
- Link plants to plant care knowledge base
- Track plant health status
- Support for indoor/outdoor plants

### 3. Smart Watering Logic
- Base watering intervals from plant database
- Weather-based adjustments (temperature, humidity, rain)
- Custom watering intervals per user
- Overdue plant detection

### 4. Automated Notifications
- Scheduled daily watering reminders (9 AM)
- Overdue plant alerts (every 6 hours)
- Email notifications via Gmail SMTP
- WhatsApp notifications via Twilio (optional)

### 5. Plant Knowledge Database
- 10+ predefined plant types
- Optimal care information for each plant
- Care tips, common issues, and solutions
- Difficulty levels and growth rates

### 6. Weather Integration
- Real-time weather data from OpenWeatherMap
- Temperature-based watering adjustments
- Rain detection (skip watering)
- Humidity considerations

### 7. Dashboard & Analytics
- Upcoming watering tasks
- Recently watered plants
- Watering history tracking
- Plant health indicators

## Development

### Running Tests
```bash
mvn test
```

### Running with Dev Profile
```bash
mvn spring-boot:run -Dspring-boot.run.arguments='--spring.profiles.active=dev'
```

### View Logs
```bash
tail -f logs/myplant.log
```

## Database Queries

### Get Plants Due for Watering
```sql
SELECT p.id, p.name, DATEDIFF(CURDATE(), p.last_watered_date) as days_since_watering
FROM plants p
WHERE p.user_id = 1
AND DATEDIFF(CURDATE(), p.last_watered_date) >= 7;
```

### Get Recent Notifications
```sql
SELECT * FROM notifications
WHERE user_id = 1 AND created_at > DATE_SUB(NOW(), INTERVAL 7 DAY)
ORDER BY created_at DESC;
```

## Troubleshooting

### Database Connection Failed
- Ensure MySQL is running: `mysql -u root -p`
- Check credentials in `application.properties`
- Verify database exists: `SHOW DATABASES;`

### Email Not Sending
- Verify Gmail app password is correct
- Check "Less secure app access" is enabled
- Ensure port 587 is not blocked by firewall
- Check application logs: `tail -f logs/myplant.log`

### Weather API Not Working
- Verify OpenWeatherMap API key is correct
- Check API call rate limit hasn't been exceeded
- Test manually: `curl "https://api.openweathermap.org/data/2.5/weather?q=London&appid=KEY&units=metric"`

## Deployment

### Production Checklist
- [ ] Change JWT secret to strong random string
- [ ] Use strong database password
- [ ] Enable HTTPS
- [ ] Setup reverse proxy (nginx)
- [ ] Configure CORS for production domain
- [ ] Enable request logging
- [ ] Setup database backups
- [ ] Configure email sender address
- [ ] Test notifications thoroughly
- [ ] Monitor error logs

### Docker Deployment
Create `Dockerfile`:
```dockerfile
FROM openjdk:17-slim
COPY target/smart-plant-care-1.0.0.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Build and run:
```bash
mvn clean package
docker build -t myplant-api .
docker run -p 8080:8080 myplant-api
```

## Performance Considerations

1. **Database Indexes**: Already created on frequently queried fields
2. **Connection Pooling**: HikariCP configured with 10 connections
3. **Caching**: Consider adding Redis for notification caching
4. **Query Optimization**: Use projection queries to reduce data transfer
5. **Async Operations**: Email sending could be made async with @Async

## Future Enhancements

- [ ] Multi-language support
- [ ] Mobile app (React Native)
- [ ] Advanced analytics dashboard
- [ ] Plant disease detection (AI/ML)
- [ ] Social features (share plants, tips)
- [ ] Integration with smart home systems
- [ ] Soil moisture sensor integration
- [ ] Payment system for premium features
- [ ] Batch operations API
- [ ] WebSocket for real-time updates

## Support & Documentation

- **API Docs**: See `API_DOCUMENTATION.md`
- **Architecture**: See `ARCHITECTURE.md`
- **Database Design**: See database diagram in documentation
- **Issues**: Report bugs in GitHub Issues

## License

MIT License

## Contact

For questions or support, contact the development team.
