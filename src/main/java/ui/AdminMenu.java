
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
            System.out.println("4. Logout");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine();

            switch (choice) {

                case "1" -> viewAllUsers();

                case "2" -> viewPendingUsers();

                case "3" -> approveUserAccount();

                case "4" -> {
                    System.out.println("\nYou have logged out.");
                    return true;
                }

                case "5" -> {
                    System.out.println("\nThank you for using the system!");
                    return false;
                }

                default ->
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

        System.out.print("\nEnter User ID to approve (0 to cancel): ");

        String input = scanner.nextLine().trim();

        // Cancel approval
        if (input.equals("0")) {
            System.out.println("Approval cancelled.");
            return;
        }

        // Validate User ID
        int userId;

        try {
            userId = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid User ID. Please enter a number.");
            return;
        }

        // Check if User ID exists in pending registrations
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

        // Approve selected account
        boolean approved = userDao.approveUser(userId);

        if (approved) {

            System.out.println("\nUser account approved successfully!");
            System.out.println("Username: " + selectedUser.username());
            System.out.println("New Status: APPROVED");

        } else {

            System.out.println("\nFailed to approve user account.");
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
