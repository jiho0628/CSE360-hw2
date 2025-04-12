package application;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import databasePart1.DatabaseHelper;

public class UserManagementPanel {
    private JTable userTable;
    private DefaultTableModel tableModel;
    private DatabaseHelper dbHelper;
    private JButton deleteButton, otpButton, updateRolesButton;
 /*   
    // Initialize the database helper
    dbHelper = new DatabaseHelper();
    
    // Set up the table model and table
    tableModel = new DefaultTableModel(new String[]{"UserID", "Username", "Name", "Email", "Roles"}, 0);
    userTable = new JTable(tableModel);
    loadUserData();

    // Create buttons for our functionalities
    deleteButton = new JButton("Delete User");
    otpButton = new JButton("Generate OTP");
    updateRolesButton = new JButton("Update Roles");
    
    // Add action listeners to buttons
    deleteButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            handleDeleteUser();
        }
    });

    otpButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            handleGenerateOTP();
        }
    });

    updateRolesButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            handleUpdateRoles();
        }
    });

    // Layout the components
    setLayout(new BorderLayout());
    add(new JScrollPane(userTable), BorderLayout.CENTER);
    JPanel buttonPanel = new JPanel();
    buttonPanel.add(deleteButton);
    buttonPanel.add(otpButton);
    buttonPanel.add(updateRolesButton);
    add(buttonPanel, BorderLayout.SOUTH);
    

    // Loads user data from the database into the table
    private void loadUserData() {
        // Clear current rows
        tableModel.setRowCount(0);
        List<User> users = dbHelper.getAllUsers();
        if (users != null) {
            for (User user : users) {
                tableModel.addRow(new Object[]{
                    user.getUserId(),
                    user.getUsername(),
                    user.getName(),
                    user.getEmail(),
                    user.getRoles()
                });
            }
        }
    }
    

    // Deletes a selected user after confirmation
    private void handleDeleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete the selected user?", 
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int userId = (Integer) tableModel.getValueAt(selectedRow, 0);
            boolean success = dbHelper.deleteUser(userId);
            if (success) {
                JOptionPane.showMessageDialog(this, "User deleted successfully.");
                loadUserData();
            } else {
                JOptionPane.showMessageDialog(this, "Error deleting user. Please try again.");
            }
        }
    }
    

    // Generates a one-time password for a selected user
    private void handleGenerateOTP() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to generate an OTP.");
            return;
        }
        int userId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String otp = generateOneTimePassword();
        boolean success = dbHelper.assignOTPToUser(userId, otp);
        if (success) {
            JOptionPane.showMessageDialog(this, "OTP generated: " + otp);
        } else {
            JOptionPane.showMessageDialog(this, "Error generating OTP. Please try again.");
        }
    }
    
    // Simple OTP generation logic (customize as needed)
    private String generateOneTimePassword() {
        // Generate a random alphanumeric string (here, using a UUID for simplicity)
        return UUID.randomUUID().toString().substring(0, 8);
    }

    // Allows the admin to update a user's roles
    private void handleUpdateRoles() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to update roles.");
            return;
        }
        int userId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String currentRoles = (String) tableModel.getValueAt(selectedRow, 4);
        
        // Prompt the admin for new roles (comma separated)
        String newRoles = JOptionPane.showInputDialog(this, "Enter new roles (comma separated):", currentRoles);
        if (newRoles != null) {
            // Validation: If the user is an admin, ensure that the admin role is not removed
            if (currentRoles.toLowerCase().contains("admin") && !newRoles.toLowerCase().contains("admin")) {
                JOptionPane.showMessageDialog(this, "Admin role cannot be removed from an admin account.");
                return;
            }
            boolean success = dbHelper.updateUserRoles(userId, newRoles);
            if (success) {
                JOptionPane.showMessageDialog(this, "User roles updated successfully.");
                loadUserData();
            } else {
                JOptionPane.showMessageDialog(this, "Error updating roles. Please try again.");
            }
        }
    }

    // For demonstration: main method to display the panel in a frame
    public static void main(String[] args) {
        JFrame frame = new JFrame("User Management");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new UserManagementPanel());
        frame.setSize(800, 400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    */
}


