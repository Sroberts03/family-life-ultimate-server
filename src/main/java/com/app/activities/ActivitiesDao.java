package com.app.activities;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import com.app.activities.types.DetailedActivity;

@Repository
public class ActivitiesDao {

    private final JdbcTemplate jdbcTemplate;

    public ActivitiesDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    public List<DetailedActivity> getAllActivities() {
        String sql = """
            SELECT 
                a.name,
                a.description,
                a.id
            FROM
                activities a
            WHERE
                a.current = true;
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            return new DetailedActivity(
                rs.getString("name"),
                rs.getString("description"),
                rs.getInt("id")
            );
        });
    }
        
}
