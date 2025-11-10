# Inventory Service

This project is a Spring Boot application for an inventory service in an e-commerce application. It provides:
- **RESTful APIs** for inventory management
- **gRPC Server** for inter-service communication (used by order-service)
- **MongoDB** integration for data persistence

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
│   │   │               ├── service
│   │   │               └── grpc              # gRPC service implementations
│   │   ├── proto                             # Protocol Buffer definitions
│   │   │   └── inventory.proto
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
├── target
│   └── generated-sources
│       └── protobuf                          # Auto-generated gRPC classes
│           ├── java                          # Proto message classes
│           └── grpc-java                     # gRPC service stubs
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

### Key Configuration Properties

```properties
# HTTP REST API port
server.port=8082

# gRPC Server port (for inter-service communication)
grpc.server.port=${GRPC_SERVER_PORT:50051}

# MongoDB connection
spring.data.mongodb.username=${MONGO_INITDB_ROOT_USERNAME:root}
spring.data.mongodb.password=${MONGO_INITDB_ROOT_PASSWORD:secret}
spring.data.mongodb.database=${MONGO_INITDB_DATABASE:inventory_db}
```

---

## 🚀 How to Implement gRPC in a Spring Boot Project

This section provides detailed steps to add gRPC server functionality to any Spring Boot project for inter-service communication.

### Step 1: Add gRPC Dependencies to `pom.xml`

Add the following dependencies and properties:

```xml
<properties>
    <java.version>17</java.version>
    <grpc.version>1.60.0</grpc.version>
    <protobuf.version>3.25.1</protobuf.version>
    <grpc-spring-boot-starter.version>3.1.0.RELEASE</grpc-spring-boot-starter.version>
</properties>

<dependencies>
    <!-- gRPC Spring Boot Starter - Auto-configures gRPC server -->
    <dependency>
        <groupId>net.devh</groupId>
        <artifactId>grpc-spring-boot-starter</artifactId>
        <version>${grpc-spring-boot-starter.version}</version>
    </dependency>

    <!-- gRPC Core Dependencies -->
    <dependency>
        <groupId>io.grpc</groupId>
        <artifactId>grpc-netty-shaded</artifactId>
        <version>${grpc.version}</version>
    </dependency>
    <dependency>
        <groupId>io.grpc</groupId>
        <artifactId>grpc-protobuf</artifactId>
        <version>${grpc.version}</version>
    </dependency>
    <dependency>
        <groupId>io.grpc</groupId>
        <artifactId>grpc-stub</artifactId>
        <version>${grpc.version}</version>
    </dependency>

    <!-- Protocol Buffers -->
    <dependency>
        <groupId>com.google.protobuf</groupId>
        <artifactId>protobuf-java</artifactId>
        <version>${protobuf.version}</version>
    </dependency>

    <!-- Required for generated code annotations -->
    <dependency>
        <groupId>javax.annotation</groupId>
        <artifactId>javax.annotation-api</artifactId>
        <version>1.3.2</version>
    </dependency>
</dependencies>
```

### Step 2: Add Maven Plugins for Proto Compilation

Add these to `<build>` section:

```xml
<build>
    <extensions>
        <!-- OS Maven Plugin - Detects OS for protobuf compiler -->
        <extension>
            <groupId>kr.motd.maven</groupId>
            <artifactId>os-maven-plugin</artifactId>
            <version>1.7.1</version>
        </extension>
    </extensions>

    <plugins>
        <!-- Protobuf Maven Plugin - Generates Java classes from .proto files -->
        <plugin>
            <groupId>org.xolstice.maven.plugins</groupId>
            <artifactId>protobuf-maven-plugin</artifactId>
            <version>0.6.1</version>
            <configuration>
                <protocArtifact>com.google.protobuf:protoc:${protobuf.version}:exe:${os.detected.classifier}</protocArtifact>
                <pluginId>grpc-java</pluginId>
                <pluginArtifact>io.grpc:protoc-gen-grpc-java:${grpc.version}:exe:${os.detected.classifier}</pluginArtifact>
                <protoSourceRoot>${project.basedir}/src/main/proto</protoSourceRoot>
                <!-- Skip cleanup to avoid permission issues in Docker -->
                <clearOutputDirectory>false</clearOutputDirectory>
                <attachProtoSources>false</attachProtoSources>
            </configuration>
            <executions>
                <execution>
                    <goals>
                        <goal>compile</goal>
                        <goal>compile-custom</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

### Step 3: Create Proto File

Create `src/main/proto/inventory.proto`:

```protobuf
syntax = "proto3";

package inventory;

// Java package for generated classes
option java_package = "com.ecomapp.inventory_service.grpc";
option java_outer_classname = "InventoryProto";
option java_multiple_files = true;

