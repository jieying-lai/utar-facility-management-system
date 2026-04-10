
package my.edu.utar.ui;

import java.io.File;

import java.util.Scanner;
import java.util.List;
import my.edu.utar.service.BookingManager;

import my.edu.utar.data.FileManager;
import my.edu.utar.model.Admin;
import my.edu.utar.model.Facility;
import my.edu.utar.util.Constants;
import my.edu.utar.util.Validator;
import my.edu.utar.service.FacilitiesService;
public class AdminMenu{

    private Scanner sc;
    private Admin currentAdmin;
    
    private FacilitiesService facilityService = new FacilitiesService();
    private BookingManager bookingManager = new BookingManager();

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
        System.out.println("\n--- Search The Facility Status ---");
        System.out.println("Example Keyword:facilityID,block,floor,roomNo,type,capacity,status");
        System.out.println("Enter Search Keyword: ");
        String input = sc.nextLine().trim().toLowerCase();

        if(input.isEmpty()) {
        	return;
        }
        
        List<Facility> results = facilityService.searchFacilities(input);

        if (results.isEmpty()) {
            System.out.println("No matching facility found. Please Try Again.");
        } else {
        	System.out.printf("%-10s %-8s %-8s %-10s %-15s %-10s %-15s\n", 
                    "ID", "Block", "Floor", "Room", "Type", "Capacity", "Status");
            for (Facility f : results) {
                System.out.printf("%-10s %-8s %-8s %-10s %-15s %-10s %-15s\n", f.getFacilityID(), f.getBlock(), f.getFloor(), f.getRoomNo(),
                        f.getType(), f.getCapacity(), f.getStatus());
            }
        }
    }

    private void manageFacility() {
    	Scanner input = new Scanner (System.in);
    	while (true) {
    		System.out.println("Facilities Management Page");
            System.out.println("[1] View all current facilities");
            System.out.println("[2] Add new facilities");
            System.out.println("[3] Remove unused facilities");
            System.out.println("[4] Edit current facilities details");
            System.out.println("[0] Return to Main Menu");
            System.out.println("Please enter your choice: ");
            String choice = sc.nextLine().trim().toUpperCase();
            
            if (Validator.isEmpty(choice)) {
                System.out.println("Cannot be empty. Please try again.");
                continue;
            }
            
            if (choice.equals("0")) {
            	return;
            }
            
            switch (choice) {
            case "1" :
            	List<Facility> all = facilityService.getAllFacilities();
                System.out.println("Current facilities List");
                for (Facility f : all) {
                	System.out.println(f.toFileString());
                }
                break;

            case "2" :
            	System.out.println("Please add a new facility by using format below.");
            	System.out.println("facilityID|block|floor|roomNo|type|capacity|status");
            	System.out.print("Enter facility ID (e.g. F001): ");
            	String ID = input.nextLine();
            	
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
                
                String errorMessage = facilityService.addFacility(f);
                
                if (errorMessage != null) {
                    System.out.println(errorMessage);
                } else {
                    System.out.println("The facility is successfully added!");
                }
                
            	break;
            	
            case "3" :
            	System.out.println("------Facilities Removal Page------");
            	System.out.println("Enter the facility ID that you would like to remove:");
            	String theremovingID = input.nextLine().trim();
            	facilityService.removeFacility(theremovingID);
                System.out.println("The Facility list has been updated.");
                break;
            	
            case "4" :
            	System.out.println("------Edit Facilities Details Page------");
            	System.out.println("Enter the facility ID that you would like to remove:");
            	String theeditingID = input.nextLine().trim();
            	
            	List<Facility> allF = facilityService.getAllFacilities();
            	Facility target = null;
            	
            	for(Facility f1 : allF) {
            		if(f1.getFacilityID().equalsIgnoreCase(theeditingID)) {
            			target = f1;
            			break;
            		}
            	}
            	
            	if(target == null) {
            		System.out.println("The facility ID is not existed.");
            		return;
            	} else {
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
                    	break;
                    }
                    
                    if (facilityService.updateFacilities(allF)) {
                        System.out.println("Successfully Edited!");
                    } else {
                        System.out.println("Error on saving edited file.");
                    }
                    	
            	}
                break;
                
            default:
                System.out.println("Invalid selection.");
                break;
            }
    	}
     }

    
    private void approval() {
    	Scanner input = new Scanner(System.in);
	    System.out.print("Enter User ID: ");
	    String userID = input.nextLine().trim();
	    
	    if (Validator.isEmpty(userID)) {
	        System.out.println("Booking ID cannot be empty.");
	        return;
	    }
	    if (bookingManager.approveBooking(userID)) {
	        System.out.println("Booking [" +userID+ "] has been approved successfully!");
	    } else {
	        System.out.println("Failed to find or approve Booking ID: " + userID);
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
