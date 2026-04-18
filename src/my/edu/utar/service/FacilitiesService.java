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
        
        for (Facility f : allF) {
        	if (f.getFacilityID().toLowerCase().contains(keyword.toLowerCase()) || 
        		    f.getBlock().toLowerCase().contains(keyword.toLowerCase()) ||
        		    f.getType().toLowerCase().contains(keyword.toLowerCase())) {
        		    
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
