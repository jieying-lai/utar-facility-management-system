<div align="center">

  # 🏫 UTAR Smart Campus Facility Booking & Maintenance Management System

  **A modular, desktop-grade facility reservation, maintenance tracking, and usage analytics system built with Pure Java.**

  [![Java](https://img.shields.io/badge/Java-8%2B%20%7C%2025%20LTS-orange?style=flat-square&logo=openjdk&logoColor=white)](#)
  [![Paradigm](https://img.shields.io/badge/Paradigm-Object--Oriented%20Programming-blue?style=flat-square)](#)
  [![Storage](https://img.shields.io/badge/Persistence-File%20I%2FO%20(.txt)-lightgrey?style=flat-square)](#)
  [![Coursework](https://img.shields.io/badge/UTAR-UECS1144%20OOAD-blueviolet?style=flat-square)](#)

</div>

---

## 📌 Project Overview

At university campuses like **Universiti Tunku Abdul Rahman (UTAR)**, managing high-demand shared resources (lecture halls, discussion rooms, sports courts, computer labs, and multipurpose halls) requires coordination to avoid double-booking and untracked facility damage.

Developed for the **UECS1144 Object-Oriented Application Development** coursework, this system provides a unified solution for:
1. **Facility Booking & Cancellation**: Self-service scheduling, modification, and automated reminder alerts for students and staff.
2. **Facility Maintenance & Tracking**: Incident reporting, task delegation, and maintenance status tracking.
3. **Administrative Analytics**: Automated report generation covering peak booking hours, utilization rates, and incident frequency.

The entire application is constructed in **Pure Java** with zero heavy framework dependencies, focusing on clean OOP architecture, robust exception handling, and reliable text-file persistence.

---

## 🏗️ System Architecture & OOP Principles

The application is designed around core Object-Oriented Design methodologies, with responsibilities cleanly decoupled:

* **Encapsulation**: Strict information hiding using `private` data members, exposing access solely through verified accessors, mutators, and domain methods.
* **Inheritance**: Base entities (`User`, `Facility`) extended by concrete types (e.g., `Student`, `Staff`, `Admin`, `SportCourt`, `ComputerLab`) to eliminate redundant logic.
* **Polymorphism**: Dynamic method dispatch for role-specific operations (e.g., permission checks, custom rate/duration calculations).
* **Abstraction**: Abstract classes and interfaces establishing contracts across storage, booking verification, and notification services.
* **Defensive Programming**: Comprehensive validation pipelines guarding against invalid date/time ranges, file corruption, and malformed terminal inputs.

---

## ✨ Key Features & Functional Modules

### 1. 📅 Facility Booking & Cancellation Module
* **Account Access**: Registration and role-based login for UTAR students and staff.
* **Smart Search**: Filter facilities dynamically by date, time slot, capacity, and facility type.
* **Reservation Lifecycle**: Request booking, review approval states (`Pending`, `Approved`, `Rejected`), or cancel existing slots per cancellation rules.
* **Automated Reminders**: Built-in notification mechanisms alerting users prior to their scheduled time slots.

### 2. 🛠️ Maintenance Reporting & Workflow
* **Issue Reporting**: Users log facility defects (e.g., damaged projectors, air-conditioning failure, broken seats).
* **Task Assignment**: Administrators assign maintenance tickets to designated personnel and update progress (`Reported` ➔ `In Progress` ➔ `Resolved`).
* **Conflict Prevention**: Facilities flagged under maintenance are locked to prevent scheduling collisions.

### 3. 📊 Analytics & Reporting Engine
* **Utilization Analysis**: Computes facility utilization rates, identifying peak booking hours and high-demand facilities.
* **Maintenance Metrics**: Analyzes average repair turnaround times and flags repeat-failure facilities.
* **Audit History**: Personal usage logs for users and periodic (monthly/semester) activity summaries for management.

---
