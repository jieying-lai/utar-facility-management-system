
package my.edu.utar.ui;

import java.io.File;
import java.util.Scanner;
import java.util.List;
import java.util.Scanner;

import my.edu.utar.data.FileManager;
import my.edu.utar.model.Admin;
import my.edu.utar.model.Facility;
import my.edu.utar.util.Constants;
import my.edu.utar.util.Validator;

public class AdminMenu {

    private Scanner sc;
    private Admin currentAdmin;

    public AdminMenu(Scanner sc, Admin currentAdmin) {
        this.sc = sc;
        this.currentAdmin = currentAdmin;
    }

    public void show() {
        while (true) {
            showIssueAlerts();  
            printAdminMenu();

            String choice = sc.nextLine().trim().toUpperCase();

            if (Validator.isEmpty(choice)) {
                System.out.println("Cannot be empty. Please try again.");
                continue;
            }
          
            switch (choice) {
                case "1": searchFacility();         break;
                case "2": manageFacility();         break;
                case "3": approval();        break;
                case "4": facilityUsageTracking();  break;
                case "5": viewSummaryReport();      break;
                case "6": maintenanceManagement();  break;
                case "7": issueAlert();             break;
                case "L":
                    System.out.println("Logged out. Goodbye, " + currentAdmin.getName() + "!");
                    return;
                default:
                    System.out.println("Invalid selection, please try again.");
            }
           }
        }
    
    private void printAdminMenu() {
        System.out.println("\n============================================");
        System.out.println("   ADMIN DASHBOARD");
        System.out.println("   " + currentAdmin.getName());
        System.out.println("============================================");
        System.out.println("[1] Search Facility Status");
        System.out.println("[2] Manage Facilities");
        System.out.println("[3] Process Booking Requests");
        System.out.println("[4] Facility Usage Tracking");
        System.out.println("[5] View Summary Report");
        System.out.println("[6] Maintenance Management");
        System.out.println("[7] View Issue Alerts");
        System.out.println("[L] Logout");
        System.out.println("--------------------------------------------");
        System.out.print("Enter your choice: ");
    }

    private void showIssueAlerts() {
        // TODO Member 3: implement alert check here
    }

    private void searchFacility() {
    	System.out.println("\n--- Search Facility Status ---");
    	System.out.println("Enter search keyword (ID, Block, or Type): ");
    	System.out.print("[Press 0 return to previous menu]:");
        String input = sc.nextLine().trim().toLowerCase();
        
        if(input.equals("0")){
        	System.out.print("Returning to the Admin Dashboard Page...");
        	return;
        }
        
        if(input.isEmpty()) {
        	System.out.print("Error,Cannot be empty.Returning...");
        	return;
        }
        
        List<Facility> allFacilities = FileManager.loadAllFacilities();
        boolean found = false;
        
        System.out.println("\n---------------------------------------------------------------------------------------");
        System.out.printf("%-10s %-8s %-8s %-10s %-15s %-10s %-15s\n", 
                          "ID", "Block", "Floor", "Room", "Type", "Capacity", "Status");
        System.out.println("---------------------------------------------------------------------------------------");
        
        for (Facility f : allFacilities) {
            if (f.getFacilityID().toLowerCase().contains(input) ||
                f.getBlock().toLowerCase().contains(input) ||
                f.getType().toLowerCase().contains(input)) {
                
                System.out.printf("%-10s %-8s %-8s %-10s %-15s %-10s %-15s\n",
                    f.getFacilityID(), f.getBlock(), f.getFloor(), f.getRoomNo(),
                    f.getType(), f.getCapacity(), f.getStatus());
                
                found = true;
            }
        }
        
        if (!found) {
            System.out.println("No matching facilities found for: " + input);
        }
        System.out.println("---------------------------------------------------------------------------------------");
    }

