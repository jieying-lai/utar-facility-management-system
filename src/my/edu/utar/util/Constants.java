package my.edu.utar.util;
import java.util.HashMap;
import java.util.Map;

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
    public static final String FACILITY_MAINTENANCE = "Under Maintenance";

    // ===================== FACILITY TYPES =====================
    public static final String[] FACILITY_TYPES = {
        "Lecture Hall",
        "Tutorial Room",
        "Laboratory",
        "Discussion Room",
        "Sports Court",
        "Computer Lab",
        "Multipurpose Hall"
    };

    // ===================== TIME SLOTS =====================
    public static final String[] TIME_SLOTS = {
        "", 
        "8 a.m. - 10 a.m ",  
        "10 a.m. - 12 p.m.", 
        "12 p.m. -  2 p.m.", 
        "2 p.m. -  4 p.m.", 
        "4 p.m. -  6 p.m."   
    };
    public static final int[] TIME_SLOT_START_HOUR = { 0, 8, 10, 12, 14, 16 };
    public static final int TOTAL_SLOTS = 5;

    // ===================== MAINTENANCE STATUS =====================
    public static final String MAINT_REPORTED    = "Reported";
    public static final String MAINT_IN_PROGRESS = "In Progress";
    public static final String MAINT_RESOLVED    = "Resolved";

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
    
    public static final Map<String, String> FACULTY_CODE_MAP = new HashMap<>();
    static {
        FACULTY_CODE_MAP.put(FACULTIES[0], "CFS");
        FACULTY_CODE_MAP.put(FACULTIES[1], "MKFMHS");
        FACULTY_CODE_MAP.put(FACULTIES[2], "LKCFES");
        FACULTY_CODE_MAP.put(FACULTIES[3], "FAM");
        FACULTY_CODE_MAP.put(FACULTIES[4], "FCI");
        FACULTY_CODE_MAP.put(FACULTIES[5], "FCS");
        FACULTY_CODE_MAP.put(FACULTIES[6], "FEd");
    }

 // ===================== PROGRAMMES MAPPING =====================
    public static final Map<String, Map<String, String>> FACULTY_PROGRAMME_MAP = new HashMap<>();

    static {
        // 1. MK FMHS
        Map<String, String> mkfmhs = new HashMap<>();
        mkfmhs.put("CD", "Chinese Medicine");
        mkfmhs.put("MS", "Medicine and Surgery");
        mkfmhs.put("NS", "Nursing");
        mkfmhs.put("PS", "Physiotherapy");
        FACULTY_PROGRAMME_MAP.put("M. Kandiah Faculty of Medicine and Health Sciences (MK FMHS)", mkfmhs);

        // 2. LKC FES
        Map<String, String> lkcfes = new HashMap<>();
        lkcfes.put("3E", "Electrical and Electronic Engineering");
        lkcfes.put("AM", "Applied Mathematics with Computing");
        lkcfes.put("AR", "Architecture");
        lkcfes.put("AS", "Actuarial Science");
        lkcfes.put("BI", "Biomedical Engineering");
        lkcfes.put("CI", "Civil Engineering");
        lkcfes.put("CL", "Chemical Engineering");
        lkcfes.put("EC", "Telecommunications Engineering");
        lkcfes.put("ET", "Electronics (Computer Networking)");
        lkcfes.put("FM", "Financial Mathematics");
        lkcfes.put("ME", "Mechanical Engineering");
        lkcfes.put("MH", "Mechatronics Engineering");
        lkcfes.put("MM", "Materials Engineering");
        lkcfes.put("MT", "Materials Engineering");
        lkcfes.put("PH", "Physics");
        lkcfes.put("QS", "Quantity Surveying");
        lkcfes.put("SE", "Software Engineering");
        lkcfes.put("TE", "Telecommunications Engineering");
        FACULTY_PROGRAMME_MAP.put("Lee Kong Chian Faculty of Engineering and Science (LKC FES)", lkcfes);

        // 3. FAM
        Map<String, String> fam = new HashMap<>();
        fam.put("AT", "Accounting");
        fam.put("BP", "Building and Property Management");
        fam.put("FT", "Financial Technology");
        fam.put("GE", "Global Economics");
        fam.put("IN", "International Business");
        FACULTY_PROGRAMME_MAP.put("Faculty of Accountancy and Management (FAM)", fam);

        // 4. FCI
        Map<String, String> fci = new HashMap<>();
        fci.put("BC", "Broadcasting");
        fci.put("CC", "Corporate Communication");
        fci.put("DA", "Digital Animation");
        fci.put("EA", "Early Childhood Education");
        fci.put("GD", "Graphic Design and Multimedia");
        fci.put("GS", "Game Design");
        fci.put("GV", "Game Development");
        fci.put("JC", "Journalism in Chinese Media");
        fci.put("MC", "Media and Creative Studies");
        FACULTY_PROGRAMME_MAP.put("Faculty of Creative Industries (FCI)", fci);

        // 5. FEd
        Map<String, String> fed = new HashMap<>();
        fed.put("ED", "English Education");
        FACULTY_PROGRAMME_MAP.put("Faculty of Education (FEd)", fed);
    }
    
    // ===================== BOOKING / ADVANCE RULES =====================
    // Facilities that require 2 hours advance booking
    public static final String[] ADVANCE_2HR_TYPES = {
        "Lecture Hall", "Computer Lab", "Multipurpose Hall"
    };

    // ===================== ADMIN CONTACT =====================
    public static final String ADMIN_NAME    = "Mr Lee";
    public static final String ADMIN_EMAIL   = "lee@utar.edu.my";
    public static final String ADMIN_PHONE   = "012345678";
    
    public static final java.time.format.DateTimeFormatter DATE_DISPLAY_FORMAT = 
    	    java.time.format.DateTimeFormatter.ofPattern("dd MMMM yyyy");
}
