

import java.time.LocalDateTime;
import java.time.ZoneId;
import javax.swing.*;

import java.awt.*;
import java.util.Date;

public class TaskUIManager {
	public static void setTaskDialog(JDialog dialog, TaskManager taskManager, 
	        DefaultListModel<Task> listModel, boolean isEdit, Task existingTask) {

	    dialog.setLayout(new GridBagLayout());
	    GridBagConstraints gbc = new GridBagConstraints();
	    
	    //name
	    gbc.gridx = 0;
	    gbc.gridy = 0;
	    gbc.anchor = GridBagConstraints.WEST;
	    gbc.insets = new Insets(5, 5, 5, 5);
	    dialog.add(new JLabel("Task Name:"), gbc);
	    gbc.gridx = 1;
	    gbc.fill = GridBagConstraints.HORIZONTAL;
	    JTextField nameField = new JTextField(20);
	    dialog.add(nameField, gbc);
	    
	    //description
	    gbc.gridx = 0;
	    gbc.gridy = 1;
	    dialog.add(new JLabel("Description:"), gbc);
	    gbc.gridx = 1;
	    JTextField descField = new JTextField(20);
	    dialog.add(descField, gbc);
	    
	    //due Date (using JSpinner instead of JDateChooser)
	    gbc.gridx = 0;
	    gbc.gridy = 2;
	    dialog.add(new JLabel("Due Date:"), gbc);
	    gbc.gridx = 1;
	    SpinnerDateModel dateModel = new SpinnerDateModel();
	    JSpinner dateSpinner = new JSpinner(dateModel);
	    JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
	    dateSpinner.setEditor(dateEditor);
	    dialog.add(dateSpinner, gbc);
	    
	    // Priority
	    gbc.gridx = 0;
	    gbc.gridy = 3;
	    dialog.add(new JLabel("Priority:"), gbc);
	    gbc.gridx = 1;
	    JComboBox<Task.Priority> priorityBox = new JComboBox<>(Task.Priority.values());
	    dialog.add(priorityBox, gbc);
	    
	    // Category
	    gbc.gridx = 0;
	    gbc.gridy = 4;
	    dialog.add(new JLabel("Category:"), gbc);
	    gbc.gridx = 1;
	    JComboBox<Task.Category> categoryBox = new JComboBox<>(Task.Category.values());
	    dialog.add(categoryBox, gbc);

	    // Buttons
	    JPanel buttonPanel = new JPanel();
	    JButton saveButton = new JButton(isEdit ? "Update" : "Save");
	    JButton cancelButton = new JButton("Cancel");
	    
	    saveButton.addActionListener(e -> {
	        try {
	            String name = nameField.getText();
	            String description = descField.getText();
	            
	            Date selectedDate = (Date) dateSpinner.getValue();
	            LocalDateTime dueDate = LocalDateTime.ofInstant(
	                selectedDate.toInstant(), 
	                ZoneId.systemDefault()
	            );

	            if (name.trim().isEmpty()) {
	                JOptionPane.showMessageDialog(dialog, 
	                    "Please enter a task name", 
	                    "Error", 
	                    JOptionPane.ERROR_MESSAGE);
	                return;
	            }

	            Task.Priority priority = (Task.Priority) priorityBox.getSelectedItem();
	            Task.Category category = (Task.Category) categoryBox.getSelectedItem();

	            if (isEdit && existingTask != null) {
	                existingTask.setName(name);
	                existingTask.setDescription(description);
	                existingTask.setDueDate(dueDate);
	                existingTask.setPriority(priority);
	                existingTask.setCategory(category);
	            } else {
	                Task newTask = new Task(name, description, dueDate, 0, priority, category);
	                taskManager.addTask(newTask);
	            }
	            taskManager.saveTasks();
	            refreshTaskList(listModel, taskManager);
	            dialog.dispose();
	        } catch (Exception ex) {
	            JOptionPane.showMessageDialog(dialog, 
	                "Error saving task: " + ex.getMessage(), 
	                "Error", 
	                JOptionPane.ERROR_MESSAGE);
	        }
	    });

	    cancelButton.addActionListener(e -> dialog.dispose());
	    buttonPanel.add(saveButton);
	    buttonPanel.add(cancelButton);
	    
	    gbc.gridx = 0;
	    gbc.gridy = 5;
	    gbc.gridwidth = 2;
	    gbc.anchor = GridBagConstraints.CENTER;
	    dialog.add(buttonPanel, gbc);
	    
	    dialog.pack();
	    dialog.setLocationRelativeTo(null);
	    dialog.setVisible(true);
	}

	private static void refreshTaskList(DefaultListModel<Task> listModel, TaskManager taskManager) {
	    listModel.clear();
	    for (Task task : taskManager.getTasks()) {
	        listModel.addElement(task);
	    }
	}
  

}
