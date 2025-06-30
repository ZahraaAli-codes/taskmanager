

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList; 
import java.util.List;

public class Task implements Serializable { //Serializable used for saving and loading task objects
	 private static final long serialVersionUID = 1L; // ID
	
	//Attributes
	private String name;
	private String description;
	private LocalDateTime dueDate;
	private Priority priority; 
	private Category category; 
	private boolean isRecurring;
	private RecurringInterval recurringInterval; //TODO
	private boolean isCompleted;
	private List<ToDoItem> toDoList; //To-do list for the task
	private int duration;
	
	//enum definitions
	public enum Priority {
		HIGH, MEDIUM, LOW
	}
	public enum Category {
		WORK, PERSONAL, SCHOOL, OTHER
	}
	public enum RecurringInterval {
		DAILY, WEEKLY, MONTHLY, YEARLY, NONE
	}
	
	//Constructor
	public Task(String name, String description, LocalDateTime dueDate, 
			int duration, Priority priority, Category category) {
		this.name = name;
		this.description = description;
		this.dueDate = dueDate;
		this.priority = priority;
		this.category = category;
		this.isRecurring  = false; //temporarily
		this.recurringInterval =recurringInterval.NONE; //temporarily
		this.isCompleted = false;
		this.toDoList = new ArrayList<>(); //initialize an empty to-do list
		
	}
	
	//Getters and Setters
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public LocalDateTime getDueDate() {
		return dueDate;	
	}
	public void setDueDate(LocalDateTime dueDate) {
		this.dueDate = dueDate;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	
	public Priority getPriority() {
		return priority;
	}
	public void setPriority(Priority priority) {
		this.priority = priority;
	}
	public Category getCategory() {
		return category;
	}
	public void setCategory(Category category) {
		this.category = category;
	}
	
	public boolean isRecurring() {
		return isRecurring;
	}
	
	public void setRecurring(boolean recurring) {
		isRecurring = recurring;
	}
	public RecurringInterval getRecurringInterval() {
		return recurringInterval;
	}
	public void setRecurringInterval(RecurringInterval recurringInterval) {
		this.recurringInterval = recurringInterval;
	}
	public boolean isCompleted() {
		return isCompleted;
	}
	public void setCompleted(boolean completed) {
		isCompleted = completed;
	}
	public List<ToDoItem> getToDoList() {
		return toDoList;
	}
	
	//methods for managing to-do list
	public void addToDoItem(ToDoItem item) {
		toDoList.add(item);
	}
	public void removeToDoItem(ToDoItem item) {
		toDoList.remove(item);
	}
	public void setTodoList(List<ToDoItem> toDoList) {
		this.toDoList = toDoList;
	}
	public void markToDoItemDone(ToDoItem item)
	{
		int index = toDoList.indexOf(item); //finds the position of item in to-do list, if item not found, returns -1
	    if (index != -1)
	    {
	    	toDoList.get(index).setDone(true); //marks the item as done if found
	    }
	}

	@Override
	public String toString() {
		return name + "(Due: " + dueDate + ")";
	}
}
