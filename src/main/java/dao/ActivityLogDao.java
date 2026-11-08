
package com.schoollaboratory.dao;

import com.schoollaboratory.model.ActivityLog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ActivityLogDao {

    // Record a system activity
    public void logAction(int userId, String actionType, String description) {

        String sql = """
                INSERT INTO activity_logs
                (user_id, action_type, description)
                VALUES (?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            statement.setString(2, actionType);
            statement.setString(3, description);

            statement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Failed to record activity: " + e.getMessage());
        }
    }

    // Retrieve all activity logs
    public List<ActivityLog> getAllLogs() {

        List<ActivityLog> logs = new ArrayList<>();

        String sql = """
                SELECT * FROM activity_logs
                ORDER BY timestamp DESC
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {

                logs.add(new ActivityLog(
                        resultSet.getInt("log_id"),
                        resultSet.getInt("user_id"),
                        resultSet.getString("action_type"),
                        resultSet.getString("description"),
                        resultSet.getString("timestamp")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Failed to retrieve activity logs: " + e.getMessage());
        }

        return logs;
    }

    // Retrieve activity logs by user ID
    public List<ActivityLog> getLogsByUserId(int userId) {

        List<ActivityLog> logs = new ArrayList<>();

        String sql = """
                SELECT * FROM activity_logs
                WHERE user_id = ?
                ORDER BY timestamp DESC
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    logs.add(new ActivityLog(
                            resultSet.getInt("log_id"),
                            resultSet.getInt("user_id"),
                            resultSet.getString("action_type"),
                            resultSet.getString("description"),
                            resultSet.getString("timestamp")
                    ));
                }
            }

        } catch (SQLException e) {
            System.out.println("Failed to filter activity logs: " + e.getMessage());
        }

        return logs;
    }

    // Clear all historical activity logs
    public boolean clearAllLogs() {

        String sql = "DELETE FROM activity_logs";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.executeUpdate();

            return true;

        } catch (SQLException e) {
            System.out.println("Failed to clear activity logs: " + e.getMessage());
            return false;
        }
    }
}
