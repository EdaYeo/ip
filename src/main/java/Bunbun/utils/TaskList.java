package Bunbun.utils;

import Bunbun.exceptions.BunbunException;
import Bunbun.exceptions.InvalidDateFormatException;
import Bunbun.exceptions.InvalidFindFormatException;
import Bunbun.exceptions.InvalidTaskFormatException;
import Bunbun.exceptions.MissingTaskException;
import Bunbun.exceptions.TaskNumOutOfBoundsException;
import Bunbun.tasks.Deadline;
import Bunbun.tasks.Event;
import Bunbun.tasks.Task;
import Bunbun.tasks.ToDo;

import java.util.ArrayList;
import java.time.LocalDate;


/**
 * This class implements a task list.
 *
 * @author Eda Yeo
 * @version CS2103T AY24/25 Semester 1
 */
public class TaskList {
    private ArrayList<Task> taskList;
    private Ui ui;
    private int numOfTasks;


    /**
     * Instantiates a task list, keeping track of the Storage
     * for the task list and the UI to print out results.
     *
     * @param s Storage to store persistent data from task list.
     * @param ui UI to print out results of methods.
     */
    public TaskList(Storage s, Ui ui) {
        this.taskList = s.toArrayList();
        this.ui = ui;
        this.numOfTasks = this.taskList.size();
    }

    /**
     * Returns number of tasks in task list.
     *
     * @return int of number of tasks in task list.
     */
    public int getNumOfTasks() {
        return this.numOfTasks;
    }

    /**
     * Returns the task at index i in the task list ArrayList.
     *
     * @param i int index of task to be returned.
     * @return Task at index i of TaskList ArrayList.
     */
    public Task getTaskByIndex(int i) {
        return this.taskList.get(i);
    }

    /**
     * Adds task to the task list.
     *
     * @param task task to be added.
     */
    public String addTask(Task task) {
        this.taskList.add(task);
        this.numOfTasks += 1;
        String res = "Added \'" + task + "\' task!\n";
        res += String.format("By the way, you have %d tasks now!", this.numOfTasks);
        return this.ui.response(res);
    }

    /**
     * Adds To Do task to the task list from an array list of tokens specifying the task.
     *
     * @param tokens ArrayList with Strings specifying the task.
     * @throws MissingTaskException if the command specifying the task is invalid due to
     * no task specified.
     */
    public String addToDo(ArrayList<String> tokens) throws MissingTaskException {
        if (tokens.size() == 1) {
            throw new MissingTaskException("Failed. Specify a task for your todo!!!! D:");
        } else {
            String taskDescription = "";
            int len = tokens.size();
            for (int i = 1; i < len; i++) {
                taskDescription += tokens.get(i) + " ";
            }
            ToDo todo = new ToDo(taskDescription);
            return this.addTask(todo);
        }
    }

    /**
     * Adds Deadline task to the task list from an array list of tokens specifying the task.
     *
     * @param tokens ArrayList with Strings specifying the task.
     * @throws BunbunException if the command specifying the task is invalid.
     */
    public String addDeadline(ArrayList<String> tokens) throws BunbunException {
        if (tokens.size() == 1 || tokens.get(1).equals("/by")) {
            throw new MissingTaskException("Failed. Specify a task for your deadline task!!!! D:");
        } else if (!(tokens.contains("/by")) || tokens.indexOf("/by") == tokens.size() - 1) {
            throw new InvalidTaskFormatException(
                    "Failed. Add /by [DATE] to specify when to complete your task by!!! ;=;");
        } else {
            String taskDescription = "";
            LocalDate deadline = null;
            int len = tokens.size();
            Boolean failed = false;
            for (int i = 1; i < len; i++) {
                if (tokens.get(i).equals("/by")) {
                    i += 1;
                    deadline = DateTimeHandler.isValidLocalDate(tokens.get(i));
                    if (deadline == null || tokens.size() > i + 1) {
                        failed = true;
                    }
                } else {
                    taskDescription += tokens.get(i) + " ";
                }
            }
            if (!failed) {
                Deadline deadlineTask = new Deadline(taskDescription, deadline);
                return this.addTask(deadlineTask);
            } else {
                throw new InvalidDateFormatException("Failed. Specify your date in yyyy-MM-dd format!! ;^;");
            }
        }
    }

