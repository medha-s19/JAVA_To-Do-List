package org.sda.todolist;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.Scanner;
import java.time.temporal.ChronoUnit;

/**
 * This class represents ToDoList which contains the ArrayList of Task objects
 *
 * @author  Imtiaz
 * @version 1.0
 * @since   2019-10-11
 **/

public class TodoList {
    // An array list of task objects
    private ArrayList<Task> taskList;
    // single Scanner to read from System.in (do not close System.in)
    private final Scanner scanner = new Scanner(System.in);

    /**
     * creating an TodoList object
     */
    public TodoList() {
        taskList = new ArrayList<>();
    }

    /**
     * Adding a Task object in ArrayList
     * @param title A String that holds the title of a task and it cannot be empty or null.
     * @param project A String that holds the name of project associated with task, and it could be an empty string.
     * @param dueDate The due date of the task as yyyy-mm-dd format
     */
    public boolean addTask(String title, String project, LocalDate dueDate) {
        // Normalize title for comparison
        String titleNorm = (title == null) ? "" : title.trim().toLowerCase();

        // Check for duplicate task (same title ignoring case/spaces and same due date)
        boolean exists = this.taskList.stream().anyMatch(t -> {
            String tTitle = (t.getTitle() == null) ? "" : t.getTitle().trim().toLowerCase();

            boolean titleMatches = !titleNorm.isEmpty() && titleNorm.equals(tTitle);

            boolean dateMatches;
            if (t.getDueDate() == null && dueDate == null) dateMatches = true;
            else if (t.getDueDate() == null || dueDate == null) dateMatches = false;
            else dateMatches = t.getDueDate().toString().equals(dueDate.toString());

            return titleMatches && dateMatches;
        });

        if (exists) {
            Messages.showMessage("Task already exists!", true);
            return false;
        }

        this.taskList.add(new Task(title, project, dueDate));
        return true;
    }

    /**
     * A method to read the value from user (standard input, i.e., terminal)
     * to create a Task object and to add in the ArrayList of Tasks
     * @return true, if the Tasks object is created and added to ArrayList, otherwise false
     */
    public boolean readTaskFromUser() {
        try {
            System.out.println(Messages.GREEN_TEXT + "Please enter the following details to add a task:" + Messages.RESET_TEXT);
            System.out.print(">>> Task Title  : ");
            String title = this.scanner.nextLine();
            System.out.print(">>> Project Name: ");
            String project = this.scanner.nextLine();
            System.out.print(">>> Due Date [example: 2019-12-31] : ");
            LocalDate dueDate = LocalDate.parse(this.scanner.nextLine());

            // Use addTask to benefit from duplicate checking and consistent construction
            boolean added = this.addTask(title, project, dueDate);
            if (added) {
                Messages.showMessage("Task is added successfully", false);
            }

            return true;
        } catch (Exception e) {
            Messages.showMessage(e.getMessage(),true);
            return false;
        }

    }

    /**
     * A method to read the value from user (standard input, i.e., terminal)
     * and update the given Task object in the ArrayList of Tasks
     * @param task the task object whose value need to be updated with user input
     * @return true, if the Tasks object is updated in ArrayList, otherwise false
     */
    public boolean readTaskFromUserToUpdate(Task task) {
        boolean isTaskUpdated = false;

        try {
            System.out.println(Messages.GREEN_TEXT + "Please enter the following details to update a task:"
                    + "\nIf you do not want to change any field, just press ENTER key!" + Messages.RESET_TEXT);
            System.out.print(">>> Task Title  : ");
            String title = this.scanner.nextLine();
            if (!(title.trim().equals("") || title == null)) {
                task.setTitle(title);
                isTaskUpdated = true;
            }

            System.out.print(">>> Project Name: ");
            String project = this.scanner.nextLine();
            if (!(project.trim().equals("") || project == null)) {
                task.setProject(project);
                isTaskUpdated = true;
            }

            System.out.print(">>> Due Date [example: 2019-12-31] : ");
            String dueDate = this.scanner.nextLine();
            if (!(dueDate.trim().equals("") || dueDate == null)) {
                task.setDueDate(LocalDate.parse(dueDate));
                isTaskUpdated = true;
            }

            Messages.showMessage("Task is " + (isTaskUpdated ? "updated successfully" : "NOT modified") + ": Returning to Main Menu", false);

            return true;
        } catch (Exception e) {
            Messages.showMessage(e.getMessage(), true);
            return false;
        }
    }

