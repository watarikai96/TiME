# RoadMap - Skills - 20 Days Plan

### **Day 1 Plan: Project Setup (10 Hours)**

 **Goal:** Set up the development environment for **Android (WebView), AngularJS (Frontend), Spring Boot (Backend), PostgreSQL (Database), and Gradle (Build System).**

### **Tasks for Day 1**

 **1. Install & Set Up Development Tools** (**2 Hours**)

- [x] **Android Studio** (For Android WebView App)
- [x] **WebStorm** (For AngularJS Frontend)
- [x] **IntelliJ IDEA** (For Spring Boot Backend)
- [x] **DataGrip** (For PostgreSQL Database)
- [x] **Postman** (For API Testing)
- [x] **Gradle** (For Build Automation)
- [x] **Git & GitHub** (For Version Control)

 **2. Initialize Backend (Spring Boot + Gradle) [2 Hours]**

- [ ] Create a **new Spring Boot project** using Gradle.
- [ ] Add dependencies:
  - [ ] **Spring Web** (for REST APIs)
  - [ ] **Spring Data JPA** (for database integration)
  - [ ] **PostgreSQL Driver**
  - [ ] **Lombok** (for clean code)
- [ ] Test the backend with a simple **"Hello World" API.**

 **3. Set Up PostgreSQL Database [2 Hours]**

- Install PostgreSQL and create a new database (`time_ai_db`).
- Create tables:
    - **users** (id, name, email, password, settings)
    - **tasks** (id, title, duration, status)
    - **sessions** (id, start_time, end_time, completed, user_id)
- Connect Spring Boot to PostgreSQL using **application.properties**.
- Test database connection.

 **4. Initialize Frontend (AngularJS) [2 Hours]**

- Create a **new AngularJS project** (`time-ai-frontend`).
- Install required packages:
    - **AngularJS Router**
    - **Bootstrap or Tailwind for UI**
    - **RxJS for state management**
- Create a simple **login page & dashboard component.**
- Start the AngularJS server and check the UI.

 **5. Initialize Android WebView App [2 Hours]**

- Create a **basic Android app** in Android Studio.
- Add **WebView component** to load the AngularJS frontend.
- Configure WebView settings for **smooth performance**.
- Run the app on an **Android emulator or physical device.**

###  **Expected Outcome After Day 1**

 Backend setup with **Spring Boot + Gradle**
 Database setup with **PostgreSQL + JPA**
 Frontend setup with **AngularJS + WebView**
 A working **Android app loading the AngularJS UI**

**Next Up: Day 2 - Implement UI Design (Moleskine-style Interface).**