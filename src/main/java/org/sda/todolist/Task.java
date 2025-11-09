package org.sda.todolist;

import java.io.Serializable;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Task implements Serializable {

    // Priority levels
    public enum Priority {
        HIGH, MEDIUM, LOW
    }

    private String title;
    private String project;
    private boolean complete;
    private LocalDate dueDate;

    private String priority;          // Low, Medium, High
    private LocalDate completedDate; 
    private String notes; // When task was completed

    /**
     * Main constructor (with priority)
     */
    public Task(String title, String project, LocalDate dueDate, String priority) {
        this.setTitle(title);
        this.setProject(project);
        this.complete = false;
        this.setDueDate(dueDate);
        this.setPriority(priority);
        this.notes = "";
    }

    /**
     * Default constructor for compatibility (priority = Medium)
     */
    public Task(String title, String project, LocalDate dueDate) {
        this(title, project, dueDate, "Medium");
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) throws NullPointerException {
        if (title == null || title.trim().equals("")) {
            throw new NullPointerException("REQUIRED: Title can not be empty.");
        }
        this.title = title.trim();
    }

    public String getProject() {
        return this.project;
    }

    public void setProject(String project) {
        this.project = project.trim();
    }

    public boolean isComplete() {
        return this.complete;
    }

    public boolean markInComplete() {
        this.complete = false;
        this.completedDate = null;
        return this.complete;
    }

    public boolean markCompleted() {
        this.complete = true;
        this.completedDate = LocalDate.now();
        return this.complete;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) throws DateTimeException {
        if (dueDate.compareTo(LocalDate.now()) < 0) {
            throw new DateTimeException("Past Date not allowed");
        }
        DateTimeFormatter formattedDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        this.dueDate = LocalDate.parse(dueDate.format(formattedDate));
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        if (priority == null || priority.trim().equals("")) {
            this.priority = "Medium";
        } else {
            this.priority = priority.trim();
        }
    }

    public LocalDate getCompletedDate() {
        return completedDate;
    }

    public String getNotes() {
    return notes;
}

    public void setNotes(String notes) {

        if (notes == null) {

            this.notes = "";
        } else {
            this.notes = notes.trim();
    }
}


    public String formattedStringOfTask() {
        StringBuilder sb = new StringBuilder();

        sb.append("\nTitle          : ").append(title);
        sb.append("\nProject        : ").append(project);
        sb.append("\nPriority       : ").append(priority);
        sb.append("\nStatus         : ").append(complete ? "Completed" : "Not Completed");
        sb.append("\nDue Date       : ").append(dueDate);
        sb.append("\nCompleted Date : ").append(completedDate == null ? "-" : completedDate);

        if (!complete) {
            long daysBetween = ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
            if (daysBetween >= 0 && daysBetween <= 2) {
                sb.append(Messages.YELLOW_TEXT)
                  .append("\nReminder: due soon!")
                  .append(Messages.RESET_TEXT);
            }
        }

        sb.append("\n");
        return sb.toString();
    }
}