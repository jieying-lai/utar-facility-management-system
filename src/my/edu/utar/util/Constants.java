package my.edu.utar.util;

/**
 * Constants.java
 * Stores all shared constant data used across the system.
 * All members should use these constants instead of hardcoding strings.
 */
public class Constants {

    // ===================== FILE PATHS =====================
    public static final String FILE_USERS        = "data/users.txt";
    public static final String FILE_ADMIN        = "data/admin.txt";
    public static final String FILE_FACILITIES   = "data/facilities.txt";
    public static final String FILE_BOOKINGS     = "data/bookings.txt";
    public static final String FILE_MAINTENANCE  = "data/maintenance.txt";

    // ===================== DELIMITERS =====================
    public static final String DELIMITER = "|";
    public static final String DELIMITER_REGEX = "\\|";

    // ===================== USER ROLES =====================
    public static final String ROLE_STUDENT = "Student";
    public static final String ROLE_STAFF   = "Staff";
    public static final String ROLE_ADMIN   = "Admin";

    // ===================== BOOKING STATUS =====================
    public static final String STATUS_PENDING   = "Pending";
    public static final String STATUS_APPROVED  = "Approved";
    public static final String STATUS_REJECTED  = "Rejected";
    public static final String STATUS_CANCELLED = "Cancelled";

    // ===================== FACILITY STATUS =====================
    public static final String FACILITY_AVAILABLE   = "Available";
    public static final String FACILITY_UNAVAILABLE = "Unavailable";
    public static final String FACILITY_MAINTENANCE = "Under Maintenance";

    // ===================== FACILITY TYPES =====================
    public static final String[] FACILITY_TYPES = {
        "Lecture Hall",
        "Discussion Room",
        "Sports Court",
        "Computer Lab",
        "Multipurpose Hall"
    };

    // ===================== TIME SLOTS =====================
    // Code 1-5 maps to these slots
    public static final String[] TIME_SLOTS = {
        "",             // index 0 unused
        "8am - 10am",   // index 1
        "10am - 12pm",  // index 2
        "12pm - 2pm",   // index 3
        "2pm - 4pm",    // index 4
        "4pm - 6pm"     // index 5
    };
    public static final int[] TIME_SLOT_START_HOUR = { 0, 8, 10, 12, 14, 16 };
    public static final int TOTAL_SLOTS = 5;

    // ===================== MAINTENANCE STATUS =====================
    public static final String MAINT_REPORTED    = "Reported";
    public static final String MAINT_IN_PROGRESS = "In Progress";
    public static final String MAINT_RESOLVED    = "Resolved";
    public static final String MAINT_CLOSED      = "Closed";

    // ===================== ISSUE TYPES =====================
    public static final String[] ISSUE_TYPES = {
        "Damaged Equipment",
        "Air-Conditioning Problem",
        "Broken Chairs / Furniture",
        "Projector / AV System Issue",
        "Lighting Problem",
        "Cleanliness Issue",
        "Other"
    };

    // ===================== BLOCKS =====================
    public static final String[] BLOCKS = { "KA", "KB" };

    // ===================== FACULTIES =====================
    public static final String[] FACULTIES = {
        "Faculty of Information and Communication Technology (FICT)",
        "Faculty of Engineering and Green Technology (FEGT)",
        "Faculty of Business and Finance (FBF)",
        "Faculty of Arts and Social Science (FASS)",
        "Faculty of Science (FSC)",
        "Faculty of Medicine and Health Sciences (FMHS)",
        "Lee Kong Chian Faculty of Engineering and Science (LKC FES)"
    };

    // ===================== PROGRAMMES (by faculty index) =====================
    public static final String[][] PROGRAMMES = {
        // FICT (index 0)
        {
            "Bachelor of Computer Science (Hons)",
            "Bachelor of Information Systems (Hons)",
            "Bachelor of Information Technology (Hons)",
            "Bachelor of Software Engineering (Hons)",
            "Bachelor of Computer Science (Hons) Specialisation in Data Science",
            "Bachelor of Computer Science (Hons) Specialisation in Cybersecurity"
        },
        // FEGT (index 1)
        {
            "Bachelor of Engineering (Hons) Civil Engineering",
            "Bachelor of Engineering (Hons) Mechanical Engineering",
            "Bachelor of Engineering (Hons) Electrical and Electronic Engineering",
            "Bachelor of Engineering (Hons) Chemical Engineering",
            "Bachelor of Engineering (Hons) Environmental Engineering"
        },
        // FBF (index 2)
        {
            "Bachelor of Commerce (Hons) Accounting",
            "Bachelor of Business Administration (Hons)",
            "Bachelor of Economics (Hons)",
            "Bachelor of Finance (Hons)",
            "Bachelor of Marketing (Hons)"
        },
        // FASS (index 3)
        {
            "Bachelor of Arts (Hons) Chinese Studies",
            "Bachelor of Arts (Hons) English Language",
            "Bachelor of Communication (Hons)",
            "Bachelor of Arts (Hons) Journalism",
            "Bachelor of Social Science (Hons) Psychology"
        },
        // FSC (index 4)
        {
            "Bachelor of Science (Hons) Biochemistry",
            "Bachelor of Science (Hons) Biotechnology",
            "Bachelor of Science (Hons) Chemistry",
            "Bachelor of Science (Hons) Mathematical and Statistical Sciences",
            "Bachelor of Science (Hons) Physics"
        },
        // FMHS (index 5)
        {
            "Bachelor of Medicine and Bachelor of Surgery (MBBS)",
            "Bachelor of Pharmacy (Hons)",
            "Bachelor of Nursing (Hons)",
            "Bachelor of Biomedical Science (Hons)"
        },
        // LKC FES (index 6)
        {
            "Bachelor of Engineering (Hons) Electronic and Electrical Engineering",
            "Bachelor of Engineering (Hons) Mechatronics Engineering",
            "Bachelor of Science (Hons) Applied Science (Industrial Chemistry)",
            "Bachelor of Computer Science (Hons) Game Development"
        }
    };

    // ===================== DEPARTMENTS (Staff) =====================
    public static final String[] DEPARTMENTS = {
        "Academic Affairs",
        "Admissions and Records",
        "Finance and Accounts",
        "Human Resource",
        "Information Technology",
        "Library",
        "Facility Management",
        "Student Affairs",
        "Examination",
        "Marketing and Communications",
        "Research and Development",
        "Security"
    };

    // ===================== BOOKING / ADVANCE RULES =====================
    // Facilities that require 2 hours advance booking
    public static final String[] ADVANCE_2HR_TYPES = {
        "Lecture Hall", "Computer Lab", "Multipurpose Hall"
    };

    // ===================== REMINDER DAYS =====================
    public static final int REMINDER_DAYS_AHEAD = 3;

    // ===================== MAINTENANCE ALERT THRESHOLDS =====================
    public static final int ALERT_ISSUE_COUNT    = 3;   // 3+ unresolved = alert
    public static final int ALERT_OVERDUE_DAYS   = 7;   // 7+ days unresolved = alert

    // ===================== ADMIN CONTACT =====================
    public static final String ADMIN_NAME    = "Mr Lee";
    public static final String ADMIN_EMAIL   = "lee@utar.edu.my";
    public static final String ADMIN_PHONE   = "012345678";
}
