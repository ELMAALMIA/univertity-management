# Exam Surveillance Management with Java

This Java Swing application allows managing exam surveillance in an academic institution.

## Features

- Manage supervisors (add, edit, delete)
- Manage departments, majors, and courses 
- Schedule exams (add exams with date, time, locations)
- Assign supervisors to exams
- Generate and display surveillance schedules for each supervisor

## Technologies Used

- Language: Java 
- GUI: Swing
- Data persistence: MySQL database
- PDF generation: JasperReports or iText
- Containerization: Docker

## Setup with Docker

1. Make sure you have Docker and Docker Compose installed
2. Clone this repo
3. Navigate to the project directory
4. Run `docker-compose up -d` to start the MySQL container
5. Connect to the MySQL container and initialize the database:

```bash
cd config
docker exec -i mysql_db mysql -uroot -proot < init.sql