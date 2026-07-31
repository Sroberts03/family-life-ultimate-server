package com.app.errors;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ErrorDao {
    
    private final JdbcTemplate jdbcTemplate;

    public ErrorDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void logError(String errorClass, String errorMessage, String stackTrace, String userId) {
        String sql = """
                INSERT INTO
                    errors (error_class, error_message, stack_trace, effected_user)
                VALUES
                    (?, ?, ?, ?);
                """;
        java.util.UUID uuid = userId != null ? java.util.UUID.fromString(userId) : null;
        jdbcTemplate.update(sql, errorClass, errorMessage, stackTrace, uuid);
    }
}
