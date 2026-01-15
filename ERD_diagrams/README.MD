# Database Design Documentation

## Overview
This document describes the database design for the Smart E-Commerce System, including conceptual, logical, and physical models. The database is normalized to Third Normal Form (3NF) and designed to support efficient e-commerce operations.

## 1. Conceptual Model

### Purpose
The conceptual model represents the high-level business entities and their relationships without implementation details. It focuses on what data the system needs to store and how entities relate to each other.

### Major Entities

1. **User** - Represents both administrators and customers of the e-commerce platform
2. **Category** - Product categorization for better organization and navigation
3. **Product** - Items available for purchase in the e-commerce system
4. **Inventory** - Stock management for products
5. **Order** - Customer purchase transactions
6. **OrderItem** - Individual items within an order
7. **Review** - Customer feedback and ratings for products

### Relationships

- **User → Order**: One-to-Many (A user can place multiple orders)
- **User → Review**: One-to-Many (A user can write multiple reviews)
- **Category → Product**: One-to-Many (A category contains multiple products)
- **Product → Inventory**: One-to-One (Each product has one inventory record)
- **Product → OrderItem**: One-to-Many (A product can appear in multiple order items)
- **Product → Review**: One-to-Many (A product can have multiple reviews)
- **Order → OrderItem**: One-to-Many (An order contains multiple order items)

### ERD Diagrams
- **Conceptual ERD**: `E-commerce-Conceptual_ERD.png` - Shows entities and relationships at a high level
- **Logical ERD**: `E-commerce-Logical_ERD.png` - Shows entities with attributes and relationship cardinalities
- **Physical ERD**: `E-commerce-Physical_ERD.png` - Shows the actual database schema with data types and constraints

## 2. Logical Model

### Entity Definitions with Attributes

#### User Entity
- **Primary Key**: `user_id` (SERIAL)
- **Attributes**:
  - `username` (VARCHAR(50), UNIQUE, NOT NULL) - User's login identifier
  - `password` (VARCHAR(255), NOT NULL) - Encrypted password
  - `role` (ENUM: 'Admin', 'Customer', NOT NULL) - User's system role
  - `email` (VARCHAR(100), UNIQUE, NOT NULL) - User's email address
  - `created_at` (TIMESTAMP) - Account creation timestamp

#### Category Entity
- **Primary Key**: `category_id` (SERIAL)
- **Attributes**:
  - `category_name` (VARCHAR(100), UNIQUE, NOT NULL) - Category name
  - `description` (TEXT) - Category description

#### Product Entity
- **Primary Key**: `product_id` (SERIAL)
- **Foreign Key**: `category_id` → Category(category_id)
- **Attributes**:
  - `name` (VARCHAR(255), NOT NULL) - Product name
  - `description` (TEXT) - Product description
  - `price` (DECIMAL(10,2), NOT NULL, CHECK >= 0) - Product price
  - `created_at` (DATE) - Product creation date

#### Inventory Entity
- **Primary Key**: `inventory_id` (SERIAL)
- **Foreign Key**: `product_id` → Product(product_id), UNIQUE
- **Attributes**:
  - `stock_quantity` (INT, NOT NULL, DEFAULT 0, CHECK >= 0) - Available stock
  - `warehouse_location` (VARCHAR(255)) - Storage location
  - `updated_at` (TIMESTAMP) - Last update timestamp

#### Order Entity
- **Primary Key**: `order_id` (SERIAL)
- **Foreign Key**: `user_id` → User(user_id)
- **Attributes**:
  - `order_date` (DATE, DEFAULT CURRENT_DATE) - Order placement date
  - `total_amount` (DECIMAL(10,2), NOT NULL, DEFAULT 0.00) - Total order value
  - `status` (ENUM: 'Pending', 'Shipped', 'Delivered', 'Cancelled', DEFAULT 'Pending') - Order status

#### OrderItem Entity
- **Primary Key**: `order_item_id` (SERIAL)
- **Foreign Keys**: 
  - `order_id` → Order(order_id)
  - `product_id` → Product(product_id)
- **Attributes**:
  - `quantity` (INT, NOT NULL, CHECK > 0) - Quantity ordered
  - `unit_price` (DECIMAL(10,2), NOT NULL) - Price at time of order

#### Review Entity
- **Primary Key**: `review_id` (SERIAL)
- **Foreign Keys**:
  - `user_id` → User(user_id)
  - `product_id` → Product(product_id)
- **Attributes**:
  - `rating` (INT, CHECK BETWEEN 1 AND 5) - Product rating (1-5 stars)
  - `comment` (TEXT) - Review text
  - `review_date` (DATE, DEFAULT CURRENT_DATE) - Review submission date

### Relationship Cardinalities

| Relationship | Type | Cardinality |
|-------------|------|-------------|
| User → Order | One-to-Many | 1:N |
| User → Review | One-to-Many | 1:N |
| Category → Product | One-to-Many | 1:N |
| Product → Inventory | One-to-One | 1:1 |
| Product → OrderItem | One-to-Many | 1:N |
| Product → Review | One-to-Many | 1:N |
| Order → OrderItem | One-to-Many | 1:N |

