
package com.schoollaboratory.ui;

import com.schoollaboratory.model.User;

import java.util.Scanner;

public class EquipmentViewerMenu {

    private final Scanner scanner;

    public EquipmentViewerMenu(Scanner scanner) {
        this.scanner = scanner;
    }

    public boolean showMenu(User user) {

        while (true) {

            System.out.println("\n==================================");
            System.out.println("       EQUIPMENT VIEWER MENU");
            System.out.println("==================================");
            System.out.println("Welcome, " + user.name());
            System.out.println("1. Logout");
            System.out.println("2. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {

                case "1" -> {
                    System.out.println("\nYou have logged out.");
                    return true;
                }

                case "2" -> {
                    System.out.println("\nThank you for using the system!");
                    return false;
                }

                default ->
                        System.out.println("\nInvalid choice. Please try again.");
            }
        }
    }
}
