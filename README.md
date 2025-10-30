# Inventory Service

This project is a Spring Boot application for a inventory service in an e-commerce application. It provides RESTful APIs for inventory management and integrates with a Mongodb database.

## Project Structure

```
inventory-service
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── ecomapp
│   │   │           └── inventory_service
│   │   │               ├── InventoryServiceApplication.java
│   │   │               ├── controller
│   │   │               ├── model
│   │   │               ├── repository
│   │   │               └── service
│   │   └── resources
│   │       ├── application.properties
│   │       ├── static
│   │       └── templates
│   └── test
│       └── java
│           └── com
│               └── ecomapp
│                   └── inventory_service
│                       └── InventoryServiceApplicationTests.java
├── Dockerfile.dev
├── Dockerfile
├── docker-compose.dev.yml
├── .dockerignore
├── pom.xml
└── README.md
```

## Getting Started

### Prerequisites

- Docker
- Docker Compose
- Java 17 (for local development)

### Building the Application

To build the application, run the following command:

```
mvn clean package
```

This will create a `inventory-service.jar` file in the `target` directory.

### Running the Application with Docker

To run the application using Docker, execute the following command:

```
docker-compose up --build
```

This command will build the Docker image and start the inventory service. The application will be accessible at `http://localhost:8082`.

### Auto-reloading

The application is configured to automatically reload when code changes are made. The source code is mounted as a volume in the Docker container, allowing for immediate reflection of changes without needing to rebuild the image.

### Testing

To run the tests, you can execute:

```
mvn test
```

This will run all the test cases defined in the `src/test` directory.

## Configuration

The application configuration can be found in the `src/main/resources/application.properties` file. You can adjust the database connection settings and other properties as needed.

## License

This project is licensed under the MIT License.