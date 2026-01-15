# Smart E-Commerce System

A comprehensive e-commerce application built with JavaFX and PostgreSQL, implementing database fundamentals including CRUD operations, indexing, caching, and performance optimization.

## Table of Contents

1. [Project Overview](#project-overview)
2. [Features](#features)
3. [Technology Stack](#technology-stack)
4. [Prerequisites](#prerequisites)
5. [Database Setup](#database-setup)
6. [Application Setup](#application-setup)
7. [Running the Application](#running-the-application)
8. [Project Structure](#project-structure)
9. [Database Schema](#database-schema)
10. [Key Features Implementation](#key-features-implementation)
11. [Performance Optimizations](#performance-optimizations)
12. [Testing](#testing)
13. [Documentation](#documentation)

---

## Project Overview

This project is a full-stack e-commerce system that demonstrates:
- **Database Design**: Normalized relational database (3NF) with conceptual, logical, and physical models
- **CRUD Operations**: Complete Create, Read, Update, Delete functionality for all entities
- **Data Structures & Algorithms**: In-memory caching, sorting, and searching implementations
- **Performance Optimization**: Database indexing and caching strategies with documented improvements
- **JavaFX Integration**: Modern GUI application with JDBC database connectivity

## Features

### Customer Features
- User registration and authentication
- Product catalog with search, filter, and sort
- Shopping cart management
- Order placement and history
- Product reviews and ratings
- Profile management
- Order cancellation (for pending/processing orders)

### Admin Features
- Product management (CRUD)
- Category management (CRUD)
- Inventory management
- Order management and status updates
- User management
- Review management
- Dashboard with analytics

### Technical Features
- Database indexing for optimized queries
- In-memory caching for improved performance
- Parameterized queries (SQL injection prevention)
- Input validation and error handling
- Responsive UI with modern design
- Full-screen and resizable windows

## Technology Stack

- **Frontend**: JavaFX 21
- **Backend**: Java 17
- **Database**: PostgreSQL 14+
- **Build Tool**: Maven
- **Password Hashing**: BCrypt
- **Architecture**: MVC pattern (Controller → DAO → Database)

## Prerequisites

Before running this application, ensure you have:

1. **Java Development Kit (JDK) 17 or higher**
   - Download from: https://adoptium.net/
   - Verify installation: `java -version`

2. **Maven 3.6+**
   - Download from: https://maven.apache.org/download.cgi
   - Verify installation: `mvn -version`

3. **PostgreSQL 14 or higher**
   - Download from: https://www.postgresql.org/download/
   - Verify installation: `psql --version`

4. **IDE (Optional but recommended)**
   - IntelliJ IDEA, Eclipse, or VS Code with Java extensions

## Database Setup

### Step 1: Create PostgreSQL Database

1. Open PostgreSQL command line or pgAdmin
2. Create a new database:
```sql
CREATE DATABASE smart_ecommerce;
```

3. Connect to the database:
```sql
\c smart_ecommerce
```

### Step 2: Run Schema Script

1. Navigate to the `SQL-queries` directory
2. Execute the schema script:
```bash
psql -U your_username -d smart_ecommerce -f SQL-queries/smart_ecommerce_schema.sql
```

Or using pgAdmin:
- Open pgAdmin
- Connect to your PostgreSQL server
- Right-click on `smart_ecommerce` database → Query Tool
- Open `SQL-queries/smart_ecommerce_schema.sql`
- Execute the script (F5)

### Step 3: Load Sample Data

1. Execute the sample data script:
```bash
psql -U your_username -d smart_ecommerce -f SQL-queries/sample_data.sql
```

Or using pgAdmin Query Tool:
- Open `SQL-queries/sample_data.sql`
- Execute the script

**Note**: The sample data includes:
- 6 categories
- 5 users (1 admin, 4 customers)
- 30 products across all categories
- 8 sample orders
- 10 product reviews

**Default Admin Credentials** (from sample data):
- Username: `admin`
- Password: `password` (hashed with BCrypt in sample data)

### Step 4: Verify Database Setup

Run these queries to verify data was loaded correctly:

```sql
SELECT COUNT(*) FROM Category;      -- Should return 6
SELECT COUNT(*) FROM "User";        -- Should return 5
SELECT COUNT(*) FROM Product;      -- Should return 30
SELECT COUNT(*) FROM Inventory;     -- Should return 30
SELECT COUNT(*) FROM "Order";      -- Should return 8
SELECT COUNT(*) FROM OrderItem;     -- Should return 10
SELECT COUNT(*) FROM Review;        -- Should return 10
```

## Application Setup

### Step 1: Clone or Download the Project

```bash
git clone https://github.com/dusengepeggy/Lab4-Smart_E-commerce.git
cd Lab4-Smart_E-commerce
```

### Step 2: Configure Database Connection

1. Create a `.env` file in the project root directory:
```bash
touch .env
```

2. Add your database configuration to `.env`:
```env
DB_URL=jdbc:postgresql://localhost:5432/smart_ecommerce
DB_USER=your_postgresql_username
DB_PASSWORD=your_postgresql_password
```

**Example**:
```env
DB_URL=jdbc:postgresql://localhost:5432/smart_ecommerce
DB_USER=postgres
DB_PASSWORD=mypassword
```

**Important**: 
- Do NOT commit the `.env` file to version control
- The `.env` file is already in `.gitignore`

### Step 3: Install Dependencies

Maven will automatically download dependencies when you build the project:

```bash
mvn clean install
```

This will:
- Download all required dependencies (JavaFX, PostgreSQL driver, etc.)
- Compile the Java source code
- Run tests (if any)

## Running the Application

### Method 1: Using Maven (Recommended)

```bash
mvn javafx:run
```

### Method 2: Using IDE

1. **IntelliJ IDEA**:
   - Open the project
   - Navigate to `src/main/java/com/example/demo/HelloApplication.java`
   - Right-click → Run 'HelloApplication.main()'

2. **Eclipse**:
   - Import as Maven project
   - Right-click on `HelloApplication.java` → Run As → Java Application

3. **VS Code**:
   - Install Java Extension Pack
   - Open `HelloApplication.java`
   - Click "Run" button or press F5

### Method 3: Build and Run JAR

```bash
# Build the project
mvn clean package

# Run the JAR (adjust path as needed)
java --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml -jar target/Smart-E-commerce-1.0-SNAPSHOT.jar
```

## Project Structure

```
Lab4-Smart_E-commerce/
├── ERD_diagrams/              # Database design documentation
│   ├── README.MD              # ERD documentation
│   ├── E-commerce-Conceptual_ERD.png
│   ├── E-commerce-Logical_ERD.png
│   └── E-commerce-Physical_ERD.png
├── SQL-queries/               # Database scripts
│   ├── smart_ecommerce_schema.sql  # Schema creation
│   └── sample_data.sql        # Sample data insertion
├── src/
│   └── main/
│       ├── java/
│       │   └── com/example/demo/
│       │       ├── controllers/    # JavaFX controllers (MVC)
│       │       ├── dao/            # Data Access Objects
│       │       ├── model/          # Entity models
│       │       ├── dbConnection/   # Database connection
│       │       ├── utils/          # Utility classes
│       │       └── HelloApplication.java  # Main entry point
│       └── resources/
│           └── com/example/demo/
│               ├── views/          # FXML UI files
│               ├── components/     # Reusable UI components
│               └── styles/         # CSS stylesheets
├── target/                    # Compiled classes (generated)
├── pom.xml                    # Maven configuration
├── README.md                  # This file
├── PERFORMANCE_REPORT.md      # Performance optimization report
└── .env                       # Database configuration (create this)
```

## Database Schema

### Entities

1. **User**: System users (Admin and Customer roles)
2. **Category**: Product categories
3. **Product**: Items available for purchase
4. **Inventory**: Stock management for products
5. **Order**: Customer purchase transactions
6. **OrderItem**: Individual items within orders
7. **Review**: Customer product reviews and ratings

### Relationships

- User → Order (1:N)
- User → Review (1:N)
- Category → Product (1:N)
- Product → Inventory (1:1)
- Product → OrderItem (1:N)
- Product → Review (1:N)
- Order → OrderItem (1:N)

### Indexes

The database includes indexes on frequently queried columns:
- `idx_product_name` on `Product(name)`
- `idx_product_category` on `Product(category_id)`
- `idx_order_user` on `Order(user_id)`
- `idx_inventory_quantity` on `Inventory(stock_quantity)`

See `ERD_diagrams/README.MD` for detailed database design documentation.

## Key Features Implementation

### CRUD Operations

All CRUD operations are implemented via JavaFX interface:

- **Product Management**: Create, read, update, delete products
- **Category Management**: Create, read, update, delete categories
- **Inventory Management**: Update stock quantities and locations
- **Order Management**: View and update order statuses
- **User Management**: View and manage user accounts
- **Review Management**: View and moderate product reviews

### Searching and Filtering

- **Product Search**: Case-insensitive search by product name
- **Category Filter**: Filter products by category
- **Price Range Filter**: Filter products by price range
- **Sorting**: Sort by name (A-Z, Z-A) or price (Low-High, High-Low)

### Data Structures & Algorithms

#### Caching
- **Product Catalog Cache**: Products loaded into memory for fast filtering/sorting
- **Cart Session Cache**: Shopping cart stored in HashMap for O(1) lookups
- **Category Cache**: Categories cached in memory

#### Sorting
- **Algorithm**: Java's TimSort (hybrid merge/insertion sort)
- **Complexity**: O(n log n) average case
- **Implementation**: `ProductCatalogController.sortProducts()`

#### Searching
- **Database Level**: B-tree indexes for O(log n) searches
- **In-Memory**: Linear search through cached lists for O(n) filtering
- **Hybrid Approach**: Use indexes for initial load, cache for subsequent operations

See `PERFORMANCE_REPORT.md` for detailed algorithm analysis and performance metrics.

## Performance Optimizations

### Database Indexing
- Indexes created on frequently queried columns
- Average query performance improvement: 65-75%
- See `PERFORMANCE_REPORT.md` for detailed metrics

### In-Memory Caching
- Product catalog cached in memory
- Shopping cart cached in HashMap
- Average cache hit performance: 80-97% faster

### Performance Report
See `PERFORMANCE_REPORT.md` for:
- Before/after optimization metrics
- Query execution time comparisons
- Algorithm complexity analysis
- Scalability considerations

## Testing

### Manual Testing

1. **Authentication**:
   - Login as admin (username: `admin`, password: `password`)
   - Login as customer (username: `john_doe`, password: `password`)

2. **Product Operations**:
   - Browse products in catalog
   - Search for products by name
   - Filter by category and price
   - Sort products

3. **Shopping Cart**:
   - Add products to cart
   - Update quantities
   - Remove items
   - Proceed to checkout

4. **Order Management**:
   - Place orders
   - View order history
   - Cancel pending orders (customer)
   - Update order status (admin)

5. **Admin Operations**:
   - Create/update/delete products
   - Manage categories
   - Update inventory
   - View dashboard analytics

### Database Query Testing

Test query performance using PostgreSQL's `EXPLAIN ANALYZE`:

```sql
-- Test product name search
EXPLAIN ANALYZE SELECT * FROM Product WHERE LOWER(name) LIKE LOWER('%laptop%');

-- Test category filter
EXPLAIN ANALYZE SELECT * FROM Product WHERE category_id = 1;

-- Test user orders
EXPLAIN ANALYZE SELECT * FROM "Order" WHERE user_id = 2;
```

## Documentation

### Database Documentation
- **ERD Documentation**: `ERD_diagrams/README.MD`
  - Conceptual model
  - Logical model
  - Physical model
  - Normalization analysis
  - Design justifications

### Performance Documentation
- **Performance Report**: `PERFORMANCE_REPORT.md`
  - Optimization methodology
  - Before/after metrics
  - Algorithm analysis
  - Scalability considerations

### Code Documentation
- JavaDoc comments in DAO classes
- Inline comments explaining complex logic
- Algorithm documentation in performance report

## Troubleshooting

### Common Issues

1. **Database Connection Error**
   - Verify PostgreSQL is running: `pg_isready`
   - Check `.env` file configuration
   - Verify database name, username, and password

2. **JavaFX Module Error**
   - Ensure JavaFX dependencies are downloaded: `mvn clean install`
   - Check Java version compatibility (requires Java 17+)

3. **Port Already in Use**
   - Change PostgreSQL port in `.env` if default port (5432) is in use

4. **Missing Dependencies**
   - Run `mvn clean install` to download all dependencies
   - Check internet connection for Maven repository access

5. **Application Won't Start**
   - Check console for error messages
   - Verify database is accessible
   - Ensure all SQL scripts have been executed

## Future Enhancements

Potential improvements for future iterations:

1. **NoSQL Integration**: Implement NoSQL storage for reviews/logs
2. **Advanced Caching**: Redis integration for distributed caching
3. **Pagination**: Implement pagination for large product catalogs
4. **Search Enhancement**: Full-text search capabilities
5. **Performance Monitoring**: Real-time performance metrics dashboard
6. **Unit Tests**: Comprehensive unit test coverage
7. **API Layer**: RESTful API for mobile/web clients

## Contributing

This is an academic project. For questions or issues, please contact the project maintainer.

## License

[Specify license if applicable]

## Acknowledgments

- JavaFX community
- PostgreSQL documentation
- Maven project management

---

## Quick Start Checklist

- [ ] Install Java 17+
- [ ] Install Maven 3.6+
- [ ] Install PostgreSQL 14+
- [ ] Create database: `smart_ecommerce`
- [ ] Run `smart_ecommerce_schema.sql`
- [ ] Run `sample_data.sql`
- [ ] Create `.env` file with database credentials
- [ ] Run `mvn clean install`
- [ ] Run `mvn javafx:run`
- [ ] Login with admin credentials

---

**For detailed database design information, see**: `ERD_diagrams/README.MD`  
**For performance optimization details, see**: `PERFORMANCE_REPORT.md`
