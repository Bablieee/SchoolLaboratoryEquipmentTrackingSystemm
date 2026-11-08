
package com.schoollaboratory.ui;

import com.schoollaboratory.dao.UserDao;
import com.schoollaboratory.dao.ActivityLogDao;
import com.schoollaboratory.model.User;
import com.schoollaboratory.model.ActivityLog;

import java.util.List;
import java.util.Scanner;

public class AdminMenu {

    private final UserDao userDao;
    private final ActivityLogDao activityLogDao;
    private final Scanner scanner;

    public AdminMenu(UserDao userDao, Scanner scanner) {
        this.userDao = userDao;
        this.activityLogDao = new ActivityLogDao();
        this.scanner = scanner;
    }

    // Administrator main menu
    public boolean showMenu(User user) {

        while (true) {

            System.out.println("\n==================================================");
            System.out.println("       SCHOOL LABORATORY EQUIPMENT TRACKING SYSTEM");
            System.out.println("                  ADMINISTRATOR MENU");
            System.out.println("==================================================");
            System.out.println("Welcome, " + user.name());
            System.out.println("--------------------------------------------------");
            System.out.println("1. View All System Users");
            System.out.println("2. View Pending Registrations");
            System.out.println("3. Approve User Account");
            System.out.println("4. Update User");
            System.out.println("5. View System Audit Logs");
            System.out.println("6. Logout");
            System.out.println("7. Exit");
            System.out.println("--------------------------------------------------");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine();

            switch (choice) {

                case "1" -> viewAllUsers();

                case "2" -> viewPendingUsers();

                case "3" -> approveUserAccount(user);

                case "4" -> updateUserAccount(user);

                case "5" -> showAuditLogs();

                case "6" -> {
                    System.out.println("\nYou have logged out.");
                    return true;
                }

                case "7" -> {
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

    // Display pending registrations
    private void viewPendingUsers() {

        System.out.println("\n========== PENDING REGISTRATIONS ==========");

        List<User> users = userDao.getPendingUsers();

        displayUserTable(users);
    }

    // Approve a user account
    private void approveUserAccount(User currentAdmin) {

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
            System.out.println("User ID not found in pending registrations.");
            return;
        }

        boolean approved = userDao.approveUser(
                userId,
                currentAdmin.id()
        );

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

        if (targetUserId == currentAdmin.id()) {
            System.out.println(
                    "\nWARNING: Modifying the currently active admin account is restricted."
            );
            return;
        }

        System.out.println("\nSelected User: " + selectedUser.username());
        System.out.println("Current Role: " + selectedUser.role());
        System.out.println("Current Status: " + selectedUser.status());

        String newRole = readValidRole();

        if (newRole == null) {
            System.out.println("Update cancelled.");
            return;
        }

        String newStatus = readValidStatus();

        if (newStatus == null) {
            System.out.println("Update cancelled.");
            return;
        }

        boolean updated = userDao.updateUserRoleAndStatus(
                targetUserId,
                newRole,
                newStatus,
                currentAdmin.id()
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

    // System audit log menu
    private void showAuditLogs() {

        while (true) {

            System.out.println("\n==========================================");
            System.out.println("             SYSTEM AUDIT LOGS");
            System.out.println("==========================================");
            System.out.println("[1] View All System Logs");
            System.out.println("[2] Filter Logs by User ID");
            System.out.println("[3] Clear Historical Logs");
            System.out.println("[0] Back to Admin Menu");
            System.out.println("------------------------------------------");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine();

            switch (choice) {

                case "1" -> {
                    displayAuditLogs(activityLogDao.getAllLogs());
                }

                case "2" -> {

                    int userId = readIntInput(
                            "\nEnter User ID (0 to cancel): "
                    );

                    if (userId == 0) {
                        System.out.println("Filtering cancelled.");
                        break;
                    }

                    displayAuditLogs(
                            activityLogDao.getLogsByUserId(userId)
                    );
                }

                case "3" -> {

                    System.out.print(
                            "\nAre you sure you want to clear all historical logs? (Y/N): "
                    );

                    String confirmation = scanner.nextLine().trim();

                    if (confirmation.equalsIgnoreCase("Y")) {

                        boolean cleared = activityLogDao.clearAllLogs();

                        if (cleared) {
                            System.out.println(
                                    "\nHistorical audit logs cleared successfully!"
                            );
                        } else {
                            System.out.println(
                                    "\nFailed to clear historical audit logs."
                            );
                        }

                    } else {
                        System.out.println("\nClear history cancelled.");
                    }
                }

                case "0" -> {
                    return;
                }

                default ->
                        System.out.println("\nInvalid choice. Please try again.");
            }
        }
    }

    // Display audit logs in an organized table
    private void displayAuditLogs(List<ActivityLog> logs) {

        System.out.println("\n==================== ALL SYSTEM LOGS ====================");

        if (logs.isEmpty()) {
            System.out.println("\nNo activity records found.");
            System.out.println("==========================================================");
            return;
        }

        System.out.println("\nTotal Records: " + logs.size());

        String border =
                "+------+---------+---------------+----------------------------------------------------+---------------------+";

        System.out.println();
        System.out.println(border);

        System.out.printf(
                "| %-4s | %-7s | %-13s | %-50s | %-19s |%n",
                "ID",
                "USER ID",
                "ACTION TYPE",
                "DESCRIPTION",
                "TIMESTAMP"
        );

        System.out.println(border);

        for (ActivityLog log : logs) {

            System.out.printf(
                    "| %-4d | %-7d | %-13s | %-50s | %-19s |%n",
                    log.logId(),
                    log.userId(),
                    fit(log.actionType(), 13),
                    fit(log.description(), 50),
                    fit(log.timestamp(), 19)
            );
        }

        System.out.println(border);
        System.out.println("                 END OF AUDIT LOG RECORDS");
        System.out.println("==========================================================");
    }

    // Limit text and add ellipsis when it exceeds the column width
    private String fit(String value, int width) {

        if (value == null) {
            return "";
        }

        if (value.length() > width) {

            if (width <= 3) {
                return value.substring(0, width);
            }

            return value.substring(0, width - 3) + "...";
        }

        return value;
    }

    // Read integer input safely
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

            switch (input.toUpperCase()) {

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

            switch (input.toUpperCase()) {

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

        System.out.println(
                "\n-----------------------------------------------------------------------"
        );

        System.out.printf(
                "%-5s | %-18s | %-22s | %-12s%n",
                "ID",
                "USERNAME",
                "ROLE",
                "STATUS"
        );

        System.out.println(
                "-----------------------------------------------------------------------"
        );

        for (User user : users) {

            System.out.printf(
                    "%-5d | %-18s | %-22s | %-12s%n",
                    user.id(),
                    fit(user.username(), 18),
                    fit(user.role(), 22),
                    fit(user.status(), 12)
            );
        }

        System.out.println(
                "-----------------------------------------------------------------------"
        );
    }
}
