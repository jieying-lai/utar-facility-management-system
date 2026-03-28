package my.edu.utar.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

public class Validator {

    public static boolean isValidStudentID(String id) {
        if (id == null || id.isEmpty()) return false;
        return id.matches("\\d{7}");
    }

    public static boolean isValidStudentEmail(String email) {
        if (email == null || email.isEmpty()) return false;
        return email.toLowerCase().endsWith("@1utar.my");
    }

    public static boolean isValidStaffEmail(String email) {
        if (email == null || email.isEmpty()) return false;
        return email.toLowerCase().endsWith("@utar.edu.my");
    }

    public static boolean isValidEmail(String email, String role) {
        if (Constants.ROLE_STUDENT.equals(role)) return isValidStudentEmail(email);
        if (Constants.ROLE_STAFF.equals(role))   return isValidStaffEmail(email);
        return false;
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.isEmpty()) return false;
        return phone.matches("01\\d{8,9}");
    }

    public static boolean isValidPassword(String password) {
        if (password == null) return false;
        return password.length() >= 8;
    }

    public static boolean passwordsMatch(String password, String confirm) {
        if (password == null || confirm == null) return false;
        return password.equals(confirm);
    }


    public static boolean isValidName(String name) {
        if (name == null) return false;
        return !name.trim().isEmpty();
    }

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

    public static LocalDate parseDate(String dateStr) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("ddMMyyyy");
        return LocalDate.parse(dateStr, fmt);
    }

    public static boolean isDateTodayOrFuture(LocalDate date) {
        return !date.isBefore(LocalDate.now());
    }


    public static boolean isDateWithinOneMonth(LocalDate date) {
        LocalDate oneMonthLater = LocalDate.now().plusMonths(1);
        return !date.isAfter(oneMonthLater);
    }

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

    public static boolean isEmpty(String input) {
        if (input == null) return true;
        return input.trim().isEmpty();
    }

    public static boolean isValidMenuChoice(String input, int min, int max) {
        try {
            int choice = Integer.parseInt(input.trim());
            return choice >= min && choice <= max;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidPositiveInt(String input) {
        try {
            return Integer.parseInt(input.trim()) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

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
