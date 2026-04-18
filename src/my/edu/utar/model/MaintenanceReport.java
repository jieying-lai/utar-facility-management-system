package my.edu.utar.model;
import my.edu.utar.util.Constants;

public class MaintenanceReport {

    private String issueID;       // Format: M + YYYYMMDD + 4-digit seq e.g. M202603140001
    private String facilityID;
    private String reporterID;    // user ID who reported
    private String issueType;     // from Constants.ISSUE_TYPES
    private String description;
    private String reportDate;    // DDMMYYYY
    private String status;        // Reported / In Progress / Resolved / Closed
    private String assignedTo;    // maintenance personnel name
    private String resolvedDate;  // DDMMYYYY, empty if not resolved
    
    //NEW report constructor (when user submit)
    public MaintenanceReport(String issueID, String facilityID, String reporterID,
            String issueType, String description, String reportDate) {
    	this.issueID      = issueID;
        this.facilityID   = facilityID;
        this.reporterID   = reporterID;
        this.issueType    = issueType;
        this.description  = description;
        this.reportDate   = reportDate;
        this.status       = Constants.MAINT_REPORTED; // After user submit terus jadi "reported" status
        this.assignedTo   = "";
        this.resolvedDate = "";
    }
    
    //Loading from file constructor 
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

    //getter
    public String getIssueID()      { return issueID; }
    public String getFacilityID()   { return facilityID; }
    public String getReporterID()   { return reporterID; }
    public String getIssueType()    { return issueType; }
    public String getDescription()  { return description; }
    public String getReportDate()   { return reportDate; }
    public String getStatus()       { return status; }
    public String getAssignedTo()   { return assignedTo; }
    public String getResolvedDate() { return resolvedDate; }
    //setter
    public void setStatus(String status)            { this.status = status; }
    public void setAssignedTo(String assignedTo)        { this.assignedTo = assignedTo; }
    public void setResolvedDate(String resolvedDate)      { this.resolvedDate = resolvedDate; }
    
    public String toFileString() {
        return issueID + "|" + facilityID + "|" +
               reporterID + "|" + issueType + "|" +
               description + "|" + reportDate + "|" +
               status + "|" + assignedTo + "|" + resolvedDate;
    }

    public void displaySummary() {
        System.out.println("Issue ID    : " + issueID);
        System.out.println("Facility    : " + facilityID);
        System.out.println("Report Type  : " + issueType);
        System.out.println("Description : " + description);
        System.out.println("Report Date : " + reportDate);
        System.out.println("Status      : " + status);
        System.out.println("Assigned To : " + (assignedTo.isEmpty() ? "Unassigned" : assignedTo));
        System.out.println("Resolved    : " + (resolvedDate.isEmpty() ? "Unresolved" : resolvedDate));
    }

    
}
