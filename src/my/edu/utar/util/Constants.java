package my.edu.utar.util;

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
    		"Centre for Foundation Studies (CFS)",
    		"M. Kandiah Faculty of Medicine and Health Sciences (MK FMHS)",
    		"Lee Kong Chian Faculty of Engineering and Science (LKC FES)",
    	    "Faculty of Accountancy and Management (FAM)",
    	    "Faculty of Creative Industries (FCI)",
    	    "Faculty of Chinese Studies (FCS)",
    	    "Faculty of Education (FEd)",
    	};

    // ===================== BOOKING / ADVANCE RULES =====================
    // Facilities that require 2 hours advance booking
    public static final String[] ADVANCE_2HR_TYPES = {
        "Lecture Hall", "Computer Lab", "Multipurpose Hall"
    };

    // ===================== REMINDER DAYS =====================
    public static final int REMINDER_DAYS_AHEAD = 3;

    // ===================== MAINTENANCE ALERT THRESHOLDS =====================
    public static final int ALERT_ISSUE_COUNT    = 3;   
    public static final int ALERT_OVERDUE_DAYS   = 7;   

    // ===================== ADMIN CONTACT =====================
    public static final String ADMIN_NAME    = "Mr Lee";
    public static final String ADMIN_EMAIL   = "lee@utar.edu.my";
    public static final String ADMIN_PHONE   = "012345678";
}
