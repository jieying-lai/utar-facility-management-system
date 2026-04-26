package my.edu.utar.service;

import java.io.*;
import java.util.*;
import my.edu.utar.model.Facility;
import my.edu.utar.data.FileManager;
import my.edu.utar.util.Constants;

public class FacilitiesService {

	public List<Facility> getAllFacilities() {
	    return FileManager.loadAllFacilities();
	}
    public String addFacility(Facility f) {
        if (FileManager.isFacilityIdExists(f.getFacilityID())) {
            return "ID already exists.";
        }
        FileManager.saveFacility(f);
        return null; 
    }

    public boolean updateFacilities(List<Facility> updatedList) {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(Constants.FILE_FACILITIES, false)))) {
            for (Facility f : updatedList) {
                out.println(formatFacilityLine(f));
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateFacilityStatus(String facilityID, String newStatus) {
        List<Facility> list = getAllFacilities();
        boolean found = false;

        for (Facility f : list) {
            if (f.getFacilityID().equalsIgnoreCase(facilityID)) {
                f.setStatus(newStatus);
                found = true;
                break;
            }
        }

        if (found) {
            return updateFacilities(list); // Uses your existing updateFacilities method
        }
        
        System.out.println("Facility " + facilityID + " not found.");
        return false;
    }

    public String getFacilityNameById(String roomId) {
        List<Facility> facilities = getAllFacilities();
        for (Facility f : facilities) {
            if (f.getFacilityID().equalsIgnoreCase(roomId)) {
                return f.getName(); 
            }
        }
        return "Unknown Facility";
    }
    private String formatFacilityLine(Facility f) {
        return String.format("%s|%s|%s|%s|%s|%s|%d|%s",
                f.getFacilityID(), f.getBlock(), f.getFloor(), 
                f.getRoomNo(), f.getName(), f.getType(), 
                f.getCapacity(), f.getStatus());
    }
}