// Define your gRPC service
service InventoryService {
  rpc GetUserCart (GetUserCartRequest) returns (GetUserCartResponse);
  rpc ClearUserCart (ClearUserCartRequest) returns (ClearUserCartResponse);
}

// Request/Response Messages
message GetUserCartRequest {
  string userId = 1;
}

message Product {
  string id = 1;
  string categoryId = 2;
  string name = 3;
  string description = 4;
  repeated string imageUrls = 5;  // Arrays use 'repeated' keyword
  bool isActive = 6;
  float mrp = 7;
  float discount = 8;
  float quantity = 9;
  float availableStock = 10;
  string unit = 11;
}

message CartItemResponse {
  string id = 1;
  int32 itemCount = 2;              // Use int32, not int
  Product product = 3;
}

message GetUserCartResponse {
  float totalMrp = 1;
  float totalPrice = 2;
  repeated CartItemResponse items = 3;  // Arrays use 'repeated'
}

message ClearUserCartRequest {
  string userId = 1;
}

message ClearUserCartResponse {
  bool success = 1;
  string message = 2;
}
```

**Proto3 Important Notes:**
- Use `repeated` for arrays, NOT `[]` syntax
- Use `int32`, `int64`, NOT plain `int`
- Field numbers (1, 2, 3...) are important and shouldn't change
- `string`, `bool`, `float`, `double` are supported types

### Step 4: Generate Java Classes from Proto

Run Maven compile to generate gRPC classes:

```bash
./mvnw clean compile
```

This generates classes in:
- `target/generated-sources/protobuf/java/` - Message classes
- `target/generated-sources/protobuf/grpc-java/` - Service stubs

**Generated classes:**
- `InventoryServiceGrpc.java` - Service base class
- `GetUserCartRequest.java`, `GetUserCartResponse.java`
- `ClearUserCartRequest.java`, `ClearUserCartResponse.java`
- `Product.java`, `CartItemResponse.java`

### Step 5: Implement gRPC Service

Create `src/main/java/com/ecomapp/inventory_service/grpc/InventoryGrpcService.java`:

```java
package com.ecomapp.inventory_service.grpc;

import java.util.List;
import java.util.stream.Collectors;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@Slf4j
@GrpcService  // ✅ This annotation registers the service with gRPC server
public class InventoryGrpcService extends InventoryServiceGrpc.InventoryServiceImplBase {
    
    private final CartService cartService;

    public InventoryGrpcService(CartService cartService) {
        this.cartService = cartService;
    }

    @Override
    public void getUserCart(GetUserCartRequest request, 
                          StreamObserver<GetUserCartResponse> responseObserver) {
        try {
            log.info("gRPC getUserCart called for userId: {}", request.getUserId());
            
            // Your business logic
            ApiResponse<Map<String, Object>> cartResponse = 
                cartService.getCart(request.getUserId());

            if (!cartResponse.isSuccess()) {
                // Send error response
                responseObserver.onError(Status.NOT_FOUND
                    .withDescription(cartResponse.getMessage())
                    .asRuntimeException());
                return;
            }

            // Build gRPC response
            GetUserCartResponse response = GetUserCartResponse.newBuilder()
                .setTotalMrp((float) totalMrp)
                .setTotalPrice((float) totalPrice)
                .addAllItems(grpcItems)
                .build();

            // Send successful response
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            log.error("Error getting user cart: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL
                .withDescription("Error: " + e.getMessage())
                .asRuntimeException());
        }
    }

    @Override
    public void clearUserCart(ClearUserCartRequest request, 
                            StreamObserver<ClearUserCartResponse> responseObserver) {
        try {
            ApiResponse<String> result = cartService.clearCart(request.getUserId());

            ClearUserCartResponse response = ClearUserCartResponse.newBuilder()
                .setSuccess(result.isSuccess())
                .setMessage(result.getMessage())
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            log.error("Error clearing cart: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL
                .withDescription("Error: " + e.getMessage())
                .asRuntimeException());
        }
    }
}
```

**Key gRPC Concepts:**
- `@GrpcService` - Registers service with auto-configured gRPC server
- `StreamObserver` - Used to send responses asynchronously
- `responseObserver.onNext()` - Send response data
- `responseObserver.onCompleted()` - Signal successful completion
- `responseObserver.onError()` - Send error with status code
- `Status.NOT_FOUND`, `Status.INTERNAL` - gRPC status codes

### Step 6: Configure gRPC Server Port

Add to `application.properties`:

```properties
# gRPC Server port
grpc.server.port=50051
```

### Step 7: Update Dockerfile for gRPC

Update `Dockerfile.dev`:

```dockerfile
FROM maven:3.9.4-eclipse-temurin-17
WORKDIR /workspace

# Copy Maven files
COPY mvnw ./
COPY .mvn .mvn
COPY pom.xml ./
RUN chmod +x mvnw && ./mvnw -q -DskipTests dependency:go-offline