    /**
     * Adds Event task to the task list from an array list of tokens specifying the task.
     *
     * @param tokens ArrayList with Strings specifying the task.
     * @throws BunbunException if the command specifying the task is invalid.
     */
    public String addEvent(ArrayList<String> tokens) throws BunbunException {
        if (tokens.size() == 1 || tokens.get(1).equals("/from") || tokens.get(1).equals("/to")) {
            throw new MissingTaskException("Failed. Specify a task for your event task!!!! D:");
        } else if (!(tokens.contains("/from")) || !(tokens.contains("/to")) ||
                (tokens.indexOf("/from") > tokens.indexOf("/to")) ||
                (tokens.indexOf("/from") + 1 == tokens.indexOf("/to")) ||
                tokens.indexOf("/to") == tokens.size() - 1) {
            throw new InvalidTaskFormatException(
                    "Failed. Add /from [DATE] /to [DATE] to specify the duration of your event!!! ;=;");
        } else {
            String taskDescription = "";
            LocalDate start = null;
            LocalDate end = null;
            int len = tokens.size();
            int i = 1;
            Boolean failed = false;
            while (i < len && !(tokens.get(i).equals("/from"))) {
                taskDescription += tokens.get(i) + " ";
                i += 1;
            }
            i += 1;
            start = DateTimeHandler.isValidLocalDate(tokens.get(i));
            if (start == null || !tokens.get(i + 1).equals("/to")) {
                failed = true;
            } else {
                i += 2;
                end = DateTimeHandler.isValidLocalDate(tokens.get(i));
                if (end == null || tokens.size() > i + 1) {
                    failed = true;
                }
            }

            if (!failed) {
                Event event = new Event(taskDescription, start, end);
                return this.addTask(event);
            } else {
                throw new InvalidDateFormatException("Failed. Specify your date in yyyy-MM-dd format!! ;^;");
            }
        }
    }


    /**
     * Displays task list for user.
     */
    public String displayList() {
        String res = "These are your tasks!\n";
        this.ui.response("These are your tasks!");
        for (int i = 0; i < this.numOfTasks; i++) {
            Task currTask = this.taskList.get(i);
            res += String.format("%d.%s\n", i + 1, currTask);
            this.ui.response(res);
        }
        res += "That's all your tasks for now :>>>";
        return this.ui.response(res);
    }

    /**
     * Signals that a task is complete and marks the description with an X.
     *
     * @param taskNum int to indicate which task to mark as complete.
     */
    public String markDoneTask(int taskNum) throws TaskNumOutOfBoundsException {
        if (taskNum <= 0 || taskNum > this.numOfTasks) {
            throw new TaskNumOutOfBoundsException(
                    String.format("I can't mark task %d cause it doesn't exist!!! ;-;", taskNum));
        } else {
            Task reqTask = this.taskList.get(taskNum - 1);
            reqTask.complete();
            String res = "Oki, I'll mark the task as done *w*! Good job finishing the task!!\n";
            res += String.format("%s", reqTask);
            return this.ui.response(res);
        }
    }

    /**
     * Deletes a task from the list
     *
     * @param taskNum int to indicate which task to delete.
     */
    public String deleteTask(int taskNum) throws TaskNumOutOfBoundsException {
        if (taskNum <= 0 || taskNum > this.numOfTasks) {
            throw new TaskNumOutOfBoundsException(
                    String.format("I can't delete task %d cause it doesn't exist!!! ;-;", taskNum));
        } else {
            String res = String.format("Oki, I've deleted %s task!\n", this.taskList.get(taskNum - 1));
            this.taskList.remove(taskNum - 1);
            this.numOfTasks -= 1;
            res += String.format("You have %d tasks left!!", this.numOfTasks);
            return this.ui.response(res);
        }
    }

    /**
     * Displays tasks in task list that contain word searched by user.
     *
     * @param tokens ArrayList with Strings specifying the word to search for
     */
    public String searchAndDisplay(ArrayList<String> tokens) throws BunbunException {
        if (tokens.size() == 1) {
            throw new InvalidFindFormatException("Failed. Key in word to find!! :<");
        }

        String word = "";
        for (int i = 1; i < tokens.size(); i++) {
            word += tokens.get(i);
        }
        this.ui.response(String.format("These are the tasks containing %s!", word));
        ArrayList<Task> tasksContainingWord = new ArrayList<>();

        for (int i = 0; i < this.numOfTasks; i++) {
            Task currTask = this.taskList.get(i);
            String currTaskDescription = currTask.toString();
            if (currTaskDescription.contains(word)) {
                tasksContainingWord.add(currTask);
            }
        }

        String res = "";
        for (int i = 0; i < tasksContainingWord.size(); i++) {
            Task currTask = tasksContainingWord.get(i);
            res += String.format("%d.%s\n", i + 1, currTask);
        }
        return this.ui.response(res);
    }

}
