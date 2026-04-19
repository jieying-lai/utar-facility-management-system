package my.edu.utar.data;

import my.edu.utar.model.*;
import my.edu.utar.util.Constants;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

    public static void initFiles() {
        String[] files = {
            Constants.FILE_USERS,
            Constants.FILE_ADMIN,
            Constants.FILE_FACILITIES,
            Constants.FILE_BOOKINGS,
            Constants.FILE_MAINTENANCE
        };

        new File("data").mkdirs();

        for (String filePath : files) {
            File f = new File(filePath);
            if (!f.exists()) {
                try {
                    f.createNewFile();
                    System.out.println("[INFO] Created missing file: " + filePath);
                } catch (IOException e) {
                    System.out.println("[ERROR] Could not create file: " + filePath);
                }
            }
        }
    }

    public static List<String> readAllLines(String filePath) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line.trim());
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("[ERROR] File not found: " + filePath);
        } catch (IOException e) {
            System.out.println("[ERROR] Could not read file: " + filePath);
        }
        return lines;
    }

    public static void writeAllLines(String filePath, List<String> lines) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, false))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("[ERROR] Could not write to file: " + filePath);
        }
    }

    public static void appendLine(String filePath, String line) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            bw.write(line);
            bw.newLine();
        } catch (IOException e) {
            System.out.println("[ERROR] Could not append to file: " + filePath);
        }
    }

    public static List<User> loadAllUsers() {
        List<User> users = new ArrayList<>();
        List<String> lines = readAllLines(Constants.FILE_USERS);

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            try {
                String[] parts = line.split(Constants.DELIMITER_REGEX, -1);
                // Format: id|name|email|phone|role|faculty|programme|password
                if (parts.length < 8) {
                    System.out.println("[WARNING] Skipping malformed user data on line " + (i + 1));
                    continue;
                }
                String id         = parts[0].trim();
                String name       = parts[1].trim();
                String email      = parts[2].trim();
                String phone      = parts[3].trim();
                String role       = parts[4].trim();
                String faculty    = parts[5].trim();
                String programme  = parts[6].trim();
                String password   = parts[7].trim();

                if (Constants.ROLE_STUDENT.equals(role)) {
                    users.add(new Student(id, name, email, phone, password, faculty, programme));
                } else if (Constants.ROLE_STAFF.equals(role)) {
                    users.add(new Staff(id, name, email, phone, password, faculty, programme));
                }
            } catch (Exception e) {
                System.out.println("[WARNING] Error reading user on line " + (i + 1) + ", skipping.");
            }
        }
        return users;
    }

    public static void saveUser(User user) {
        appendLine(Constants.FILE_USERS, user.toFileString());
    }

    public static void updateUser(User updatedUser) {
        List<String> lines = readAllLines(Constants.FILE_USERS);
        for (int i = 0; i < lines.size(); i++) {
            String[] parts = lines.get(i).split(Constants.DELIMITER_REGEX, -1);
            if (parts.length > 0 && parts[0].trim().equals(updatedUser.getId())) {
                lines.set(i, updatedUser.toFileString());
                break;
            }
        }
        writeAllLines(Constants.FILE_USERS, lines);
    }

    public static boolean isUserIdExists(String id) {
        List<String> lines = readAllLines(Constants.FILE_USERS);
        for (String line : lines) {
            String[] parts = line.split(Constants.DELIMITER_REGEX, -1);
            if (parts.length > 0 && parts[0].trim().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public static User loginUser(String id, String password) {
        List<User> users = loadAllUsers();
        for (User user : users) {
            if (user.validateCredentials(id, password)) {
                return user;
            }
        }
        return null;
    }

    public static User findUserById(String id) {
        List<User> users = loadAllUsers();
        for (User user : users) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        return null;
    }

    public static List<Admin> loadAllAdmins() {
        List<Admin> admins = new ArrayList<>();
        List<String> lines = readAllLines(Constants.FILE_ADMIN);

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            try {
                String[] parts = line.split(Constants.DELIMITER_REGEX, -1);
                // Format: adminID|name|email|phone|department|password
                if (parts.length < 6) {
                    System.out.println("[WARNING] Skipping malformed admin data on line " + (i + 1));
                    continue;
                }
                admins.add(new Admin(
                    parts[0].trim(), parts[1].trim(), parts[2].trim(),
                    parts[3].trim(), parts[5].trim(), parts[4].trim()
                ));
            } catch (Exception e) {
                System.out.println("[WARNING] Error reading admin on line " + (i + 1) + ", skipping.");
            }
        }
        return admins;
    }

    public static Admin loginAdmin(String id, String password) {
        List<Admin> admins = loadAllAdmins();
        for (Admin admin : admins) {
            if (admin.validateCredentials(id, password)) {
                return admin;
            }
        }
        return null;
    }
    
    public static List<Facility> loadAllFacilities(){    
    	List<Facility> facilities = new ArrayList<>();
        List<String> lines = readAllLines(Constants.FILE_FACILITIES);
        
        for (int i = 0; i < lines.size(); i++) {
        	try {
        		String[] parts = lines.get(i).split(Constants.DELIMITER_REGEX, -1);
        		if (parts.length < 7) continue;
        		
        		facilities.add(new Facility(
        				parts[0].trim(), 
                        parts[1].trim(), 
                        parts[2].trim(), 
                        parts[3].trim(), 
                        parts[4].trim(), 
                        Integer.parseInt(parts[5].trim()), 
                        parts[6].trim()
                ));
        	}catch (Exception e) {
        		System.out.println("[WARNING] Error parsing facility on line " + (i + 1));
        	}
        }
        return facilities;
    }
    
    public static void saveFacility(Facility f) {
        appendLine(Constants.FILE_FACILITIES, f.toFileString());
    }

    public static void updateAllFacilities(List<Facility> facilities) {
        List<String> lines = new ArrayList<>();
        for (Facility f : facilities) {
            lines.add(f.toFileString());
        }
        writeAllLines(Constants.FILE_FACILITIES, lines);
    }
    
    public static boolean isFacilityIdExists(String id) {
        List<Facility> list = loadAllFacilities();
        for (Facility f : list) {
            if (f.getFacilityID().equalsIgnoreCase(id.trim())) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isExactFacilityDuplicate(Facility newFac) {
    	List<Facility> list = loadAllFacilities();
    	for(Facility f :list) {
    		if(
    		   f.getBlock().equalsIgnoreCase(newFac.getBlock())&&
    		   f.getFloor().equalsIgnoreCase(newFac.getFloor())&&
    		   f.getRoomNo().equalsIgnoreCase(newFac.getRoomNo())&&
    		   f.getType().equalsIgnoreCase(newFac.getType())&&
    		   f.getCapacity() == newFac.getCapacity())
    		{
    			return true;
    		}
    	}
    	return false;
    }
    
    public static List<MaintenanceReport> readMaintenanceReports()
    {
    	List<MaintenanceReport> reports = new ArrayList<>();
    	try (BufferedReader br = new BufferedReader(new FileReader(Constants.FILE_MAINTENANCE)))
    	{
    		String line;
    		int lineNumber = 0;
    		while((line = br.readLine()) != null)
    		{
    			lineNumber++;
    			if (line.trim().isEmpty()) continue;
                String[] parts = line.split("\\|", -1); 
                if (parts.length < 9)
                {
                	System.out.println("[WARNING] Data error occured on line" + lineNumber + " .");
                	continue; // skip the error part then continue with the next
                }
                reports.add(new MaintenanceReport(parts[0], parts[1],parts[2],parts[3],parts[4],parts[5],parts[6],parts[7],parts[8]));
    		}
    	}
		catch (FileNotFoundException e)
		{
			try
			{
				new File(Constants.FILE_MAINTENANCE).createNewFile();
			}
			catch (IOException ex)
			{
				System.out.println("Could not create maintenance.txt");
			}
		}
		catch (IOException e)
		{
			System.out.println("File error: try again.");
		}
		return reports;
		
	}    
    public static void writeMaintenanceReports(List<MaintenanceReport> reports)
    {
    	try (BufferedWriter bw = new BufferedWriter(new FileWriter(Constants.FILE_MAINTENANCE, false)))
    	{
            for (MaintenanceReport r : reports)
            {
                bw.write(r.toFileString());
                bw.newLine();
            }
    	}
    	catch (IOException e)
    	{
    		System.out.println("File error: try again.");
    	}
    }
    
    public static void appendMaintenanceReport(MaintenanceReport reports)
    {
    	try (BufferedWriter bw = new BufferedWriter(new FileWriter(Constants.FILE_MAINTENANCE, true)))
    	{
            bw.write(reports.toFileString());
            bw.newLine();
    	}
    	catch (IOException e)
    	{
    		System.out.println("File error: try again.");
    	}
    }
    
    public static String generateMaintenanceID()
    {
    	LocalDate today = LocalDate.now();
    	String datePart = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    	List<MaintenanceReport> all = readMaintenanceReports();
    	long count = all.stream().filter(r -> r.getIssueID().startsWith("M" + datePart)).count();
    	return String.format("M%s%04d", datePart, count + 1);
    }
    
 // 在 FileManager.java 类里面添加这个静态方法
    public static void deleteUser(String userId) {
        List<User> users = loadAllUsers();
        
        boolean removed = users.removeIf(u -> u.getId().equalsIgnoreCase(userId));
        
        if (removed) {
            saveAllUsers(users); 
            System.out.println(">> Database updated: User " + userId + " removed.");
        } else {
            System.out.println(">> Error: User ID not found in database.");
        }
    }
    
    public static void saveAllUsers(List<User> users) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("data/users.txt"))) {
            for (User u : users) {
                
                writer.println(u.toFileString()); 
            }
        } catch (IOException e) {
            System.out.println("Error saving users: " + e.getMessage());
        }
    }
    
}