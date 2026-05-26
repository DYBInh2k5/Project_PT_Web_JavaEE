# PROJECT DETAILS

## Project Overview

This is a Java EE class project migrated to Spring Boot + JPA. The app is a bookstore (shop bán sách) using SQL Server `QLBanSach`.

## Architecture

- Spring Boot application entry: `src/java/com/project/BookstoreApiApplication.java`
- JPA persistence unit: `bookstorePU` defined in `src/main/resources/META-INF/persistence.xml`
- Data access: DAOs under `src/java/com/project/dao/` (many migrated to JPA)
- REST controllers: `src/java/com/project/api/`
- JSP UI + servlets kept under `web/` and `src/java/com/project/web/` for compatibility
- Build: Maven (`pom.xml`), packaging as WAR with embedded Tomcat via Spring Boot

## Key Files

- `pom.xml`: project dependencies and build configuration
- `src/main/resources/application.properties`: Spring Boot datasource and JPA settings
- `src/main/resources/META-INF/persistence.xml`: JPA persistence unit
- `src/java/com/project/JpaSupport.java`: helper for EntityManagerFactory (created during migration)
- `src/java/com/project/BookstoreApiApplication.java`: Spring Boot main application
- `src/java/com/project/dao/BookDAO.java`: example DAO migrated to use EntityManager

## Migration Notes

- Legacy `SqlServerConnection.java` removed; DAOs converted to use JPA `EntityManager`.
- Many `catch (SQLException ...)` blocks were updated to `catch (Exception ...)` or rethrow `RuntimeException` to avoid compile errors after removal of JDBC helper.
- Some DAO method signatures still declare `throws SQLException` — these should be harmonized to unchecked exceptions or updated across callers.

## API Endpoints (summary)

- `/api/auth/*` — authentication endpoints
- `/api/db-check` — DB health check
- `/api/books` — book CRUD and listing
- `/api/customers` — customer CRUD
- `/api/invoices` — invoice operations
- `/api/promotions` — promotions
- `/api/reports/*` — reports (revenue, top sold)
- `/api/shop/*` — public shop endpoints (catalog, cart, checkout, order lookup)
- `/api/dashboard` — admin dashboard

## How to Run

1. Ensure JDK 21 is installed and IntelliJ configured (see docs/INTELLIJ_RUN.md).
2. Create SQL Server database `QLBanSach` and update credentials in `docs/SQL_SERVER_SETUP.md` if needed.
3. From project root, run:

```bash
mvn -DskipTests=true spring-boot:run
```

4. Access app at `http://localhost:8080/Project/` and test `http://localhost:8080/Project/api/db-check`.

## Troubleshooting

- If compile errors about `jakarta.*` or package mismatches appear, ensure source root is `src/java` and `pom.xml` configured correctly.
- If `SQLException` compile errors persist, run a search for `throws SQLException` and update method signatures to `throws Exception` or remove checked exceptions.

## Next Steps

- Finish harmonizing exceptions across DAOs and callers.
- Run `mvn clean package` to validate build and fix remaining compile errors.