    private void manageFacility() {
    	Scanner input = new Scanner (System.in);
    	while (true) {
    		System.out.println("Facilities Management Page");
            System.out.println("Please enter your choice: ");
            System.out.println("[1] View all current facilities");
            System.out.println("[2] Add new facilities");
            System.out.println("[3] Remove unused facilities");
            System.out.println("[4] Edit current facilities details");
            String choice = sc.nextLine().trim().toUpperCase();
            
            if (Validator.isEmpty(choice)) {
                System.out.println("Cannot be empty. Please try again.");
                continue;
            }
 
            switch (choice) {
            case "1" :
            	System.out.println("---Current facilities List---");
            	System.out.println("facilityID|block|floor|roomNo|type|capacity|status");
            	List <Facility> all =FileManager.loadAllFacilities();
            	for(Facility f : all) {
            		System.out.println(f.toFileString());
            	}
            	System.out.println("Press enter button to return to the admin menu...");
            	input.nextLine();
            break;
            
            case "2" :
            	System.out.println("Please add a new facility by using format below.");
            	System.out.println("facilityID|block|floor|roomNo|type|capacity|status");
            	System.out.print("Enter facility ID (e.g. F001): ");
            	String ID = input.nextLine();
            	
            	if(FileManager.isFacilityIdExists(ID)) {
            		System.out.println("ID" + ID + "exists in the system. Try another ID.");
            	} else {
            		System.out.print("Enter block name (e.g. KB): ");
            		String block = input.nextLine();
                	System.out.print("Enter floor number (e.g. 1): ");
                	String num = input.nextLine();
                	System.out.print("Enter roomNo (e.g. KB104): ");
                	String roomNo = input.nextLine();
                	System.out.print("Enter type of the facility (e.g. Lecture hall): ");
                	String type = input.nextLine();
                	System.out.print("Enter capacity of the facility (e.g. 80): ");
                	String cap = input.nextLine();
                	int capacity = Integer.parseInt(cap);
                	String status = "Available";
                	
                	Facility f = new Facility(ID,block,num,roomNo,type,capacity,status);
                	
                	if(FileManager.isExactFacilityDuplicate(f)) {
                		System.out.println("Facility added failed. It is already exists in the system.");
                	} else {
                		FileManager.saveFacility(f);
                		System.out.println("The facility is successfully added!");
                	}
            	}
            	break;
            	
            case "3" :
            	System.out.println("------Facilities Removal Page------");
            	System.out.println("Enter the facility ID that you would like to remove:");
            	String theremovingID = input.nextLine().trim();
            	List <Facility> allf = FileManager.loadAllFacilities();
            	
            	try {
            		new java.io.PrintWriter(Constants.FILE_FACILITIES).close();
            	}catch (Exception e){}
            	
            	for(Facility f : allf) {
            		if(!f.getFacilityID().equalsIgnoreCase(theremovingID)) {
            			FileManager.saveFacility(f);
            		}
            	}
            	System.out.println("The Facility list have been updated. Please check again.");
            	break;
            	
            case "4" :
            	System.out.println("------Edit Facilities Details Page------");
            	System.out.println("Enter the facility ID that you would like to remove:");
            	String theeditingID = input.nextLine().trim();
            	
            	List <Facility> allf1 =FileManager.loadAllFacilities();
            	Facility target = null;
            	
            	for(Facility f : allf1) {
            		if(f.getFacilityID().equalsIgnoreCase(theeditingID)) {
            			target = f;
            			break;
            		}
            	}
            	
            	if(target == null) {
            		System.out.println("The facility ID is not existed.");
            		return;
            	}
            	
            	System.out.println("------Current Details of the Facility ID------");
            	target.display();
            	
            	System.out.println("What would you like to edit?");
            	System.out.println("[1] Block name");
            	System.out.println("[2] Floor number");
            	System.out.println("[3] Room number");
            	System.out.println("[4] Facility type");
            	System.out.println("[5] Faciltiy capacity");
            	System.out.println("[6] Facility status");
            	System.out.print("Choice: ");
                int Choice = Integer.parseInt(input.nextLine());
                
                switch(Choice) {
                case 1:
                	System.out.println("Enter new block name:(e.g. KB) ");
                	target.setBlock(input.nextLine());
                	break;
                case 2:
                	System.out.println("Enter new floor number:(e.g. 1) ");
                	target.setFloor(input.nextLine());
                	break;
                case 3:
                	System.out.println("Enter new room number:(KB104) ");
                	target.setRoomNo(input.nextLine());
                	break;
                case 4:
                	System.out.println("Enter new facility type:(lecture hall) ");
                	target.setType(input.nextLine());
                	break;
                case 5:
                	System.out.println("Enter new facility capacity: (e.g. 80) ");
                	target.setCapacity(Integer.parseInt(input.nextLine()));
                	break;
                case 6:
                	System.out.println("Enter new facility status: (e.g. active) ");
                	target.setStatus(input.nextLine());
                	break;
                default:
                	System.out.println("Only choice 1 to 6 make changes. Please try again.");
                	return;
                	
                }
                try {
                	new java.io.PrintWriter(Constants.FILE_FACILITIES).close();
                	for(Facility f2 : allf1) {
                		FileManager.saveFacility(f2);
                	}
                	
                	System.out.println("Successfully Edited!");
                	target.display();
                }catch (Exception e) {
                	System.out.println("Error on saving edited file. Please try again!");
                }
            }
    	}
     }