    /**
     * A method to display the contents of ArrayList with first column as task number
     */
    public void listAllTasksWithIndex() {
    String displayFormat = "%-4s %-30s %-20s %-12s %-15s %-10s";
        // NUM, TITLE, PROJECT, DUE DATE, DAYS TILL DUE, COMPLETED

        if (taskList.size() > 0) {
            System.out.println(String.format(displayFormat, "NUM", "TITLE", "PROJECT", "DUE DATE", "DAYS TILL DUE", "COMPLETED"));
            Messages.separator('=', 90);
        } else {
            System.out.println(Messages.RED_TEXT + "No tasks to show" + Messages.RESET_TEXT);
        }

        java.time.LocalDate today = java.time.LocalDate.now();

        // Sort tasks by due date before displaying (tasks without due date go last)
        Collections.sort(taskList, new Comparator<Task>() {
            @Override
            public int compare(Task t1, Task t2) {
                if (t1.getDueDate() == null && t2.getDueDate() == null) return 0;
                if (t1.getDueDate() == null) return 1;  // tasks without due date go last
                if (t2.getDueDate() == null) return -1;
                return t1.getDueDate().compareTo(t2.getDueDate());
            }
        });

        taskList.stream()
            .forEach(task -> {
                java.time.LocalDate due = task.getDueDate();
                String dueStr = (due == null) ? "-" : due.toString();
                long daysTillDue = (due == null) ? 0 : ChronoUnit.DAYS.between(today, due);
                System.out.println(String.format(displayFormat,
                    taskList.indexOf(task) + 1,
                    task.getTitle(),
                    task.getProject(),
                    dueStr,
                    daysTillDue,
                    (task.isComplete() ? "YES" : "NO")
                ));
            });
    }

    /**
     * A method to display the contents of ArrayList
     * @param sortBy a string holding a number, "2" for sorting by project, otherwise it will sorty by date
     */
    public void listAllTasks(String sortBy) {
        Messages.separator('=',75);
        System.out.println(
                "Total Tasks = " + taskList.size() +
                        "\t\t (Completed = " + completedCount() + "\t\t" +
                        Messages.RED_TEXT + " Not Compeleted = " + notCompletedCount() + Messages.RESET_TEXT +
                        " )");
        Messages.separator('=',75);

        java.time.LocalDate today = java.time.LocalDate.now();

        if (sortBy.equals("2")) {
            String displayFormat = "%-20s %-30s %-12s %-15s %-10s";

            if (taskList.size() > 0) {
                System.out.println(String.format(displayFormat, "PROJECT", "TITLE", "DUE DATE", "DAYS TILL DUE", "COMPLETED"));
                Messages.separator('=', 90);
            } else {
                System.out.println(Messages.RED_TEXT + "No tasks to show" + Messages.RESET_TEXT);
            }

            taskList.stream()
                .sorted(Comparator.comparing(Task::getProject, Comparator.nullsLast(String::compareTo)))
                .forEach(task -> {
                    java.time.LocalDate due = task.getDueDate();
                    String dueStr = (due == null) ? "-" : due.toString();
                    long daysTillDue = (due == null) ? 0 : ChronoUnit.DAYS.between(today, due);
                    System.out.println(String.format(displayFormat,
                        task.getProject(),
                        task.getTitle(),
                        dueStr,
                        daysTillDue,
                        (task.isComplete() ? "YES" : "NO")
                    ));
                });
        } else {
            String displayFormat = "%-12s %-30s %-20s %-15s %-10s";

            if (taskList.size() > 0) {
                System.out.println(String.format(displayFormat, "DUE DATE", "TITLE", "PROJECT", "DAYS TILL DUE", "COMPLETED"));
                Messages.separator('=', 90);
            } else {
                System.out.println(Messages.RED_TEXT + "No tasks to show" + Messages.RESET_TEXT);
            }

            taskList.stream()
                .sorted(Comparator.comparing(Task::getDueDate))
                .forEach(task -> {
                    java.time.LocalDate due = task.getDueDate();
                    String dueStr = (due == null) ? "-" : due.toString();
                    long daysTillDue = (due == null) ? 0 : ChronoUnit.DAYS.between(today, due);
                    System.out.println(String.format(displayFormat,
                        dueStr,
                        task.getTitle(),
                        task.getProject(),
                        daysTillDue,
                        (task.isComplete() ? "YES" : "NO")
                    ));
                });
        }
    }

