
package com.schoollaboratory;

import com.schoollaboratory.dao.UserDao;
import com.schoollaboratory.model.User;
import com.schoollaboratory.ui.AdminMenu;

import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final UserDao userDao = new UserDao();

    public static void main(String[] args) {

        // Create users table before displaying the menu
        userDao.createUsersTable();

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

                case "2" -> login();

                case "3" -> {
                    System.out.println("\nThank you for using the system!");
                    running = false;
                }

                default -> System.out.println("\nInvalid choice. Please try again.");
            }
        }

        scanner.close();
    }

    private static void registerAccount() {

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
                name,
                username,
                password,
                role
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

    private static void login() {

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
            System.out.println("\nYour account is still PENDING.");
            System.out.println("Please wait for administrator approval.");
            return;
        }

        System.out.println("\nLogin successful!");
        System.out.println("Welcome, " + user.name() + "!");
        System.out.println("Role: " + user.role());

        AdminMenu adminMenu = new AdminMenu(scanner);
        adminMenu.showMenu(user);
    }
}
