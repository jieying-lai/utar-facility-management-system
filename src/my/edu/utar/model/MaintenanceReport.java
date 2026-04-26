package my.edu.utar.model;

import my.edu.utar.util.Constants;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class MaintenanceReport {

    private String issueID; 
    private String facilityID;
    private String reporterID; 
    private String issueType;  
    private String description;
    private String reportDate;  
    private String status;        
    private String assignedTo;   
    private String resolvedDate; 
    
    public MaintenanceReport(String issueID, String facilityID, String reporterID,
            String issueType, String description, String reportDate) {
        this.issueID      = issueID;
        this.facilityID   = facilityID;
        this.reporterID   = reporterID;
        this.issueType    = issueType;
        this.description  = description;
        this.reportDate   = reportDate;
        this.status       = Constants.MAINT_REPORTED; 
        this.assignedTo   = "";
        this.resolvedDate = "";
    }
    
    public MaintenanceReport(String issueID, String facilityID, String reporterID,
                             String issueType, String description, String reportDate,
                             String status, String assignedTo, String resolvedDate) {
        this.issueID      = issueID;
        this.facilityID   = facilityID;
        this.reporterID   = reporterID;
        this.issueType    = issueType;
        this.description  = description;
        this.reportDate   = reportDate;
        this.status       = status;
        this.assignedTo   = assignedTo;
        this.resolvedDate = resolvedDate;
    }

    public MaintenanceReport() {}

    public String getIssueID()      { return issueID; }
    public String getFacilityID()   { return facilityID; }
    public String getReporterID()   { return reporterID; }
    public String getIssueType()    { return issueType; }
    public String getDescription()  { return description; }
    public String getReportDate()   { return reportDate; }
    public String getStatus()       { return status; }
    public String getAssignedTo()   { return assignedTo; }
    public String getResolvedDate() { return resolvedDate; }

    public void setStatus(String status)             { this.status = status; }
    public void setAssignedTo(String assignedTo)     { this.assignedTo = assignedTo; }
    public void setResolvedDate(String resolvedDate) { this.resolvedDate = resolvedDate; }
    
    public String toFileString() {
        return issueID + "|" + facilityID + "|" +
               reporterID + "|" + issueType + "|" +
               description + "|" + reportDate + "|" +
               status + "|" + assignedTo + "|" + resolvedDate;
    }

    public long getDaysToResolve() {
        if (resolvedDate == null || resolvedDate.isEmpty() || reportDate == null) return 0;
        try {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("ddMMyyyy");
            LocalDate start = LocalDate.parse(reportDate, dtf);
            LocalDate end = LocalDate.parse(resolvedDate, dtf);
            return ChronoUnit.DAYS.between(start, end);
        } catch (Exception e) {
            return 0;
        }
    }

    public void displaySummary() {
        System.out.println("\n---------------------------------------------------");
        System.out.printf("Issue ID     : %-15s\n", issueID);
        System.out.printf("Facility     : %-15s\n", facilityID);
        System.out.printf("Report Type  : %-15s\n", issueType);
        System.out.println("Description  : " + description);
        System.out.println("Status       : " + status);
        
        if (!assignedTo.isEmpty()) {
            System.out.println("Admin ID     : " + assignedTo);
        }
        
        if (status.equals(Constants.MAINT_RESOLVED)) {
            System.out.println("Resolved On  : " + resolvedDate);
            System.out.println("Repair Time  : " + getDaysToResolve() + " day(s)");
        }
        System.out.println("---------------------------------------------------");
    }
}