# Copy proto files
COPY src/main/proto ./src/main/proto

# Copy source code
COPY src ./src

# Expose both HTTP and gRPC ports
EXPOSE 8082 50051

CMD ["./mvnw", "spring-boot:run", "-Dspring-boot.run.fork=false"]
```

### Step 8: Update docker-compose.yml

```yaml
services:
  inventory_service:
    container_name: inventory-service
    build:
      context: .
      dockerfile: Dockerfile.dev
    ports:
      - "8082:8082"      # HTTP REST API
      - "50052:50051"    # gRPC (host:container)
    environment:
      - GRPC_SERVER_PORT=50051  # Container port
    networks:
      - e_com_network
```

### Step 9: VS Code Configuration (Optional)

Create `.vscode/settings.json` to help IDE recognize generated sources:

```json
{
  "java.configuration.updateBuildConfiguration": "automatic",
  "java.project.sourcePaths": [
    "src/main/java",
    "target/generated-sources/protobuf/java",
    "target/generated-sources/protobuf/grpc-java"
  ]
}
```

After adding this, reload VS Code:
- `Cmd+Shift+P` → `Java: Clean Java Language Server Workspace`
- `Cmd+Shift+P` → `Developer: Reload Window`

### Step 10: Build and Run

```bash
# Build and start services
docker-compose -f docker-compose.dev.yml up --build

# Or run locally
./mvnw clean compile
./mvnw spring-boot:run
```

The service now runs:
- **HTTP REST API**: `http://localhost:8082`
- **gRPC Server**: `localhost:50052` (from host) or `inventory-service:50051` (from other containers)

---

## 📡 How to Call gRPC Service from Another Service

### From Order Service (Another Docker Container):

```java
// Add dependency in order-service pom.xml
<dependency>
    <groupId>io.grpc</groupId>
    <artifactId>grpc-netty-shaded</artifactId>
    <version>1.60.0</version>
</dependency>

// Copy generated proto classes or generate them from the same .proto file

// Create gRPC client
@Service
public class InventoryClient {
    
    private final InventoryServiceGrpc.InventoryServiceBlockingStub inventoryStub;

    public InventoryClient() {
        ManagedChannel channel = ManagedChannelBuilder
            .forAddress("inventory-service", 50051)  // Service name + container port
            .usePlaintext()  // No TLS for internal communication
            .build();
        
        this.inventoryStub = InventoryServiceGrpc.newBlockingStub(channel);
    }

    public GetUserCartResponse getUserCart(String userId) {
        GetUserCartRequest request = GetUserCartRequest.newBuilder()
            .setUserId(userId)
            .build();
        
        return inventoryStub.getUserCart(request);
    }

    public ClearUserCartResponse clearUserCart(String userId) {
        ClearUserCartRequest request = ClearUserCartRequest.newBuilder()
            .setUserId(userId)
            .build();
        
        return inventoryStub.clearUserCart(request);
    }
}
```

### Testing gRPC from Local Machine:

Using `grpcurl` tool:

```bash
# Install grpcurl
brew install grpcurl

# List available services
grpcurl -plaintext localhost:50052 list

# Call getUserCart
grpcurl -plaintext -d '{"userId": "user123"}' \
  localhost:50052 inventory.InventoryService/GetUserCart

# Call clearUserCart
grpcurl -plaintext -d '{"userId": "user123"}' \
  localhost:50052 inventory.InventoryService/ClearUserCart
```

---

## 🔍 Troubleshooting gRPC

### Import Errors in IDE
- Run `./mvnw clean compile` to generate classes
- Reload VS Code: `Cmd+Shift+P` → `Java: Clean Java Language Server Workspace`
- Check `.vscode/settings.json` includes generated source paths

### Port Already in Use
```bash
# Find process using port
lsof -ti:50051
# Kill the process
kill -9 <PID>
```

### Docker Permission Issues
If you get "unable to delete directory" errors:
- Add `<clearOutputDirectory>false</clearOutputDirectory>` to protobuf plugin config
- This skips cleanup that causes permission issues in Docker

### Proto Compilation Fails
- Ensure proto file is in `src/main/proto/`
- Check proto syntax (use `repeated` for arrays, `int32` not `int`)
- Verify Maven plugins are in `pom.xml`

### gRPC Server Not Starting
- Check `grpc.server.port` in application.properties
- Ensure port is exposed in Dockerfile
- Check Docker logs: `docker logs inventory-service`

---

## 📚 gRPC Benefits

- **Performance**: Binary protocol (Protocol Buffers) is faster than JSON
- **Type Safety**: Strongly typed contracts between services
- **Bi-directional Streaming**: Supports client, server, and bi-directional streaming
- **Language Agnostic**: Works across different programming languages
- **Code Generation**: Auto-generates client and server code

---

## License

This project is licensed under the MIT License.