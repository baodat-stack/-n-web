# ShopSphere E-Commerce Project 🛒

ShopSphere is a production-level Java-based e-commerce platform built with **MVC architecture** and **DAO patterns**. This project has been upgraded from a basic functional site to a secure, performant, and feature-rich application ready for production-like environments.

## 🚀 Newly Added Features

### 🛡️ Advanced Security
*   **Password Hashing:** Implemented **BCrypt** for secure password storage.
*   **CSRF Protection:** Global protection against Cross-Site Request Forgery via a cryptographically secure token system and `CsrfFilter`.
*   **XSS Protection:** Implemented HTML escaping for all user-generated content to prevent Cross-Site Scripting.
*   **Access Control Filters:** Centralized `AuthFilter` and `AdminFilter` to protect secure routes (Cart, Profile, Admin Dashboard) at the container level.

### ⚡ Performance & Infrastructure
*   **Connection Pooling:** Migrated to **HikariCP** for high-performance database connection management.
*   **Jakarta EE 10 / Tomcat 10**: Fully upgraded to the `jakarta.*` namespace with JSTL 3.0 support.
*   **Clean MVC**: Strict separation of concerns between Models, DAOs, Servlets, and JSPs.

### 🍱 Enhanced Functionality
*   **Product Discovery:** Added **Category Filtering** and **Search** with real-time UI updates.
*   **Pagination:** Smooth product browsing with paginated results (8 items per page).
*   **User Profiles:** Dedicated profile management with identity verification and password change flows.
*   **Order Details:** Granular order tracking including itemized breakdown and shipping address history.
*   **Inventory Management:** Real-time stock tracking with "Out of Stock" alerts and purchase-time reduction.

## 🛠️ Tech Stack

*   **Frontend:** Bootstrap 5, JSTL 3.0, Vanilla CSS
*   **Backend:** Java Servlets (Jakarta EE 10)
*   **Database:** MySQL 8.0+
*   **Libraries:** HikariCP (Connection Pool), jBCrypt (Security), SLF4J (Logging)
*   **Server:** Apache Tomcat 10+

## 🔧 Local Setup Instructions

### 1. Database Setup
1. Ensure MySQL is installed.
2. Run the `migration_v2.sql` file (found in the root) to create the schema and seed the database.
   ```sql
   mysql -u root -p < migration_v2.sql
   ```

### 2. Dependencies
Ensure the following JARs are in your `src/main/webapp/WEB-INF/lib/` folder:
*   `mysql-connector-j-x.x.x.jar`
*   `jbcrypt-0.4.jar`
*   `HikariCP-5.1.0.jar`, `slf4j-api-2.0.9.jar`, `slf4j-simple-2.0.9.jar`
*   `jakarta.servlet.jsp.jstl-api-3.0.0.jar`
*   `jakarta.servlet.jsp.jstl-3.0.1.jar`

### 3. Configure Credentials
1. Create `src/main/resources/db.properties` (or `src/main/java/db.properties` depending on your IDE build path).
2. Add your credentials:
   ```properties
   db.url=jdbc:mysql://localhost:3306/ecommerce_db
   db.username=YOUR_USERNAME
   db.password=YOUR_PASSWORD
   db.driver=com.mysql.cj.jdbc.Driver
   ```

### 4. Run the Project
1. Import as an **existing Dynamic Web Project** in Eclipse.
2. Right-click project -> **Run As > Run on Server** (Select Tomcat 10).

---
*A comprehensive Java Web Showcase by Anirudh Kumar.*

## 🤝 Contributing
Contributions, issues, and feature requests are welcome! Feel free to open a pull request or submit an issue.

## 📝 License
This project is open-source and available under the MIT License. 
