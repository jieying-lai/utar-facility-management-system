package my.edu.utar.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

/**
 * Validator.java
 * Utility class with static methods to validate all user inputs.
 * Used by all members across the system — do not modify without discussion.
 */
public class Validator {

    // ===================== STUDENT ID =====================
    /**
     * Validates student ID: must be exactly 7 digits, all numeric.
     * e.g. 2301888 is valid, 230ABC is not.
     */
    public static boolean isValidStudentID(String id) {
        if (id == null || id.isEmpty()) return false;
        return id.matches("\\d{7}");
    }

    // ===================== EMAIL =====================
    /**
     * Validates student email: must end with @1utar.my
     */
    public static boolean isValidStudentEmail(String email) {
        if (email == null || email.isEmpty()) return false;
        return email.toLowerCase().endsWith("@1utar.my");
    }

    /**
     * Validates staff email: must end with @utar.edu.my
     */
    public static boolean isValidStaffEmail(String email) {
        if (email == null || email.isEmpty()) return false;
        return email.toLowerCase().endsWith("@utar.edu.my");
    }

    /**
     * Validates email based on role.
     */
    public static boolean isValidEmail(String email, String role) {
        if (Constants.ROLE_STUDENT.equals(role)) return isValidStudentEmail(email);
        if (Constants.ROLE_STAFF.equals(role))   return isValidStaffEmail(email);
        return false;
    }

    // ===================== PHONE NUMBER =====================
    /**
     * Validates Malaysian phone number.
     * Must start with 01, total 10 or 11 digits, numeric only.
     * e.g. 0112345678 or 01112345678
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.isEmpty()) return false;
        return phone.matches("01\\d{8,9}");
    }

    // ===================== PASSWORD =====================
    /**
     * Validates password length: must be at least 8 characters.
     */
    public static boolean isValidPassword(String password) {
        if (password == null) return false;
        return password.length() >= 8;
    }

    /**
     * Checks if two password entries match.
     */
    public static boolean passwordsMatch(String password, String confirm) {
        if (password == null || confirm == null) return false;
        return password.equals(confirm);
    }

    // ===================== NAME =====================
    /**
     * Checks name is not empty or spaces only.
     */
    public static boolean isValidName(String name) {
        if (name == null) return false;
        return !name.trim().isEmpty();
    }

    // ===================== DATE =====================
    /**
     * Validates date string in DDMMYYYY format.
     * Returns true only if format is correct AND date is a real calendar date.
     */
    public static boolean isValidDateFormat(String dateStr) {
        if (dateStr == null || dateStr.length() != 8) return false;
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("ddMMyyyy");
            LocalDate.parse(dateStr, fmt);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Parses a DDMMYYYY string to LocalDate.
     * Call isValidDateFormat() first before calling this.
     */
    public static LocalDate parseDate(String dateStr) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("ddMMyyyy");
        return LocalDate.parse(dateStr, fmt);
    }

    /**
     * Checks if a date is today or in the future.
     */
    public static boolean isDateTodayOrFuture(LocalDate date) {
        return !date.isBefore(LocalDate.now());
    }

    /**
     * Checks if a date is within 1 month from today.
     */
    public static boolean isDateWithinOneMonth(LocalDate date) {
        LocalDate oneMonthLater = LocalDate.now().plusMonths(1);
        return !date.isAfter(oneMonthLater);
    }

    /**
     * Full date validation for booking search.
     * Returns an error message string, or null if date is valid.
     */
    public static String validateBookingDate(String dateStr) {
        if (!isValidDateFormat(dateStr)) {
            return "Invalid date format. Please enter date as DDMMYYYY (e.g., 14032026).";
        }
        LocalDate date = parseDate(dateStr);
        if (!isDateTodayOrFuture(date)) {
            return "You can only search for today's schedule and future dates.";
        }
        if (!isDateWithinOneMonth(date)) {
            return "You can only book within 1 month from today.\n" +
                   "For dates beyond this period, please contact " +
                   Constants.ADMIN_NAME + " at " + Constants.ADMIN_EMAIL +
                   " or " + Constants.ADMIN_PHONE + ".";
        }
        return null; // null means valid
    }

    // ===================== MENU SELECTION =====================
    /**
     * Checks if input is empty or whitespace only.
     */
    public static boolean isEmpty(String input) {
        if (input == null) return true;
        return input.trim().isEmpty();
    }

    /**
     * Validates a numeric menu selection within a given range.
     * Returns true if input is a number between min and max inclusive.
     */
    public static boolean isValidMenuChoice(String input, int min, int max) {
        try {
            int choice = Integer.parseInt(input.trim());
            return choice >= min && choice <= max;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validates positive integer (e.g., number of people).
     */
    public static boolean isValidPositiveInt(String input) {
        try {
            return Integer.parseInt(input.trim()) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // ===================== ADVANCE BOOKING TIME CHECK =====================
    /**
     * Checks if a time slot can be booked based on advance booking rules.
     * For Lecture Hall, Lab, Multipurpose Hall: at least 2 hours before slot.
     * For others: must be before the slot starts.
     *
     * @param facilityType  the type of facility
     * @param slotIndex     the time slot index (1-5)
     * @param currentHour   the current hour (24hr format)
     * @return true if booking is allowed
     */
    public static boolean isAdvanceBookingAllowed(String facilityType, int slotIndex, int currentHour) {
        int slotStartHour = Constants.TIME_SLOT_START_HOUR[slotIndex];
        boolean requires2Hr = false;
        for (String type : Constants.ADVANCE_2HR_TYPES) {
            if (type.equalsIgnoreCase(facilityType)) {
                requires2Hr = true;
                break;
            }
        }
        if (requires2Hr) {
            return currentHour <= slotStartHour - 2;
        } else {
            return currentHour < slotStartHour;
        }
    }
}
