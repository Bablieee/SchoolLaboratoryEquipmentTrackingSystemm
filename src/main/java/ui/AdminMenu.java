
package com.schoollaboratory.ui;

import com.schoollaboratory.dao.UserDao;
import com.schoollaboratory.model.User;

import java.util.List;
import java.util.Scanner;

public class AdminMenu {

    private final UserDao userDao;
    private final Scanner scanner;

    public AdminMenu(UserDao userDao, Scanner scanner) {
        this.userDao = userDao;
        this.scanner = scanner;
    }

    public boolean showMenu(User user) {

        while (true) {

            System.out.println("\n==================================");
            System.out.println("       ADMINISTRATOR MENU");
            System.out.println("==================================");
            System.out.println("Welcome, " + user.name());
            System.out.println("1. View All System Users");
            System.out.println("2. View Pending Registrations");
            System.out.println("3. Approve User Account");
            System.out.println("4. Update User");
            System.out.println("5. Logout");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine();

            switch (choice) {

                case "1":
                    viewAllUsers();
                    break;

                case "2":
                    viewPendingUsers();
                    break;

                case "3":
                    approveUserAccount();
                    break;

                case "4":
                    updateUserAccount(user);
                    break;

                case "5":
                    System.out.println("\nYou have logged out.");
                    return true;

                case "6":
                    System.out.println("\nThank you for using the system!");
                    return false;

                default:
                    System.out.println("\nInvalid choice. Please try again.");
            }
        }
    }

    // Display all system users
    private void viewAllUsers() {

        System.out.println("\n========== ALL SYSTEM USERS ==========");

        List<User> users = userDao.getAllUsers();

        displayUserTable(users);
    }

    // Display all pending registrations
    private void viewPendingUsers() {

        System.out.println("\n========== PENDING REGISTRATIONS ==========");

        List<User> users = userDao.getPendingUsers();

        displayUserTable(users);
    }

    // Approve a pending user account
    private void approveUserAccount() {

        System.out.println("\n========== APPROVE USER ACCOUNT ==========");

        List<User> pendingUsers = userDao.getPendingUsers();

        if (pendingUsers.isEmpty()) {
            System.out.println("No pending registrations found.");
            return;
        }

        displayUserTable(pendingUsers);

        int userId = readIntInput(
                "\nEnter User ID to approve (0 to cancel): "
        );

        if (userId == 0) {
            System.out.println("Approval cancelled.");
            return;
        }

        User selectedUser = null;

        for (User pendingUser : pendingUsers) {

            if (pendingUser.id() == userId) {
                selectedUser = pendingUser;
                break;
            }
        }

        if (selectedUser == null) {
            System.out.println(
                    "User ID not found in the pending registrations."
            );
            return;
        }

        boolean approved = userDao.approveUser(userId);

        if (approved) {

            System.out.println("\nUser account approved successfully!");
            System.out.println("Username: " + selectedUser.username());
            System.out.println("New Status: APPROVED");

        } else {

            System.out.println("\nFailed to approve user account.");
        }
    }

    // Update user role and account status
    private void updateUserAccount(User currentAdmin) {

        System.out.println("\n========== UPDATE USER ACCOUNT ==========");

        List<User> users = userDao.getAllUsers();

        if (users.isEmpty()) {
            System.out.println("No user records found.");
            return;
        }

        displayUserTable(users);

        int targetUserId = readIntInput(
                "\nEnter User ID to update (0 to cancel): "
        );

        if (targetUserId == 0) {
            System.out.println("Update cancelled.");
            return;
        }

        User selectedUser = null;

        for (User user : users) {

            if (user.id() == targetUserId) {
                selectedUser = user;
                break;
            }
        }

        if (selectedUser == null) {
            System.out.println("User ID not found.");
            return;
        }

        // Prevent editing the currently active admin account
        if (targetUserId == currentAdmin.id()) {

            System.out.println(
                    "\nWARNING: Modifying the currently active admin account is restricted."
            );

            return;
        }

        System.out.println("\nSelected User: " + selectedUser.username());
        System.out.println("Current Role: " + selectedUser.role());
        System.out.println("Current Status: " + selectedUser.status());

        // Read and validate the new role
        String newRole = readValidRole();

        if (newRole == null) {
            System.out.println("Update cancelled.");
            return;
        }

        // Read and validate the new status
        String newStatus = readValidStatus();

        if (newStatus == null) {
            System.out.println("Update cancelled.");
            return;
        }

        // Update selected user's information
        boolean updated = userDao.updateUserRoleAndStatus(
                targetUserId,
                newRole,
                newStatus
        );

        if (updated) {

            System.out.println("\nUser account updated successfully!");
            System.out.println("Username: " + selectedUser.username());
            System.out.println("New Role: " + newRole);
            System.out.println("New Status: " + newStatus);

        } else {

            System.out.println("\nFailed to update user account.");
        }
    }

    // Read integer input without crashing
    private int readIntInput(String prompt) {

        while (true) {

            System.out.print(prompt);

            String input = scanner.nextLine().trim();

            if (input.equals("0")) {
                return 0;
            }

            try {
                return Integer.parseInt(input);

            } catch (NumberFormatException e) {

                System.out.println(
                        "Invalid input. Please enter numbers only."
                );
            }
        }
    }

    // Read and validate user role
    private String readValidRole() {

        while (true) {

            System.out.println("\nSelect New Role:");
            System.out.println("1. ADMINISTRATOR");
            System.out.println("2. LABORATORY STAFF");
            System.out.println("3. EQUIPMENT VIEWER");
            System.out.println("Enter 0 to cancel.");

            System.out.print("Enter new role: ");

            String input = scanner.nextLine().trim();

            if (input.equals("0")) {
                return null;
            }

            String role = input.toUpperCase();

            switch (role) {

                case "1":
                case "ADMINISTRATOR":
                    return "ADMINISTRATOR";

                case "2":
                case "LABORATORY STAFF":
                    return "LABORATORY STAFF";

                case "3":
                case "EQUIPMENT VIEWER":
                    return "EQUIPMENT VIEWER";

                default:
                    System.out.println(
                            "Invalid role. Please select a valid role."
                    );
            }
        }
    }

    // Read and validate account status
    private String readValidStatus() {

        while (true) {

            System.out.println("\nSelect New Status:");
            System.out.println("1. APPROVED");
            System.out.println("2. PENDING");
            System.out.println("Enter 0 to cancel.");

            System.out.print("Enter new status: ");

            String input = scanner.nextLine().trim();

            if (input.equals("0")) {
                return null;
            }

            String status = input.toUpperCase();

            switch (status) {

                case "1":
                case "APPROVED":
                    return "APPROVED";

                case "2":
                case "PENDING":
                    return "PENDING";

                default:
                    System.out.println(
                            "Invalid status. Please select a valid status."
                    );
            }
        }
    }

    // Display formatted user table
    private void displayUserTable(List<User> users) {

        if (users.isEmpty()) {
            System.out.println("No user records found.");
            return;
        }

        System.out.println("---------------------------------------------------------------");

        System.out.printf(
                "%-5s %-15s %-20s %-12s%n",
                "ID", "USERNAME", "ROLE", "STATUS"
        );

        System.out.println("---------------------------------------------------------------");

        for (User user : users) {

            System.out.printf(
                    "%-5d %-15s %-20s %-12s%n",
                    user.id(),
                    user.username(),
                    user.role(),
                    user.status()
            );
        }

        System.out.println("---------------------------------------------------------------");
    }
}
