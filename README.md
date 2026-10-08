# AI Recommendation System — Java Project

A beginner-friendly Spring Boot web application that demonstrates personalized recommendations using preference matching, rating and popularity.

## Main features
- User registration and login
- Password confirmation and basic validation
- SHA-256 password hashing for the demo authentication layer
- User dashboard with personalized recommendations
- Preference management
- Admin login and analytics dashboard
- Recommendation weight management (Preference + Rating + Popularity = 100)
- Six sample content items
- Responsive frontend using HTML, CSS and JavaScript
- REST APIs using Spring Boot

## Demo accounts
### Admin
- Email: `admin@airecommend.com`
- Password: `Admin@123`

### Existing user
- Email: `diwakar@example.com`
- Password: `Diwakar@123`

Other sample users: `aman@example.com` / `Aman@123`, `neha@example.com` / `Neha@123`.

## Run
```powershell
& "C:\Maven\apache-maven-3.10.0\bin\mvn.cmd" spring-boot:run "-Dspring-boot.run.arguments=--server.port=8081"
```
Open: `http://localhost:8081`

## Recommendation formula
The score combines three components:
- Preference match: 60%
- Rating: 25%
- Popularity: 15%

Admin can change the three weights as long as their total is 100.

## Important project note
This version uses an in-memory repository, so newly registered accounts and changed settings are available while the application is running. They reset when the application is restarted. This keeps the project simple for a Java programming project and easy to demonstrate.
