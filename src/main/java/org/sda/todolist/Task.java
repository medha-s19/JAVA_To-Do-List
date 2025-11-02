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
 * @author  Imtiaz
 * @version 1.0
 * @since   2019-10-11
 **/

public class Task implements Serializable {

    // A String that holds the title of a task and it cannot be empty or null.
    private String title;

    // A String that holds the name of project associated with task, and it could be an empty string.
    private String project;

    // A boolean value, if true: the task is completed, otherwise false.
    private boolean complete;

    // The due date of the task as yyyy-mm-dd format
    private LocalDate dueDate;

    /**
     * Creating an object of Task class
     * @param title A String that holds the title of a task and it cannot be empty or null.
     * @param project A String that holds the name of project associated with task, and it could be an empty string.
     * @param dueDate The due date of the task as yyyy-mm-dd format
     */
    public Task(String title, String project, LocalDate dueDate) {
        this.setTitle(title);
        this.setProject(project);
        this.complete = false;
        this.setDueDate(dueDate);
    }

    // Get title
    public String getTitle() {
        return this.title;
    }

    // Set title
    public void setTitle(String title) throws NullPointerException {
        if (title == null || title.trim().equals("")) {
            throw new NullPointerException("REQUIRED: Title can not be empty.");
        }
        this.title = title.trim();
    }

    // Get project name
    public String getProject() {
        return this.project;
    }

    // Set project name
    public void setProject(String project) {
        this.project = project.trim();
    }

    // Get completion status
    public boolean isComplete() {
        return this.complete;
    }

    // Mark incomplete
    public boolean markInComplete() {
        this.complete = false;
        return this.complete;
    }

    // Mark complete
    public boolean markCompleted() {
        this.complete = true;
        return this.complete;
    }

    // Get due date
    public LocalDate getDueDate() {
        return dueDate;
    }

    // Set due date
    public void setDueDate(LocalDate dueDate) throws DateTimeException {
        // Throw DateTimeException if past date is given
        if (dueDate.compareTo(LocalDate.now())<0) {
            throw new DateTimeException("Past Date not allowed");
        }

        //Ensure dueDate is saved as yyyy-MM-dd
        DateTimeFormatter formattedDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        this.dueDate = LocalDate.parse(dueDate.format(formattedDate));
    }

    // Print formatted task info
    public String formattedStringOfTask() {
        return (
                "\nTitle     : " + title +
                "\nProject   : " + project +
                "\nStatus    : " + (complete ? "Completed" : "NOT COMPLETED") +
                "\nDue Date  : " + dueDate +
                "\n"
        );
    }
}


