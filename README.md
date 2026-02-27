# Dual-Write Data Synchronization via Spring AOP

##  Project Overview
This microservice demonstrates a robust dual-write database strategy using **Aspect-Oriented Programming (AOP)**. 

The primary goal of this application is to serve as a User Management system where primary transactional data is saved to a relational SQL database. Upon a successful transaction, a Spring AOP Aspect transparently intercepts the operation, adapts the data model, and asynchronously archives the record into a NoSQL MongoDB Atlas cluster for analytical purposes.

This project was developed as part of the **DTS Intern Exercise (Week 6)**.

##  Architecture & Data Flow

This application follows a strict Separation of Concerns, utilizing the **MVC** architecture alongside **AOP** and the **Adapter Pattern**.

1. **Client Request:** A POST request hits the `UserController` with a `UserAddRequest` DTO.
2. **Business Logic:** The `UserAddService` maps the DTO to a `UserEntity` and persists it via Spring Data JPA.
3. **AOP Interception:** The `MongoSyncAspect` listens for the `@AfterReturning` JoinPoint on the `UserRepository.save()` method.
4. **Data Adaptation:** If the SQL save is successful, the Aspect passes the `UserEntity` (Adaptee) to the `UserToMongoAdapter`.
5. **NoSQL Persistence:** The Adapter transforms the data into a flattened `UserDoc` (Target), which is then persisted to MongoDB.

## Technologies Used
* **Java 17+**
* **Spring Boot**
* **Databases:** Relational SQL (MySQL/SQL Server) & MongoDB Atlas
* **Lombok:** Boilerplate reduction (Builders, Data, Constructors)
* **Testing:** JUnit 5, Mockito 

## Project Structure
```text
src/main/java/com/assignment/aop/
├── adapter/          # Contains DataSyncAdapter and UserToMongoAdapter
├── aspect/           # Contains MongoSyncAspect 
├── controller/       # Contains UserController 
├── model/            # Contains Entities (SQL), Docs (Mongo), and DTOs
├── repository/       # Contains JpaRepository and MongoRepository interfaces
└── service/          # Contains UserAddService (Business logic)
