# Fuel Booking System - Spring Boot Backend

## Overview
The **Booking Management System** is a robust and scalable Spring Boot-based backend designed for efficient management of cylinder bookings, supplier selection, billing, and user authentication. This API-first system is built to support high-traffic operations with secure **authentication**, well-defined **role-based access control**, and automated **email notifications**, including **PDF invoice sending** for bookings.

## Features
- **User Authentication & Authorization** (Spring Security, Role-based Access Control)
- **Comprehensive Supplier Management** (CRUD, Stock Management, Status Updates)
- **Cylinder Inventory Management** (Tracking Booked/Available Cylinders)
- **Booking System with Flexible Scheduling** (Time Slots, Delivery Options, Payment Modes)
- **Billing System with Automated Invoicing** (Including CLECharge for exceeding booking limits)
- **Supplier Selection Strategies** (Most Stock, Least Stock, Round Robin - Strategy Pattern)
- **RESTful API with Well-Structured Endpoints** (For seamless frontend integration)
- **Automated Email Notifications** (With PDF Invoice Attachments)

## Project Structure

```
Backend/
├── src/main/java/com/faos/Booking
│   ├── BookingApplication.java          # Main application entry point
│   ├── security/                        # Spring Security configurations
│   ├── strategies/                      # Supplier selection strategies
│   ├── repositories/                    # JPA repositories for database access
│   ├── models/                          # Data models and entities
│   ├── controllers/                      # REST API controllers
│   ├── services/                        # Business logic and service layer
│   ├── enums/                           # Enum definitions
│
├── src/main/resources
│   ├── application.properties           # Spring Boot configurations
│
├── pom.xml                              # Maven dependencies
└── README.md                            # Project documentation
```

## Technologies Used

- **Spring Boot 3.4.2**
- **Java 21**
- **Spring Data JPA** for database interactions
- **Spring Security** for authentication and authorization
- **MySQL Database**
- **Lombok** for boilerplate reduction
- **Thymeleaf** for templating (if required)
- **Spring Boot Mail** for email functionalities
- **Swagger (Springdoc OpenAPI)** for API documentation
- **iText** for PDF generation

## Installation & Setup
### Prerequisites:
1. **Java 21** installed
2. **Maven** installed
3. **MySQL Database** configured

### Steps to Run:
1. Clone the repository:
   ```sh
   git clone https://github.com/KAMRANKHANALWI/Fuel-Agency-SpringBoard.git
   ```
2. Configure **database credentials** in `application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/your_db
   spring.datasource.username=root
   spring.datasource.password=your_password
   ```
3. Build and run the application:
   ```sh
   mvn clean install
   mvn spring-boot:run
   ```

## Key Concepts

### 1. Security & Authentication
The project employs **Spring Security** to enforce authentication and **Role-based Access Control (RBAC)**.

Roles:
- **ADMIN**: Can manage bookings, suppliers, and billing.
- **CUSTOMER**: Can create and manage their bookings.

### 2. Booking System & Business Rules
Customers can book cylinders, but a rule limits them to **6 bookings per year**. Beyond this limit, an additional **Cylinder Limit Exceed Charge (CLECharge)** of **20%** is applied.

### 3. Supplier Selection Strategies (Strategy Pattern)
- **Most Stock Strategy**: Chooses the supplier with the highest stock.
- **Least Stock Strategy**: Chooses the supplier with the lowest stock.
- **Round Robin Strategy**: Distributes bookings evenly among suppliers.

## API Endpoints

### Supplier Controller
| Method | Endpoint | Description |
|--------|------------------------|-------------|
| GET | `/api/suppliers/{id}` | Get supplier details |
| PUT | `/api/suppliers/{id}` | Update supplier details |
| DELETE | `/api/suppliers/{id}` | Delete a supplier |
| GET | `/api/suppliers` | List all suppliers |
| POST | `/api/suppliers` | Add a new supplier |
| POST | `/api/suppliers/{supplierId}/add-cylinders/{count}` | Add cylinders to a supplier |
| GET | `/api/suppliers/{supplierId}/stock` | Get stock of a supplier |
| GET | `/api/suppliers/total-stock` | Get total stock available |
| GET | `/api/suppliers/top-suppliers` | Get top suppliers |
| GET | `/api/suppliers/inactive` | Get inactive suppliers |
| GET | `/api/suppliers/active` | Get active suppliers |

### Cylinder Controller
| Method | Endpoint | Description |
|--------|------------------------|-------------|
| GET | `/api/cylinders/{id}` | Get cylinder details |
| PUT | `/api/cylinders/{id}` | Update cylinder details |
| DELETE | `/api/cylinders/{id}` | Delete a cylinder |
| POST | `/api/cylinders/add/{supplierId}` | Add a cylinder to a supplier |
| GET | `/api/cylinders` | Get all cylinders |
| GET | `/api/cylinders/booked` | Get booked cylinders |
| GET | `/api/cylinders/available` | Get available cylinders |

### Customer Controller
| Method | Endpoint | Description |
|--------|------------------------|-------------|
| GET | `/api/customers/{id}` | Get customer details |
| PUT | `/api/customers/{id}` | Update customer details |
| DELETE | `/api/customers/{id}` | Delete a customer |
| POST | `/api/customers/register` | Register a new customer |
| GET | `/api/customers` | List all customers |
| GET | `/api/customers/me` | Get logged-in customer details |

### Booking Controller
| Method | Endpoint | Description |
|--------|------------------------|-------------|
| GET | `/api/bookings/{id}` | Get booking details |
| PUT | `/api/bookings/{id}` | Update booking details |
| DELETE | `/api/bookings/{id}` | Delete a booking |
| PUT | `/api/bookings/{id}/cancel` | Cancel a booking |
| POST | `/api/bookings/customer/booking` | Create a new booking |
| GET | `/api/bookings` | Get all bookings |
| GET | `/api/bookings/mybookings` | Get user’s bookings |
| GET | `/api/bookings/cancelled` | Get cancelled bookings |

### Bill Controller
| Method | Endpoint | Description |
|--------|------------------------|-------------|
| GET | `/api/bills/{id}` | Get bill details |
| PUT | `/api/bills/{id}` | Update bill details |
| DELETE | `/api/bills/{id}` | Delete a bill |
| POST | `/api/bills/{billId}/send-email` | Send bill via email |
| GET | `/api/bills` | Get all bills |

### Authentication Controller
| Method | Endpoint | Description |
|--------|------------------------|-------------|
| POST | `/auth/register` | Register a new user |
| POST | `/auth/login` | User login |
| GET | `/auth/whoami` | Get current user role |
| GET | `/auth/me` | Get logged-in user details |


---
### Contributors
Maintained by **Kamran** and team. Contributions are welcome!

For issues and improvements, feel free to open a pull request.

📧 Contact: [khankamran@gmail.com](mailto:khankamran@gmail.com)

