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
    private ArrayList<Task> taskList;

    public TodoList() {
        taskList = new ArrayList<>();
    }

    public void addTask(String title, String project, LocalDate dueDate) {
        this.taskList.add(new Task(title, project, dueDate, "MEDIUM"));
    }

    public boolean readTaskFromUser() {
        @SuppressWarnings("resource")
        Scanner scan = new Scanner(System.in);

        try {
            System.out.println(Messages.GREEN_TEXT + "Please enter the following details to add a task:" + Messages.RESET_TEXT);
            System.out.print(">>> Task Title  : ");
            String title = scan.nextLine();
            System.out.print(">>> Project Name: ");
            String project = scan.nextLine();

            LocalDate dueDate = null;
            while (true) {
                System.out.print(">>> Due Date [example: 2019-12-31] : ");
                String dateInput = scan.nextLine();
                try {
                    dueDate = LocalDate.parse(dateInput);
                    if (dueDate.isBefore(LocalDate.now())) {
                        System.out.println("Please enter a future date.");
                    } else {
                        break;
                    }
                } catch (Exception e) {
                    System.out.println("Invalid date format. Please use yyyy-mm-dd.");
                }
            }

            System.out.print(">>> Priority (HIGH / MEDIUM / LOW): ");
            String priority = scan.nextLine().trim().toUpperCase();

            if (priority.isEmpty() ||
                !(priority.equals("HIGH") || priority.equals("MEDIUM") || priority.equals("LOW"))) {
                priority = "MEDIUM";
            }

            System.out.print(">>> Notes (optional): ");
            String notes = scan.nextLine();

            Task newTask = new Task(title, project, dueDate, priority);
            newTask.setNotes(notes);
            this.taskList.add(newTask);

            Messages.showMessage("Task is added successfully with priority: " + priority, false);
            return true;

        } catch (Exception e) {
            Messages.showMessage(e.getMessage(), true);
            return false;
        }
    }

    public boolean readTaskFromUserToUpdate(Task task) {
        Scanner scan = new Scanner(System.in);
        boolean isTaskUpdated = false;

        try {
            System.out.println(Messages.GREEN_TEXT + "Please enter the following details to update a task:"
                    + "\nIf you do not want to change any field, just press ENTER key!" + Messages.RESET_TEXT);
            System.out.print(">>> Task Title  : ");
            String title = scan.nextLine();
            if (!(title.trim().equals("") || title == null)) {
                task.setTitle(title);
                isTaskUpdated = true;
            }

            System.out.print(">>> Project Name: ");
            String project = scan.nextLine();
            if (!(project.trim().equals("") || project == null)) {
                task.setProject(project);
                isTaskUpdated = true;
            }

            System.out.print(">>> Due Date [example: 2019-12-31] : ");
            String dueDate = scan.nextLine();
            if (!(dueDate.trim().equals("") || dueDate == null)) {
                task.setDueDate(LocalDate.parse(dueDate));
                isTaskUpdated = true;
            }

            System.out.print(">>> Notes (leave blank to keep current): ");
            String notes = scan.nextLine();
            if (!(notes.trim().equals("") || notes == null)) {
                task.setNotes(notes);
                isTaskUpdated = true;
            }

            Messages.showMessage("Task is " + (isTaskUpdated ? "updated successfully" : "NOT modified") + ": Returning to Main Menu", false);
            return true;
        } catch (Exception e) {
            Messages.showMessage(e.getMessage(), true);
            return false;
        }
    }

    public void listAllTasksWithIndex() {
        String displayFormat = "%-4s %-25s %-20s %-10s %-12s %-15s %-10s";

        if (taskList.size() > 0) {
            System.out.println(String.format(displayFormat,
                    "NUM", "TITLE", "PROJECT", "PRIORITY", "DUE DATE", "DAYS LEFT", "COMPLETED"));
            Messages.separator('=', 100);
        } else {
            System.out.println(Messages.RED_TEXT + "No tasks to show" + Messages.RESET_TEXT);
            return;
        }

        LocalDate today = LocalDate.now();
        for (int i = 0; i < taskList.size(); i++) {
            Task task = taskList.get(i);
            LocalDate due = task.getDueDate();
            String dueStr = (due == null) ? "-" : due.toString();
            long daysTillDue = (due == null) ? 0 : ChronoUnit.DAYS.between(today, due);

            String overdueText = "";
            if (due != null && !task.isComplete() && due.isBefore(LocalDate.now())) {
                overdueText = Messages.RED_TEXT + "OVERDUE" + Messages.RESET_TEXT;
            }

            String priorityColor = "";
            switch (task.getPriority().toString()) {
                case "HIGH": priorityColor = Messages.RED_TEXT; break;
                case "MEDIUM": priorityColor = Messages.GREEN_TEXT; break;
                case "LOW": priorityColor = "\u001B[33m"; break;
            }
            String resetColor = Messages.RESET_TEXT;

            System.out.println(String.format(displayFormat,
                (i + 1),
                task.getTitle(),
                task.getProject(),
                priorityColor + task.getPriority() + resetColor,
                dueStr,
                daysTillDue + (overdueText.isEmpty() ? "" : " " + overdueText),
                (task.isComplete() ? "YES" : "NO")
            ));
        }
    }

    public void listAllTasks(String sortBy) {
        Messages.separator('=', 75);
        System.out.println("Total Tasks = " + taskList.size() +
                "\t\t (Completed = " + completedCount() + "\t\t" +
                Messages.RED_TEXT + " Not Completed = " + notCompletedCount() + Messages.RESET_TEXT + " )");
        Messages.separator('=', 75);

        LocalDate today = LocalDate.now();

        if (sortBy.equals("2")) {
            String displayFormat = "%-20s %-30s %-25s %-10s";

            if (taskList.size() > 0) {
                System.out.println(String.format(displayFormat, "PROJECT", "TITLE", "DUE DATE", "STATUS"));
                Messages.separator('=', 90);
            } else {
                System.out.println(Messages.RED_TEXT + "No tasks to show" + Messages.RESET_TEXT);
            }

            taskList.stream()
                    .sorted(Comparator.comparing(Task::getProject, Comparator.nullsLast(String::compareTo)))
                    .forEach(task -> {
                        String status = task.isComplete() ? "YES" : "NO";
                        String overdue = "";
                        LocalDate d = task.getDueDate();
                        if (d != null && !task.isComplete() && d.isBefore(LocalDate.now())) {
                            overdue = " " + "\u001B[1;31mOVERDUE\u001B[0m";
                        }
                        String dueDisplay = (d == null) ? "-" : d.toString();
                        System.out.println(String.format(displayFormat,
                                task.getProject(),
                                task.getTitle(),
                                dueDisplay + overdue,
                                status
                        ));
                    });
        } else {
            String displayFormat = "%-12s %-25s %-20s %-10s %-15s %-10s";
            if (taskList.size() > 0) {
                System.out.println(String.format(displayFormat,
                        "DUE DATE", "TITLE", "PROJECT", "PRIORITY", "DAYS LEFT", "COMPLETED"));
                Messages.separator('=', 95);
            } else {
                System.out.println(Messages.RED_TEXT + "No tasks to show" + Messages.RESET_TEXT);
            }

            taskList.stream()
                    .sorted(Comparator.comparing(Task::getDueDate))
                    .forEach(task -> {
                        LocalDate due = task.getDueDate();
                        String dueStr = (due == null) ? "-" : due.toString();
                        long daysTillDue = (due == null) ? 0 : ChronoUnit.DAYS.between(today, due);
                        String priorityColor = "";
                        switch (task.getPriority().toString()) {
                            case "HIGH": priorityColor = Messages.RED_TEXT; break;
                            case "MEDIUM": priorityColor = Messages.GREEN_TEXT; break;
                            case "LOW": priorityColor = "\u001B[33m"; break;
                        }
                        String resetColor = Messages.RESET_TEXT;

                        System.out.println(String.format(displayFormat,
                                dueStr,
                                task.getTitle(),
                                task.getProject(),
                                priorityColor + task.getPriority() + resetColor,
                                daysTillDue,
                                (task.isComplete() ? "YES" : "NO")
                        ));
                    });
        }
    }

    public void editTask(String selectedTask) throws NullPointerException {
        try {
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
            @SuppressWarnings("resource")
            Scanner scan = new Scanner(System.in);
            String editChoice = scan.nextLine();
            switch (editChoice) {
                case "1" -> readTaskFromUserToUpdate(task);
                case "2" -> {
                    task.markCompleted();
                    Messages.showMessage("Task Num " + selectedTask + " is marked as Completed: Returning to Main Menu", false);
                }
                case "3" -> {
                    taskList.remove(task);
                    Messages.showMessage("Task Num " + selectedTask + " is Deleted: Returning to Main Menu", true);
                }
                default -> Messages.showMessage("Returning to Main Menu", true);
            }
        } catch (Exception e) {
            Messages.showMessage(e.getMessage(), true);
        }
    }

    public int completedCount() {
        return (int) taskList.stream().filter(Task::isComplete).count();
    }

    public int notCompletedCount() {
        return (int) taskList.stream().filter(task -> !task.isComplete()).count();
    }

    public boolean readFromFile(String filename) {
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
            Messages.showMessage(e.getMessage(), true);
            return false;
        }
    }

    public boolean saveToFile(String filename) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(filename);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

            objectOutputStream.writeObject(taskList);

            objectOutputStream.close();
            fileOutputStream.close();
            return true;

        } catch (Exception e) {
            Messages.showMessage(e.getMessage(), true);
            return false;
        }
    }

    public void showSortMenu() {
        System.out.println("\n=== Sort Tasks Menu ===");
        System.out.println("1. Sort by Due Date");
        System.out.println("2. Sort by Priority");
        System.out.println("3. Sort by Completion Status");
        System.out.print(">>> Enter your choice: ");
        
        @SuppressWarnings("resource")
        Scanner scan = new Scanner(System.in);
        String choice = scan.nextLine();

        switch (choice) {
            case "1" -> sortByDueDate();
            case "2" -> sortByPriority();
            case "3" -> sortByCompletionStatus();
            default -> System.out.println(Messages.RED_TEXT + "Invalid choice!" + Messages.RESET_TEXT);
        }
    }

    public void sortByDueDate() {
        taskList.sort(Comparator.comparing(Task::getDueDate, Comparator.nullsLast(Comparator.naturalOrder())));
        Messages.showMessage("Tasks have been sorted by due date!", false);
        listAllTasksWithIndex();
    }

    public void sortByPriority() {
        taskList.sort(Comparator.comparing(Task::getPriority, Comparator.nullsLast(String::compareToIgnoreCase)));
        Messages.showMessage("Tasks have been sorted by priority!", false);
        listAllTasksWithIndex();
    }

    public void sortByCompletionStatus() {
        taskList.sort(Comparator.comparing(Task::isComplete));
        Messages.showMessage("Tasks have been sorted by completion status!", false);
        listAllTasksWithIndex();
    }

    // 🔍 NEW METHOD ADDED BELOW
    public void searchTask() {
        @SuppressWarnings("resource")
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter a keyword to search by title or project: ");
        String keyword = scan.nextLine().trim().toLowerCase();

        System.out.println("\nSearch Results:");
        Messages.separator('=', 70);

        boolean found = false;
        for (Task task : taskList) {
            if (task.getTitle().toLowerCase().contains(keyword) || 
                task.getProject().toLowerCase().contains(keyword)) {
                System.out.println(task.formattedStringOfTask());
                found = true;
            }
        }

        if (!found) {
            System.out.println(Messages.RED_TEXT + "No matching tasks found!" + Messages.RESET_TEXT);
        }
        Messages.separator('=', 70);
    }
}
