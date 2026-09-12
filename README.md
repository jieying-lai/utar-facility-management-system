# 🏫 UTAR Campus Facility Management System

> An Object-Oriented Programming (OOP) coursework project developed in Pure Java, simulating a real-world campus facility reservation and administration system.

---

## 📌 Project Overview
This project was developed as part of the **Object-Oriented Programming** course at **Universiti Tunku Abdul Rahman (UTAR)**. 

The goal was to design and implement a maintainable, modular, and robust system using **Pure Java** to handle university facility scheduling, user role management, and booking conflicts—strictly adhering to object-oriented design principles.

---

## ✨ Key Features
- **Role-Based Operations**:
  - **Administrator**: Manage facility listings (Add / Edit / Remove) and review/approve student booking requests.
  - **Student / Staff**: Browse available facilities, check schedules, book slots, and track reservation statuses.
- **Booking & Collision Logic**: Basic scheduling logic to ensure facilities are not double-booked for the same time slot.
- **Data Persistence**: Stores and retrieves facility records and transaction histories using Java File I/O.
- **Input Validation & Exception Handling**: Defensive programming practices to handle edge cases and invalid terminal inputs gracefully.

---

## 🏗️ OOP Principles Demonstrated
- **Encapsulation**: Strict use of access specifiers (`private`), shielding data representation and exposing well-defined public accessors/mutators.
- **Inheritance**: Defined base classes (e.g., `User`, `Facility`) extended by specialized subclasses to maximize code reusability.
- **Polymorphism**: Dynamic method overriding for role-specific behaviors and operations.
- **Abstraction**: Abstract classes / interfaces defining consistent contracts across system components.
- **Java Collections**: Practical usage of `ArrayList` / `List` structures for efficient in-memory data processing.

---

## 🛠️ Tech & Tools
- **Language**: Pure Java (JDK 8+)
- **Storage**: Java File I/O (`.txt`)
- **Paradigm**: Object-Oriented Programming (OOP)
- **Environment**: Eclipse IDE

---
