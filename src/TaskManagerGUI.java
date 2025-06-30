

import javax.swing.*;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TaskManagerGUI extends JFrame {
    private TaskManager taskManager;
    private JList<Task> taskList; //visual list component
    private DefaultListModel<Task> listModel; //Data model for the list

    public TaskManagerGUI() { //constructor
        taskManager = new TaskManager(); // Initialize the task manager
        taskManager.loadTasks(); //ensures tasks are loaded (double - check)
        initializeGUI(); // Set up the gui
        checkForOverdueTasks(); // check for overdue tasks on startup
    }

    private void checkForOverdueTasks() {
        List<Task> overdueTasks = taskManager.getOverdueTasks();
        if (!overdueTasks.isEmpty()) {
            StringBuilder message = new StringBuilder("The following tasks are overdue:\n");
            for (Task task : overdueTasks) {
                message.append("- ").append(task.getName())
                       .append(" (Due: ").append(task.getDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))).append(")\n");
            }
            JOptionPane.showMessageDialog(
                this, //parent component (main window)
                message.toString(),
                "Overdue Tasks", //title of joptionpane
                JOptionPane.WARNING_MESSAGE
            );

            // Optionally mark tasks as completed
            for (Task task : overdueTasks) {
                task.setCompleted(true); 
            }
            taskManager.saveTasks(); // Save updated task status
        }
    }


    private void initializeGUI() {
        setTitle("Time Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1566, 966);
        setLayout(new BorderLayout());

        // Load and set icon ~ doesn't work on mac :( ~
        ImageIcon logo = new ImageIcon(getClass().getResource("/logo.png"));
        if (logo.getImage() == null) {
            System.out.println("Logo image not found!");
        } else {
            setIconImage(logo.getImage());
        }
        

        // Create main panels
        JPanel leftPanel = createLeftPanel();
        JPanel rightPanel = createRightPanel();
        // Set minimum sizes for panels to prevent them from disappearing
        leftPanel.setMinimumSize(new Dimension(300, 0));
        rightPanel.setMinimumSize(new Dimension(300, 0));

        // Add split pane
        JSplitPane splitPane = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setResizeWeight(0.5); //  both sides resize equally
        splitPane.setDividerLocation(0.5); // Start with 50-50 split
        
        // Add a component listener to maintain the equal split when window is resized
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                splitPane.setDividerLocation(0.5);
            }
        }); //makes sure the split pane stays in the middle unless we move it
        
        add(splitPane, BorderLayout.CENTER);

        refreshTaskList();
    }


    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Title for the Tasks panel
        JLabel titleLabel = new JLabel("Tasks", JLabel.CENTER);
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        titleLabel.setBackground(Color.BLACK);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setOpaque(true);
        panel.add(titleLabel, BorderLayout.NORTH);

        // Add filter panel
        JPanel filterPanel = createFilterPanel();
        panel.add(filterPanel, BorderLayout.NORTH);

        // Task list setup
        listModel = new DefaultListModel<>();
        taskList = new JList<>(listModel);
        taskList.setCellRenderer(new TaskListCellRenderer());
        taskList.setBackground(new Color(222, 210, 209));

        JScrollPane scrollPane = new JScrollPane(taskList);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(255, 192, 203), 2));
        taskList.setOpaque(true);

        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Background color 
        panel.setBackground(new Color(222, 210, 209));
        panel.setOpaque(true);

        // Add toolbar for Task operations
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.setBorder(BorderFactory.createLineBorder(new Color(255, 192, 203), 2));

        JButton addButton = new JButton("Add Task");
        JButton editButton = new JButton("Edit Task");
        JButton deleteButton = new JButton("Delete Task");
        JButton deleteAllButton = new JButton("Delete All");

        toolbar.add(addButton);
        toolbar.add(editButton);
        toolbar.add(deleteButton);
        panel.add(toolbar, BorderLayout.SOUTH);
        toolbar.add(deleteAllButton);
        
  
        deleteAllButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete ALL tasks? This cannot be undone.",
                "Confirm Delete All",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                taskManager.deleteAllTasks();  // Use the new method
                listModel.clear();            // Clear the list model
                refreshTaskList();           // Refresh the display
                JOptionPane.showMessageDialog(
                    this,
                    "All tasks have been deleted.",
                    "Tasks Deleted",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
        });

        // Button functionalities
        addButton.addActionListener(e -> showAddTaskDialog());
        editButton.addActionListener(e -> {
            Task selectedTask = taskList.getSelectedValue();
            if (selectedTask != null) {
                showEditTaskDialog();
            }
        });
        deleteButton.addActionListener(e -> {
            Task selectedTask = taskList.getSelectedValue();
            if (selectedTask != null) {
                int confirm = JOptionPane.showConfirmDialog(
                    this, "Are you sure you want to delete this task?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    taskManager.removeTask(selectedTask);
                    refreshTaskList();
                }
            }
        });

        return panel;
    }
    
    private JPanel createFilterPanel() {
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(Color.gray);
        
        // Category filter
        JComboBox<String> categoryFilter = new JComboBox<>(
            new String[]{"All Categories", "WORK", "PERSONAL", "SCHOOL", "OTHER"});
        
        // Priority filter
        JComboBox<String> priorityFilter = new JComboBox<>(
            new String[]{"All Priorities", "HIGH", "MEDIUM", "LOW"});
        
        // Show overdue tasks checkbox
        JCheckBox showOverdueBox = new JCheckBox("Show Overdue Only");
        
        // Add action listeners
        categoryFilter.addActionListener(e -> {
            String selected = (String) categoryFilter.getSelectedItem();
            if ("All Categories".equals(selected)) {
                refreshTaskList();
            } else {
                listModel.clear();
                Task.Category category = Task.Category.valueOf(selected);
                List<Task> filteredTasks = taskManager.getTasksByCategory(category);
                for (Task task : filteredTasks) {
                    listModel.addElement(task);
                }
            }
        });

        priorityFilter.addActionListener(e -> {
            String selected = (String) priorityFilter.getSelectedItem();
            if ("All Priorities".equals(selected)) {
                refreshTaskList();
            } else {
                listModel.clear();
                Task.Priority priority = Task.Priority.valueOf(selected);
                List<Task> filteredTasks = taskManager.getTaskByPriority(priority);
                for (Task task : filteredTasks) {
                    listModel.addElement(task);
                }
            }
        });

        showOverdueBox.addActionListener(e -> {
            if (showOverdueBox.isSelected()) {
                listModel.clear();
                List<Task> overdueTasks = taskManager.getOverdueTasks();
                for (Task task : overdueTasks) {
                    listModel.addElement(task);
                }
            } else {
                refreshTaskList();
            }
        });

        // Add components to filter panel
        filterPanel.add(new JLabel("Category:"));
        filterPanel.add(categoryFilter);
        filterPanel.add(Box.createHorizontalStrut(10));
        filterPanel.add(new JLabel("Priority:"));
        filterPanel.add(priorityFilter);
        filterPanel.add(Box.createHorizontalStrut(10));
        filterPanel.add(showOverdueBox);

        return filterPanel;
    }


    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Title for the To-Do panel
        JLabel titleLabel = new JLabel("To-Do", JLabel.CENTER);
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding around the label
        titleLabel.setBackground(Color.BLACK);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setOpaque(true);
        panel.add(titleLabel, BorderLayout.NORTH);

        // Create a list model and JList for To-Do Items
        DefaultListModel<ToDoItem> toDoListModel = new DefaultListModel<>();
        JList<ToDoItem> todoList = new JList<>(toDoListModel);
        todoList.setFont(new Font("Arial", Font.PLAIN, 14));

        // Custom cell renderer to show done items in green
        todoList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value instanceof ToDoItem) {
                    ToDoItem item = (ToDoItem) value;
                    if (item.isDone()) {
                        setForeground(new Color(34, 139, 34)); // Forest green for completed items
                    } else {
                        setForeground(Color.BLACK); // Default color for incomplete items
                    }
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(todoList);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(255, 192, 203), 2)); // Baby pink border
        panel.add(scrollPane, BorderLayout.CENTER);


        // Add toolbar for Task operations
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.setBorder(BorderFactory.createLineBorder(new Color(255, 192, 203), 2)); // Baby pink border
        JButton addButton = new JButton("Add");
        JButton deleteButton = new JButton("Delete");
        JButton editButton = new JButton("Edit");
        JButton markDoneButton = new JButton("Mark as Done");

        toolbar.add(addButton);
        toolbar.add(deleteButton);
        toolbar.add(editButton);
        toolbar.add(markDoneButton);
        panel.add(toolbar, BorderLayout.SOUTH);


        // Event listener to display To-Do List for the selected task
        taskList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Task selectedTask = taskList.getSelectedValue();
                toDoListModel.clear();
                if (selectedTask != null) {
                    for (ToDoItem item : selectedTask.getToDoList()) {
                        toDoListModel.addElement(item);
                    }
                }
            }
        });

        // Add button functionality
        addButton.addActionListener(e -> {
            Task selectedTask = taskList.getSelectedValue();
            if (selectedTask != null) {
                String description = JOptionPane.showInputDialog(this, "Enter To-Do item description:");
                if (description != null && !description.trim().isEmpty()) {
                    ToDoItem newItem = new ToDoItem(description);
                    selectedTask.addToDoItem(newItem);
                    toDoListModel.addElement(newItem);
                    taskManager.saveTasks();
                }
            }
        });

        deleteButton.addActionListener(e -> {
            Task selectedTask = taskList.getSelectedValue();
            ToDoItem selectedItem = todoList.getSelectedValue();
            if (selectedTask != null && selectedItem != null) {
                selectedTask.removeToDoItem(selectedItem);
                toDoListModel.removeElement(selectedItem);
                taskManager.saveTasks();
            }
        });

        editButton.addActionListener(e -> {
            ToDoItem selectedItem = todoList.getSelectedValue();
            if (selectedItem != null) {
                String newDescription = JOptionPane.showInputDialog(this, "Edit description:", selectedItem.getDescription());
                if (newDescription != null && !newDescription.trim().isEmpty()) {
                    selectedItem.setDescription(newDescription);
                    todoList.repaint();
                    taskManager.saveTasks();
                }
            }
        });

        markDoneButton.addActionListener(e -> {
            ToDoItem selectedItem = todoList.getSelectedValue();
            if (selectedItem != null) {
                selectedItem.setDone(!selectedItem.isDone()); // Toggle done status
                todoList.repaint();
                taskManager.saveTasks();
            }
        });

        return panel;
    }

    private void showAddTaskDialog() {
        try {
            
            JDialog dialog = new JDialog(this, "Add New Task", true);
            TaskUIManager.setTaskDialog(dialog, taskManager, listModel, false, null);
        } catch (Exception e) {
            System.out.println("Error in showAddTaskDialog: ");
            e.printStackTrace();
        }
    }

    private void showEditTaskDialog() {
        Task selectedTask = taskList.getSelectedValue();
        if (selectedTask != null) {
            JDialog dialog = new JDialog(this, "Edit Task", true);
            TaskUIManager.setTaskDialog(dialog, taskManager, listModel, true, selectedTask);
        }
    }

    private void refreshTaskList() {
        listModel.clear();
        for (Task task : taskManager.getTasks()) {
            listModel.addElement(task);
        }
    }


    private class TaskListCellRenderer extends DefaultListCellRenderer {
        private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof Task) {
                Task task = (Task) value;
                
                // Add null check for due date
                String displayText;
                if (task.getDueDate() != null) {
                    displayText = task.getName() + " - Due: " + task.getDueDate().format(dateFormatter);
                } else {
                    displayText = task.getName() + " - No due date";
                }
                setText(displayText);

                // Also modify the due date check
                if (task.getDueDate() != null && task.getDueDate().isBefore(LocalDateTime.now())) {
                    setForeground(Color.RED);  // Red for overdue tasks
                } else {
                    setForeground(Color.BLACK);  // Default color for non-overdue tasks
                }
            }
            return c;
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {  //schedules to run asap, making sure to handle stuff in the right order
            TaskManagerGUI gui = new TaskManagerGUI();
            gui.setVisible(true);
        });
    
    }
}