    /**
     * A method to select a particular Task object from ArrayList and perform editing operations
     * @param selectedTask Task number that is selected by user from given list to perform editing operations
     * @throws NullPointerException if task number of given as empty string or null
     * @throws ArrayIndexOutOfBoundsException if task number does not fall in index range of ArrayList
     */
    public void editTask(String selectedTask) throws NullPointerException {
        try {
            // checking if the task number is given and empty string or null
            if (selectedTask.trim().equals("") || selectedTask == null) {
                throw new NullPointerException("EMPTY/NULL TASK NUM: Returning to Main Menu");
            }

            int taskIndex = Integer.parseInt(selectedTask) - 1;
            if (taskIndex < 0 || taskIndex > taskList.size()) {
                throw new ArrayIndexOutOfBoundsException("TASK NUM NOT GIVEN FROM TASK LIST: Returning to Main Menu");
            }

            Task task = taskList.get(taskIndex);

            Messages.showMessage("Task Num " + selectedTask + "  is selected:" + task.formattedStringOfTask(), false);

            Messages.editTaskMenu();
            String editChoice = this.scanner.nextLine();
            switch (editChoice) {
                case "1":
                    readTaskFromUserToUpdate(task);
                    break;
                case "2":
                    task.markCompleted();
                    Messages.showMessage("Task Num " + selectedTask + " is marked as Completed: Returning to Main Menu", false);
                    break;
                case "3":
                    taskList.remove(task);
                    Messages.showMessage("Task Num " + selectedTask + " is Deleted: Returning to Main Menu", true);
                    break;
                default:
                    Messages.showMessage("Returning to Main Menu", true);
            }
        } catch (Exception e) {
            Messages.showMessage(e.getMessage(),true);
        }
    }

    /**
     * A method to count the number of tasks with completed status
     * @return number of tasks with completed status
     */
    public int completedCount() {
        return (int) taskList.stream()
                .filter(Task::isComplete)
                .count();
    }

    /**
     * A method to count the number of tasks with incomplete status
     * @return number of tasks with incomplete status
     */
    public int notCompletedCount() {
        return (int) taskList.stream()
                .filter(task -> !task.isComplete())
                .count();
    }

    /**
     * This method will read the data file from disk which will contain the data of previously saved tasks
     * @param filename a string specifying the full path and extension of data file, for example,  "resources/tasks.obj"
     * @return true if the reading operation was successful, otherwise false
     */
    public boolean readFromFile(String filename) {
    // boolean status not used

        try {
            if (!Files.isReadable(Paths.get(filename))) {
                Messages.showMessage("The data file, i.e., " + filename + " does not exists", true);
                return false;
            }

            FileInputStream fileInputStream = new FileInputStream(filename);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

            taskList = (ArrayList<Task>) objectInputStream.readObject();

            objectInputStream.close();
            fileInputStream.close();
            return true;

        } catch (Exception e) {
            Messages.showMessage(e.getMessage(),true);
            return false;
        }
    }

    /**
     * This method will write the data of Tasks from ArrayList to data file on disk, i.e., tasks.obj
     * @param filename a string specifying the full path and extension of data file, for example,  "resources/tasks.obj"
     * @return true if the reading operation was successful, otherwise false
     */
    public boolean saveToFile(String filename) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(filename);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

            objectOutputStream.writeObject(taskList);

            objectOutputStream.close();
            fileOutputStream.close();
            return true;

        } catch (Exception e) {
            Messages.showMessage(e.getMessage(),true);
            return false;
        }
    }
}