    private abstract class Booking{
    	private String ID;
    	public abstract void approvalBooking();
 
    
    	public Booking(String id) {
    		this.ID=id;
    	}
    
    	public String getID() {
    		return ID;
    	}
    }
    
    private class OfficialBooking extends Booking{
    	public OfficialBooking(String id) {
			super(id);
		}

    	@Override
		public void approvalBooking() {
    		System.out.println("Approved successfully!");
    	}
    }
    
    	private void approval() {
    	    Scanner input = new Scanner(System.in);
    	    System.out.print("Enter User ID: ");
    	    String userID = input.next();

    	    File uFile = new File("data/users.txt");
    	    File bFile = new File("data/bookings.txt");

    	    if (!uFile.exists() || !bFile.exists()) {
    	        System.out.println("System Error: File 'users.txt' or 'bookings.list' is missing from the folder!");
    	        return;
    	    }

    	    try {
    	        Scanner userReader = new Scanner(uFile);
    	        boolean userFound = false;
    	        while (userReader.hasNextLine()) {
    	            String[] data = userReader.nextLine().split("\\|");
    	            if (data.length > 0 && data[0].equals(userID)) {
    	                userFound = true;
    	                break;
    	            }
    	        }
    	        userReader.close();

    	        if (!userFound) {
    	            System.out.println("User ID not found in users.txt");
    	            return;
    	        }

    	        Scanner bookReader = new Scanner(bFile);
    	        boolean bookFound = false;
    	        while (bookReader.hasNextLine()) {
    	            String[] data = bookReader.nextLine().split("\\|");
    	            if (data.length > 1 && data[1].equals(userID)) {
    	                bookFound = true;
    	                Booking request = new OfficialBooking(userID);
    	                request.approvalBooking();
    	                break;
    	            }
    	        }
    	        bookReader.close();

    	        if (!bookFound) System.out.println("No pending bookings found for this user.");

    	    } catch (Exception e) {
    	        System.out.println("A logic error occurred: " + e.getMessage());
    	    }
    	}

    /** TODO Member 4 */
    private void facilityUsageTracking() {
        System.out.println("[TODO - Member 4] Facility Usage Tracking");
    }

    /** TODO Member 4 */
    private void viewSummaryReport() {
        System.out.println("[TODO - Member 4] View Summary Report");
    }

    /** TODO Member 3 */
    private void maintenanceManagement() {
        System.out.println("[TODO - Member 3] Maintenance Management");
    }

    /** TODO Member 3 */
    private void issueAlert() {
        System.out.println("[TODO - Member 3] Issue Alerts");
    }
}
