
package com.schoollaboratory.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class DatabaseInitializer {

    public void initialize() {

        String usersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    username TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL,
                    roles TEXT NOT NULL,
                    status TEXT NOT NULL DEFAULT 'PENDING'
                )
                """;

        String equipmentTable = """
                CREATE TABLE IF NOT EXISTS equipment (
                    equipment_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    equipment_name TEXT NOT NULL,
                    category TEXT NOT NULL,
                    location TEXT NOT NULL,
                    condition_status TEXT NOT NULL
                )
                """;

        String activityLogsTable = """
                CREATE TABLE IF NOT EXISTS activity_logs (
                    log_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    equipment_id INTEGER,
                    action_type TEXT NOT NULL,
                    description TEXT NOT NULL,
                    timestamp TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (user_id) REFERENCES users(id)
                        ON DELETE CASCADE,
                    FOREIGN KEY (equipment_id) REFERENCES equipment(equipment_id)
                        ON DELETE SET NULL
                )
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute(usersTable);
            statement.execute(equipmentTable);
            statement.execute(activityLogsTable);

            // Check if equipment_id already exists
            boolean hasEquipmentId = false;

            try (ResultSet resultSet =
                         statement.executeQuery("PRAGMA table_info(activity_logs)")) {

                while (resultSet.next()) {
                    if ("equipment_id".equalsIgnoreCase(
                            resultSet.getString("name"))) {
                        hasEquipmentId = true;
                        break;
                    }
                }
            }

            // Add equipment_id to an existing table if missing
            if (!hasEquipmentId) {
                statement.execute("""
                        ALTER TABLE activity_logs
                        ADD COLUMN equipment_id INTEGER
                        REFERENCES equipment(equipment_id)
                        ON DELETE SET NULL
                        """);
            }

            System.out.println("Database initialized successfully!");

        } catch (SQLException e) {
            System.out.println(
                    "Database initialization failed: " + e.getMessage()
            );
        }
    }
}
