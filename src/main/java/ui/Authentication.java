
package com.schoollaboratory.ui;

import com.schoollaboratory.dao.UserDao;
import com.schoollaboratory.dao.ActivityLogDao;
import com.schoollaboratory.model.User;

import java.util.Scanner;

public class Authentication {

    private final UserDao userDao;
    private final ActivityLogDao activityLogDao;
    private final Scanner scanner;

    public Authentication(UserDao userDao, Scanner scanner) {
        this.userDao = userDao;
        this.activityLogDao = new ActivityLogDao();
        this.scanner = scanner;
    }

    public void start() {

        boolean running = true;

        while (running) {

            System.out.println("\n==============================================");
            System.out.println(" SCHOOL LABORATORY EQUIPMENT TRACKING SYSTEM");
            System.out.println("==============================================");
            System.out.println("1. Register Account");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine();

            switch (choice) {

                case "1":
                    registerAccount();
                    break;

                case "2":
                    loginAccount();
                    break;

                case "3":
                    running = false;
                    System.out.println("Thank you for using the system!");
                    break;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void registerAccount() {

        System.out.println("\n========== REGISTER ACCOUNT ==========");

        System.out.print("Enter Name: ");
        String name = scanner.nextLine();

        System.out.print("Enter Username: ");
        String username = scanner.nextLine();

        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        System.out.println("\nSelect Role:");
        System.out.println("1. Laboratory Staff");
        System.out.println("2. Equipment Viewer");
        System.out.print("Enter choice: ");

        String roleChoice = scanner.nextLine();
        String role;

        switch (roleChoice) {

            case "1":
                role = "LABORATORY STAFF";
                break;

            case "2":
                role = "EQUIPMENT VIEWER";
                break;

            default:
                System.out.println("Invalid role selection.");
                return;
        }

        boolean registered = userDao.registerUser(
                name,
                username,
                password,
                role
        );

        if (registered) {
            System.out.println("Registration successful!");
            System.out.println("Your account is waiting for administrator approval.");
        } else {
            System.out.println("Registration failed. Username may already exist.");
        }
    }

    private void loginAccount() {

        System.out.println("\n========== LOGIN ==========");

        System.out.print("Enter Username: ");
        String username = scanner.nextLine();

        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        User user = userDao.login(username, password);

        if (user == null) {
            System.out.println("\nInvalid username or password.");
            return;
        }

        if (!user.status().equalsIgnoreCase("APPROVED")) {
            System.out.println("\nYour account is not yet approved.");
            return;
        }

        System.out.println("\nLogin successful!");
        System.out.println("Welcome, " + user.name());
        System.out.println("Role: " + user.role());

        // Record successful login
        activityLogDao.logAction(
                user.id(),
                "USER_LOGIN",
                "User " + user.username() + " (ID: " + user.id() + ") logged in."
        );

        routeUser(user);
    }

    private void routeUser(User user) {

        boolean loggedOut = false;

        switch (user.role().toUpperCase()) {

            case "ADMINISTRATOR":

                AdminMenu adminMenu =
                        new AdminMenu(userDao, scanner);

                loggedOut = adminMenu.showMenu(user);
                break;

            case "LABORATORY STAFF":

                LaboratoryStaffMenu staffMenu =
                        new LaboratoryStaffMenu(scanner);

                loggedOut = staffMenu.showMenu(user);
                break;

            case "EQUIPMENT VIEWER":

                EquipmentViewerMenu viewerMenu =
                        new EquipmentViewerMenu(scanner);

                loggedOut = viewerMenu.showMenu(user);
                break;

            default:
                System.out.println("Invalid user role.");
                return;
        }

        // Record logout only when the user selects Logout
        if (loggedOut) {

            activityLogDao.logAction(
                    user.id(),
                    "USER_LOGOUT",
                    "User " + user.username() + " (ID: " + user.id() + ") logged out."
            );

            System.out.println("\nYou have successfully logged out.");
        }
    }
}
