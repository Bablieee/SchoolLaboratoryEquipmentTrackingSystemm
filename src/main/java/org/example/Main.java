package com.schoollaboratory;

import com.schoollaboratory.dao.DatabaseInitializer;
import com.schoollaboratory.dao.UserDao;
import com.schoollaboratory.ui.Authentication;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        // Initialize database
        DatabaseInitializer databaseInitializer = new DatabaseInitializer();
        databaseInitializer.initialize();

        // Create shared instances
        UserDao userDao = new UserDao();
        Scanner scanner = new Scanner(System.in);

        // Start authentication
        Authentication authentication = new Authentication(userDao, scanner);
        authentication.start();

        scanner.close();
    }
}