## 3. Physical Model

### Database System
- **RDBMS**: PostgreSQL
- **Normalization Level**: Third Normal Form (3NF)

### Normalization Analysis

#### First Normal Form (1NF)
- All attributes contain atomic values
- No repeating groups
- Each row is unique

#### Second Normal Form (2NF)
- All non-key attributes are fully functionally dependent on the primary key
- OrderItem: `quantity` and `unit_price` depend on the composite key (order_id, product_id), but since we use `order_item_id` as PK, this is satisfied

#### Third Normal Form (3NF)
- No transitive dependencies
- All non-key attributes depend only on the primary key
- Example: In OrderItem, `unit_price` is stored at order time (not derived from Product) to maintain historical accuracy

### Constraints

#### Primary Keys
- All tables have SERIAL primary keys for auto-incrementing IDs

#### Foreign Keys
- `Product.category_id` → `Category.category_id` (ON DELETE CASCADE)
- `Inventory.product_id` → `Product.product_id` (ON DELETE CASCADE)
- `Order.user_id` → `User.user_id` (ON DELETE RESTRICT)
- `OrderItem.order_id` → `Order.order_id` (ON DELETE CASCADE)
- `OrderItem.product_id` → `Product.product_id` (ON DELETE RESTRICT)
- `Review.user_id` → `User.user_id` (ON DELETE CASCADE)
- `Review.product_id` → `Product.product_id` (ON DELETE CASCADE)

#### Check Constraints
- `Product.price >= 0` - Ensures non-negative pricing
- `Inventory.stock_quantity >= 0` - Prevents negative inventory
- `OrderItem.quantity > 0` - Ensures positive order quantities
- `Review.rating BETWEEN 1 AND 5` - Validates rating range

#### Unique Constraints
- `User.username` - Unique usernames
- `User.email` - Unique email addresses
- `Category.category_name` - Unique category names
- `Inventory.product_id` - One inventory record per product

### Indexes

Indexes are created on frequently queried columns to optimize search and join operations:

1. **idx_product_name** on `Product(name)`
   - Purpose: Optimize product name searches
   - Usage: Product search by name, sorting by name

2. **idx_product_category** on `Product(category_id)`
   - Purpose: Optimize category-based product filtering
   - Usage: Filtering products by category, joins with Category table

3. **idx_order_user** on `Order(user_id)`
   - Purpose: Optimize user order history queries
   - Usage: Retrieving all orders for a specific user

4. **idx_inventory_quantity** on `Inventory(stock_quantity)`
   - Purpose: Optimize low stock queries
   - Usage: Finding products with low inventory levels

### Data Types

- **SERIAL**: Auto-incrementing integers for primary keys
- **VARCHAR(n)**: Variable-length strings with maximum length
- **TEXT**: Unlimited length text fields
- **DECIMAL(10,2)**: Fixed-point decimal for monetary values (precision: 10, scale: 2)
- **INT**: Integer values
- **DATE**: Date values (without time)
- **TIMESTAMP**: Date and time values
- **ENUM**: User-defined enumerated types for role and order status

### Referential Integrity Rules

- **CASCADE**: When a parent record is deleted, child records are automatically deleted
  - Product deletion → Inventory deletion
  - Product deletion → Review deletion
  - Category deletion → Product deletion
  - Order deletion → OrderItem deletion
  - User deletion → Review deletion

- **RESTRICT**: Prevents deletion of parent records if child records exist
  - Cannot delete User if they have Orders
  - Cannot delete Product if it's referenced in OrderItem

## 4. Design Justifications

### Why 3NF?
- Eliminates data redundancy (e.g., product details stored once, not in every order)
- Prevents update anomalies (e.g., changing product price doesn't affect historical orders)
- Maintains data integrity while allowing efficient queries

### Why These Indexes?
- **Product name index**: Most common search operation
- **Category index**: Frequent filtering operation
- **User order index**: Essential for order history display
- **Inventory quantity index**: Critical for stock management queries

### Why These Data Types?
- **DECIMAL for prices**: Prevents floating-point rounding errors in financial calculations
- **ENUM for roles/status**: Ensures data consistency and prevents invalid values
- **TIMESTAMP for audit fields**: Provides precise tracking of when records are created/updated

### Why These Relationships?
- **One-to-One (Product-Inventory)**: Each product has exactly one inventory record, simplifying stock management
- **One-to-Many (User-Order)**: Reflects real-world business logic where users place multiple orders
- **Many-to-Many via OrderItem**: Allows multiple products per order and multiple orders per product

## 5. Scalability Considerations

- **SERIAL primary keys**: Allow for millions of records
- **Indexes**: Maintain query performance as data grows
- **Normalized structure**: Reduces storage requirements and update complexity
- **Foreign key constraints**: Ensure data integrity at scale

## 6. Security Considerations

- **Password storage**: VARCHAR(255) allows for hashed passwords (e.g., bcrypt)
- **Role-based access**: ENUM ensures only valid roles are assigned
- **Referential integrity**: Prevents orphaned records and data corruption
- **Check constraints**: Prevent invalid data entry at the database level
