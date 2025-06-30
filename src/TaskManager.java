

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class TaskManager { //manages task data like saving and loading it, 
	                       //listens for and notifies changes to other parts of the program
	private List<Task> tasks;
	private List<Consumer<List<Task>>> taskListeners; //list of consumers used as listeners to do different actions 
	private static final String FILE_PATH = "tasks.ser"; //serialized objects, can be read or written using ObjectOutputStream and ObjectInputStream.

	//constructor
	public TaskManager() {
		tasks = new ArrayList<>();
		taskListeners = new ArrayList<>();
		loadTasks(); //TODO define
	}
	
	public void deleteAllTasks() {
	    tasks.clear();  // Clear the tasks list
	    saveTasks();    // Save the empty state
	}
	
	public void addTask(Task task) {
		tasks.add(task);
		saveTasks(); 
		notifyTaskListeners(); //TODO
	}
	public void removeTask(Task task) {
		tasks.remove(task);
		saveTasks();
		notifyTaskListeners(); //TODO
	}
	 public List<Task> getTasks() {
	        return new ArrayList<>(tasks);
	    }

	//task attributes
	public List<Task> getTasksByCategory(Task.Category category) { 
		return tasks.stream().filter(t -> t.getCategory() == category) //checks if the given category matches the task's category for each task (t) in the stream
					   		 .sorted(Comparator.comparing(Task::getDueDate)) //sorts tasks by comparing their due date //.sorted(Comparator.comparing(Task::getDueDate).reversed()) if we want ascending order
					   		 .collect(Collectors.toList()); //converts stream back to list
	} 
	public List<Task> getTaskByPriority(Task.Priority priority) {
		return tasks.stream().filter(t -> t.getPriority() == priority) //checks if given priority = priority of t
							 .sorted(Comparator.comparing(Task::getDueDate))
							 .collect(Collectors.toList());
	}
	public List<Task> getOverdueTasks() {
		LocalDateTime now = LocalDateTime.now(); 
		return tasks.stream().filter(t -> t.getDueDate() != null && t.getDueDate().isBefore(now) && !t.isCompleted())
				             .collect(Collectors.toList());
	}
	//listener methods
	public void addTaskListener(Consumer<List<Task>> listener) {
		taskListeners.add(listener);
	}
	private void notifyTaskListeners() {
		for (Consumer<List<Task>> lstn: taskListeners) {
			lstn.accept(new ArrayList<>(tasks)); //pass a copy of tasks
		}
	}
	void saveTasks() {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
			oos.writeObject(tasks); //Writes the tasks list (which implements Serializable) to the file
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void loadTasks() {
		if (new File(FILE_PATH).exists()){
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))){
				tasks = (List<Task>) ois.readObject(); //explicitly casts the returned object from ois.readObject() to a List<Task>.
			for (Task task : tasks) {
				if (task.getToDoList() == null) {
					task.setTodoList(new ArrayList<>()); //initialize if null
				 }
			  }
		   }catch (IOException | ClassNotFoundException e) { // | multi-catch operator
			   e.printStackTrace();
			   tasks = new ArrayList<>();//initializes tasks to an empty list 
		   }
			
		}
	}
} 
