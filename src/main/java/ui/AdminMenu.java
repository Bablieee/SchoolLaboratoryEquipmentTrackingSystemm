
package com.schoollaboratory.ui;

import com.schoollaboratory.model.User;
import java.util.Scanner;

public class AdminMenu {

    private final Scanner scanner;

    public AdminMenu(Scanner scanner) {
        this.scanner = scanner;
    }

    public void showMenu(User user) {
        boolean running = true;

        while (running) {
            System.out.println("\n===== ADMIN MENU =====");
            System.out.println("Welcome, " + user.username());
            System.out.println("1. Logout");
            System.out.println("2. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> {
                    System.out.println("You have logged out.");
                    running = false;
                }
                case "2" -> {
                    System.out.println("Exiting system...");
                    System.exit(0);
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }
}
