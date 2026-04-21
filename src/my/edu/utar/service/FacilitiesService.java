package my.edu.utar.service;

import my.edu.utar.model.Facility;
import my.edu.utar.data.FileManager;
import java.util.*;
import my.edu.utar.util.Constants;
import java.io.PrintWriter;

public class FacilitiesService {
	public List<Facility> searchFacilities(String keyword) {
	    List<Facility> allF = FileManager.loadAllFacilities();
	    List<Facility> searchResult = new ArrayList<>();
	    if (keyword == null) return searchResult;

	    String lowerKeyword = keyword.toLowerCase();

	    for (Facility f : allF) {
	        if (f.getFacilityID().toLowerCase().contains(lowerKeyword) || 
	            f.getBlock().toLowerCase().contains(lowerKeyword) ||
	            f.getType().toLowerCase().contains(lowerKeyword) ||
	            (f.getName() != null && f.getName().toLowerCase().contains(lowerKeyword))) {
	            
	            searchResult.add(f);
	        }
	    }
	    return searchResult; 
	}
	
	public List<Facility> getAllFacilities() {
        return FileManager.loadAllFacilities();
    }

    public String addFacility(Facility f) {
        if (FileManager.isFacilityIdExists(f.getFacilityID())) {
            return ("ID " + f.getFacilityID() + " exists in the system. Try another ID.");
        }
        if (FileManager.isExactFacilityDuplicate(f)) {
            return ("Facility added failed. It already exists in the system.");
        }
        FileManager.saveFacility(f);
        return null; 
    }

    public void removeFacility(String targetID) {
        List<Facility> all = FileManager.loadAllFacilities();
        try {
            new PrintWriter(Constants.FILE_FACILITIES).close();
            for (Facility f : all) {
                if (!f.getFacilityID().equalsIgnoreCase(targetID)) {
                    FileManager.saveFacility(f);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean updateFacilities(List<Facility> updatedList) {
        try {
            new PrintWriter(Constants.FILE_FACILITIES).close();
            for (Facility f : updatedList) {
                FileManager.saveFacility(f);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
