package my.edu.utar.service;

import java.io.*;
import java.util.*;
import my.edu.utar.model.Facility;
import my.edu.utar.data.FileManager;
import my.edu.utar.util.Constants;

public class FacilitiesService {

	public List<Facility> getAllFacilities() {
	    // DO NOT return a static list. 
	    // Always call the FileManager to read the file again.
	    return FileManager.loadAllFacilities();
	}
    public String addFacility(Facility f) {
        // Validation
        if (FileManager.isFacilityIdExists(f.getFacilityID())) {
            return "ID already exists.";
        }
        // Direct save to file via FileManager
        FileManager.saveFacility(f);
        return null; 
    }

    public boolean updateFacilities(List<Facility> updatedList) {
        // OVERWRITE the file with the new list
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

    private String formatFacilityLine(Facility f) {
        return String.format("%s|%s|%s|%s|%s|%s|%d|%s",
                f.getFacilityID(), f.getBlock(), f.getFloor(), 
                f.getRoomNo(), f.getName(), f.getType(), 
                f.getCapacity(), f.getStatus());
    }
}