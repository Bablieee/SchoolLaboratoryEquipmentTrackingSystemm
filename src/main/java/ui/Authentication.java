
package com.schoollaboratory.ui;

import com.schoollaboratory.dao.UserDao;
import com.schoollaboratory.model.User;

import java.util.Scanner;

public class Authentication {

    private final UserDao userDao;
    private final Scanner scanner;

    public Authentication(UserDao userDao, Scanner scanner) {
        this.userDao = userDao;
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
                case "1" -> registerAccount();

                case "2" -> {
                    if (!login()) {
                        running = false;
                    }
                }

                case "3" -> {
                    System.out.println("\nThank you for using the system!");
                    running = false;
                }

                default -> System.out.println("\nInvalid choice. Please try again.");
            }
        }
    }

    private void registerAccount() {

        System.out.println("\n========== REGISTER ACCOUNT ==========");

        System.out.print("Enter Full Name: ");
        String name = scanner.nextLine();

        System.out.print("Enter Username: ");
        String username = scanner.nextLine();

        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        System.out.println("\nSelect User Role:");
        System.out.println("1. ADMINISTRATOR");
        System.out.println("2. LABORATORY STAFF");
        System.out.println("3. EQUIPMENT VIEWER");
        System.out.print("Enter your choice: ");

        String roleChoice = scanner.nextLine();
        String role;

        switch (roleChoice) {
            case "1" -> role = "ADMINISTRATOR";
            case "2" -> role = "LABORATORY STAFF";
            case "3" -> role = "EQUIPMENT VIEWER";

            default -> {
                System.out.println("\nInvalid role selection.");
                return;
            }
        }

        boolean registered = userDao.registerUser(
                name, username, password, role
        );

        if (registered) {
            System.out.println("\nAccount registered successfully!");
            System.out.println("Name: " + name);
            System.out.println("Username: " + username);
            System.out.println("Role: " + role);
            System.out.println("Status: PENDING");
            System.out.println("Please wait for administrator approval.");
        } else {
            System.out.println("\nRegistration failed.");
            System.out.println("Username may already exist.");
        }
    }

    private boolean login() {

        System.out.println("\n========== LOGIN ==========");

        System.out.print("Enter Username: ");
        String username = scanner.nextLine();

        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        User user = userDao.login(username, password);

        if (user == null) {
            System.out.println("\nInvalid username or password.");
            return true;
        }

        if (!user.status().equalsIgnoreCase("APPROVED")) {
            System.out.println("\nYour account is still PENDING.");
            System.out.println("Please wait for administrator approval.");
            return true;
        }

        System.out.println("\nLogin successful!");
        System.out.println("Welcome, " + user.name() + "!");
        System.out.println("Role: " + user.role());

        return routeUser(user);
    }

    private boolean routeUser(User user) {

        String role = user.role().trim();

        switch (role.toUpperCase()) {

            case "ADMINISTRATOR" -> {
                return showRoleMenu("ADMINISTRATOR MENU", user);
            }

            case "LABORATORY STAFF" -> {
                return showRoleMenu("LABORATORY STAFF MENU", user);
            }

            case "EQUIPMENT VIEWER" -> {
                return showRoleMenu("EQUIPMENT VIEWER MENU", user);
            }

            default -> {
                System.out.println("Invalid user role.");
                return true;
            }
        }
    }

    private boolean showRoleMenu(String menuTitle, User user) {

        while (true) {

            System.out.println("\n===== " + menuTitle + " =====");
            System.out.println("Welcome, " + user.name());
            System.out.println("1. Logout");
            System.out.println("2. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> {
                    System.out.println("You have logged out.");
                    return true;
                }

                case "2" -> {
                    System.out.println("Exiting system...");
                    return false;
                }

                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }
}
