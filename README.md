# 🏘️ StreetConnect — Community Complaint & Awareness Platform

> **Your Street. Your Voice. Your Community.**

StreetConnect is a full-stack web application that empowers residents of a street or locality to report civic issues, track their resolution, and stay informed about their community. Built with **Java Spring Boot** + **MySQL** + **HTML/CSS/JavaScript**.

---

## 🚀 Quick Start

### Prerequisites
- Java 17+
- MySQL 8.0 (running on `localhost:3306`)
- Maven (bundled at `~/.maven/apache-maven-3.9.9/`)

### Step 1 — Set Up Database

```bash
mysql -u root -p < database/schema.sql
mysql -u root -p < database/data.sql
```

### Step 2 — Start the Application

```bash
./start.sh
```

Or manually:

```bash
cd backend
java -jar target/streetconnect-backend-1.0.0.jar
```

### Step 3 — Open in Browser

Visit: **http://localhost:8080**

---

## 🔐 Demo Login Credentials

| Role       | Email                              | Password    |
|------------|-------------------------------------|-------------|
| **Admin**  | admin@streetconnect.com            | admin123    |
| **Moderator** | mod.tambaram@streetconnect.com  | password123 |
| **Resident** | arun.prakash@gmail.com           | password123 |
| **Resident** | priya.nair@hotmail.com           | password123 |

> You can also use the **Quick Demo Login** buttons inside the Login modal.

---

## ✨ Features

### For Residents
- 📝 Register with email or mobile number and join a street
- 📣 Post civic complaints with photo, category, priority, and location details
- 👍 Upvote / Support other residents' complaints
- 💬 Comment on complaints to add updates or context
- 🔔 Receive notifications when your complaint status changes
- 📊 Track complaint history (full timeline of status changes)
- 🗺️ View all complaints in your street filtered by status/category/priority

### For Street Moderators
- 🛡️ Dedicated moderation workspace with complaint queue
- ✏️ Update complaint status (Pending → Under Review → In Progress → Resolved/Rejected)
- 📢 Post street-level announcements visible to residents
- 📊 View street-specific resolution metrics

### For Administrators
- 🖥️ Full platform overview with system-wide metrics
- 👥 User directory and role management
- 🏘️ Register and manage streets/localities
- 📋 View and manage all complaints across all streets
- 📈 Charts: Resolution rates, category distribution, activity trends

---

## 🗂️ Project Structure

```
chellan complain/
├── backend/                    # Java Spring Boot Application
│   ├── src/main/java/com/streetconnect/
│   │   ├── config/             # Security, WebMVC, DataInitializer
│   │   ├── controller/         # REST API controllers
│   │   ├── dto/                # Data Transfer Objects
│   │   ├── entity/             # JPA entities (User, Complaint, etc.)
│   │   ├── exception/          # Global exception handling
│   │   ├── repository/         # Spring Data JPA repositories
│   │   ├── security/           # JWT authentication
│   │   └── service/            # Business logic services
│   └── src/main/resources/
│       └── application.properties
├── database/
│   ├── schema.sql              # Complete DB schema
│   └── data.sql                # Sample seed data (15 complaints, 13 users)
├── frontend/                   # Static HTML/CSS/JavaScript SPA
│   ├── index.html              # Homepage (complaint feed)
│   ├── css/style.css           # Design system & component styles
│   ├── js/
│   │   ├── api.js              # API client module
│   │   ├── auth.js             # Auth & session module
│   │   └── app.js              # Core app helpers & modals
│   └── pages/
│       ├── login.html          # Login page
│       ├── register.html       # Registration page
│       ├── complaint-detail.html  # Full complaint view + comments
│       ├── create-complaint.html  # New complaint form
│       ├── my-complaints.html  # Personal complaint tracker
│       ├── community.html      # Civic stats & street leaderboard
│       ├── profile.html        # Account settings
│       ├── moderator.html      # Moderator dashboard
│       └── admin.html          # Admin console
├── start.sh                    # One-click startup script
└── README.md                   # This file
```

---

## 🛠️ Technology Stack

| Layer      | Technology                    |
|------------|-------------------------------|
| Backend    | Java 17, Spring Boot 3.2.5    |
| Security   | Spring Security + JWT (JJWT)  |
| ORM        | Spring Data JPA + Hibernate   |
| Database   | MySQL 8.0                     |
| Frontend   | HTML5, CSS3, Vanilla JavaScript |
| UI Library | Bootstrap 5.3 + Bootstrap Icons |
| Charts     | Chart.js 4.4                  |

---

## 🌐 API Endpoints Reference

| Method | Endpoint                          | Description                  |
|--------|-----------------------------------|------------------------------|
| POST   | `/api/auth/login`                | Login and get JWT token      |
| POST   | `/api/auth/register`             | Register new user            |
| GET    | `/api/auth/me`                   | Get current user profile     |
| GET    | `/api/streets`                   | List all streets             |
| GET    | `/api/complaints`                | Get complaints (with filters)|
| POST   | `/api/complaints`                | File a new complaint         |
| GET    | `/api/complaints/{id}`           | Get complaint details        |
| PUT    | `/api/complaints/{id}/status`    | Update status (moderator)    |
| POST   | `/api/complaints/{id}/support`   | Toggle upvote/support        |
| GET    | `/api/complaints/{id}/comments`  | Get comments on complaint    |
| POST   | `/api/complaints/{id}/comments`  | Post a comment               |
| GET    | `/api/notifications`             | Get user notifications       |
| GET    | `/api/admin/stats`               | Platform-wide statistics     |
| GET    | `/api/admin/users`               | User directory (admin)       |

---

## 📊 Complaint Statuses

| Status       | Description                                    |
|--------------|------------------------------------------------|
| 🟡 Pending   | Just filed, awaiting moderator review          |
| 🟣 Under Review | Moderator has acknowledged the complaint   |
| 🔵 In Progress | Civic work or corrective action is ongoing  |
| 🟢 Resolved  | Issue has been fully addressed               |
| 🔴 Rejected  | Complaint found invalid or out of scope      |

---

## 👥 Demo Streets & Data

The seed data includes **3 streets** in Chennai:

1. **Gandhi Street**, Tambaram — 6 residents, 8 complaints
2. **Bharathi Nagar Main Road**, Velachery — 4 residents, 4 complaints  
3. **Anna Salai 3rd Cross**, Guindy — 3 residents, 3 complaints

Sample complaint categories: Street Lighting, Garbage & Waste, Road & Pavement, Water Supply, Drainage, Public Safety, Stray Animals, Tree & Greenery, Noise Pollution.
