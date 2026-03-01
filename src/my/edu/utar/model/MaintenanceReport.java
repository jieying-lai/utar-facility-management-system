package my.edu.utar.model;

import my.edu.utar.util.Constants;

/**
 * MaintenanceReport.java
 * Represents a facility maintenance/issue report.
 * Member 3 owns this class.
 */
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
        this.assignedTo   = assignedTo != null ? assignedTo : "";
        this.resolvedDate = resolvedDate != null ? resolvedDate : "";
    }

    public MaintenanceReport() {}

    /**
     * Converts to file string for maintenance.txt
     * Format: issueID|facilityID|reporterID|issueType|description|reportDate|status|assignedTo|resolvedDate
     */
    public String toFileString() {
        return issueID + Constants.DELIMITER + facilityID + Constants.DELIMITER +
               reporterID + Constants.DELIMITER + issueType + Constants.DELIMITER +
               description + Constants.DELIMITER + reportDate + Constants.DELIMITER +
               status + Constants.DELIMITER + assignedTo + Constants.DELIMITER + resolvedDate;
    }

    public void display() {
        System.out.println("Issue ID    : " + issueID);
        System.out.println("Facility    : " + facilityID);
        System.out.println("Issue Type  : " + issueType);
        System.out.println("Description : " + description);
        System.out.println("Report Date : " + reportDate);
        System.out.println("Status      : " + status);
        System.out.println("Assigned To : " + (assignedTo.isEmpty() ? "Not yet assigned" : assignedTo));
        System.out.println("Resolved    : " + (resolvedDate.isEmpty() ? "Not yet resolved" : resolvedDate));
    }

    // Getters
    public String getIssueID()      { return issueID; }
    public String getFacilityID()   { return facilityID; }
    public String getReporterID()   { return reporterID; }
    public String getIssueType()    { return issueType; }
    public String getDescription()  { return description; }
    public String getReportDate()   { return reportDate; }
    public String getStatus()       { return status; }
    public String getAssignedTo()   { return assignedTo; }
    public String getResolvedDate() { return resolvedDate; }

    // Setters
    public void setIssueID(String id)          { this.issueID = id; }
    public void setFacilityID(String id)       { this.facilityID = id; }
    public void setReporterID(String id)       { this.reporterID = id; }
    public void setIssueType(String t)         { this.issueType = t; }
    public void setDescription(String d)       { this.description = d; }
    public void setReportDate(String d)        { this.reportDate = d; }
    public void setStatus(String s)            { this.status = s; }
    public void setAssignedTo(String a)        { this.assignedTo = a; }
    public void setResolvedDate(String d)      { this.resolvedDate = d; }
}
