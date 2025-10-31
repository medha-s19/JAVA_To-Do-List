package org.sda.todolist;

import java.io.Serializable;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * This is a model class and it represents a Task object
 * and it contains necessary fields and methods to operate
 * on task object.
 *
 * @author Anushka
 * @version 2.0
 **/

public class Task implements Serializable {

    private String title;
    private String project;
    private boolean complete;
    private LocalDate dueDate;

    // ✅ NEW FEATURES
    private String priority;          // Low, Medium, High
    private LocalDate completedDate;  // When task was completed

    /**
     * Creating an object of Task class
     */
    public Task(String title, String project, LocalDate dueDate, String priority) {

        this.setTitle(title);
        this.setProject(project);
        this.complete = false;
        this.setDueDate(dueDate);

        // ✅ new field
        this.setPriority(priority);
        this.completedDate = null;
    }

    // -------- EXISTING METHODS --------

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
        this.completedDate = null; // reset completed date
        return this.complete;
    }

    public boolean markCompleted() {
        this.complete = true;
        this.completedDate = LocalDate.now(); // ✅ store completion date
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

    // -------- NEW PRIORITY METHODS --------

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        if (priority == null || priority.trim().equals("")) {
            this.priority = "Medium"; // Default
        } else {
            this.priority = priority.trim();
        }
    }

    public LocalDate getCompletedDate() {
        return completedDate;
    }

    /**
     * Formatted display for the task
     */
    public String formattedStringOfTask() {
        return (
                "\nTitle          : " + title +
                "\nProject        : " + project +
                "\nPriority       : " + priority +
                "\nStatus         : " + (complete ? "Completed" : "Not Completed") +
                "\nDue Date       : " + dueDate +
                "\nCompleted Date : " + (completedDate == null ? "-" : completedDate) +
                "\n"
        );
    }
}

