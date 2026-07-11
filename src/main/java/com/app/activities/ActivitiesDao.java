package com.app.activities;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import com.app.activities.types.DetailedActivity;
import com.app.auth.types.PersActivity;
import java.util.stream.Collectors;

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

    public List<PersActivity> getUserActivities(String userId, String familyId) {
        String sql = """
            SELECT 
                a.id,
                a.name,
                pa.family_id as familyId
            FROM
                pers_activities pa
            JOIN
                activities a ON a.id = pa.activity_id
            WHERE
                pa.user_id = ? AND pa.family_id = ?;
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> new PersActivity(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("familyId")
        ), UUID.fromString(userId), UUID.fromString(familyId));
    }

    public List<String> getAllActivitiesNamesFromListOfIds(Set<Integer> activityIds) {
        String placeholders = activityIds.stream()
            .map(id -> "?")
            .collect(Collectors.joining(","));

        String sql = """
                SELECT 
                    a.name
                FROM
                    activities a
                WHERE
                    a.id IN (%s);
                """.formatted(placeholders);

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            return rs.getString("name");
        }, activityIds.toArray());
    }
    
    public void updateUserActivities(String userId, String familyId, Map<Integer, Boolean> permissions) {
        Map<Integer, Boolean> deleteUpdates = permissions.entrySet().stream()
            .filter(entry -> !entry.getValue())
            .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));
            
        Map<Integer, Boolean> insertUpdates = permissions.entrySet().stream()
            .filter(entry -> entry.getValue())
            .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));
        
        String insertSql = """
            INSERT INTO pers_activities (user_id, activity_id, family_id)
            VALUES (?, ?, ?)
            ON CONFLICT (user_id, family_id, activity_id) DO NOTHING;
            """;

        jdbcTemplate.batchUpdate(insertSql, insertUpdates.entrySet().stream().map(entry -> new Object[] {
            UUID.fromString(userId),
            entry.getKey(),
            UUID.fromString(familyId)
        }).toList());

        String deleteSql = """
            DELETE FROM pers_activities
            WHERE user_id = ? AND activity_id = ? AND family_id = ?;
            """;
        jdbcTemplate.batchUpdate(deleteSql, deleteUpdates.entrySet().stream().map(entry -> new Object[] {
            UUID.fromString(userId),
            entry.getKey(),
            UUID.fromString(familyId)
        }).toList());
    }
}
