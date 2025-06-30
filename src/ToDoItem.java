

import java.io.Serializable; 

public class ToDoItem implements Serializable {
	private String description;
	private boolean done;
	private int duration; //in minutes (optional)
	
	public ToDoItem(String description) {
		this(description, 0); // default duration is 0 cuz optional
	}
	
	//constructior
	public ToDoItem(String description, int duration) {
		this.description = description;
		this.done = false;
		this.duration = duration;
	}
	
	//getters and setters
	public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

	public boolean isDone() {
		return done;
	}
	public void setDone(boolean done) {
		this.done = done;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	
	@Override 
	public String toString() {
		StringBuilder display = new StringBuilder(); //StringBuilder allows modifications without creating new objects, unlike string which is immutable
		display.append(done ? "[✓] " : "").append(description); //ternary operator, if done then tick else nothing before description
		if (duration > 0) {
			display.append(" (").append(duration).append(" min)");
		}
		return display.toString();
	}
	
}
