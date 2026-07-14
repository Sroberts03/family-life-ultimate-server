package com.app.chore;

import java.time.LocalDate;
import java.sql.Array;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.app.chore.types.Chore;
import jakarta.annotation.Nullable;

@Repository
public class ChoreDao {

    private final JdbcTemplate jdbcTemplate;

    public ChoreDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @SuppressWarnings("null")
    public List<Chore> getAllChoresForFamilyForDate(String familyId, LocalDate date) {
        String sql = """
                SELECT
                    c.id,
                    ct.name,
                    ct.description,
                    c.due_date,
                    c.date_completed,
                    array_agg(uc.user_id) as assignee_ids,
                    array_agg(a.raw_user_meta_data->>'display_name') as assignee_names
                FROM
                    chores as c
                JOIN chore_templates as ct on c.chore_id = ct.id
                LEFT JOIN user_chore uc on c.id = uc.chore_id
                LEFT JOIN auth.users as a ON uc.user_id = a.id
                WHERE ct.family_id = ?
                AND c.due_date = ?
                GROUP BY
                    c.id,
                    ct.name,
                    ct.description,
                    c.due_date,
                    c.date_completed;
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Array assigneeIdsArray = rs.getArray("assignee_ids");
            String[] assigneeIds = new String[0];
            if (assigneeIdsArray != null) {
                UUID[] assigneeUuids = (UUID[]) assigneeIdsArray.getArray();
                if (assigneeUuids.length > 0 && assigneeUuids[0] != null) {
                    assigneeIds = Arrays.stream(assigneeUuids).map(UUID::toString).toArray(String[]::new);
                }
            }

            Array assigneeNamesArray = rs.getArray("assignee_names");
            String[] assigneeNames = new String[0];
            if (assigneeNamesArray != null) {
                assigneeNames = (String[]) assigneeNamesArray.getArray();
                if (assigneeNames.length > 0 && assigneeNames[0] == null) {
                    assigneeNames = new String[0];
                }
            }

            return new Chore(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getDate("due_date"),
                    rs.getDate("date_completed"),
                    assigneeIds,
                    assigneeNames);
        }, UUID.fromString(familyId), date);
    }

    public boolean choreExists(int choreId) {
        String sql = "SELECT EXISTS (SELECT 1 FROM chores WHERE id = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, choreId);
    }

    public String getFamilyIdFromChoreId(int choreId) {
        String sql = """
                SELECT
                    ct.family_id
                FROM
                    chores as c
                JOIN chore_templates as ct ON c.chore_id = ct.id
                WHERE c.id = ?;
                """;
        ;
        return jdbcTemplate.queryForObject(sql, String.class, choreId);
    }

    public void markChoreComplete(Date dateCompleted, int choreId) {
        String sql = """
                UPDATE
                    chores
                SET
                    date_completed = ?
                WHERE id = ?;
                """;
        jdbcTemplate.update(sql, dateCompleted, choreId);
    }

    public int createChoreTemplate(
        String familyId, 
        String name, 
        String description, 
        String recurring,
        LocalDate startDate,
        @Nullable LocalDate endDate) {
            String sql = """
                    INSERT INTO
                        chore_templates (family_id, name, description, recurring, start_date, end_date)
                    VALUES
                        (?, ?, ?, ?, ?, ?)
                    RETURNING id;
                    """;
            return jdbcTemplate.queryForObject(sql, Integer.class, UUID.fromString(familyId), name, description, recurring, startDate, endDate != null ? endDate : null);
    }

    @SuppressWarnings("null")
    public Chore getChoreFromTemplateIdAndDueDate(int templateId, LocalDate dueDate) {
        String sql = """
                SELECT
                    c.id,
                    ct.name,
                    ct.description,
                    c.due_date,
                    c.date_completed,
                    array_agg(uc.user_id) as assignee_ids,
                    array_agg(a.raw_user_meta_data->>'display_name') as assignee_names
                FROM
                    chores as c
                JOIN chore_templates as ct on c.chore_id = ct.id
                LEFT JOIN user_chore uc on c.id = uc.chore_id
                LEFT JOIN auth.users as a ON uc.user_id = a.id
                WHERE c.chore_id = ? and due_date = ?
                GROUP BY
                    c.id,
                    ct.name,
                    ct.description,
                    c.due_date,
                    c.date_completed;
                """;

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            Array assigneeIdsArray = rs.getArray("assignee_ids");
            String[] assigneeIds = new String[0];
            if (assigneeIdsArray != null) {
                UUID[] assigneeUuids = (UUID[]) assigneeIdsArray.getArray();
                if (assigneeUuids.length > 0 && assigneeUuids[0] != null) {
                    assigneeIds = Arrays.stream(assigneeUuids).map(UUID::toString).toArray(String[]::new);
                }
            }

            Array assigneeNamesArray = rs.getArray("assignee_names");
            String[] assigneeNames = new String[0];
            if (assigneeNamesArray != null) {
                assigneeNames = (String[]) assigneeNamesArray.getArray();
                if (assigneeNames.length > 0 && assigneeNames[0] == null) {
                    assigneeNames = new String[0];
                }
            }

            return new Chore(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getDate("due_date"),
                    rs.getDate("date_completed"),
                    assigneeIds,
                    assigneeNames);
        }, templateId, dueDate);
    }

    @SuppressWarnings("null")
    public Chore getChoreFromId(int choreId) {
        String sql = """
                SELECT
                    c.id,
                    ct.name,
                    ct.description,
                    c.due_date,
                    c.date_completed,
                    array_agg(uc.user_id) as assignee_ids,
                    array_agg(a.raw_user_meta_data->>'display_name') as assignee_names
                FROM
                    chores as c
                JOIN chore_templates as ct on c.chore_id = ct.id
                LEFT JOIN user_chore uc on c.id = uc.chore_id
                LEFT JOIN auth.users as a ON uc.user_id = a.id
                WHERE c.id = ?
                GROUP BY
                    c.id,
                    ct.name,
                    ct.description,
                    c.due_date,
                    c.date_completed;
                """;

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            Array assigneeIdsArray = rs.getArray("assignee_ids");
            String[] assigneeIds = new String[0];
            if (assigneeIdsArray != null) {
                UUID[] assigneeUuids = (UUID[]) assigneeIdsArray.getArray();
                if (assigneeUuids.length > 0 && assigneeUuids[0] != null) {
                    assigneeIds = Arrays.stream(assigneeUuids).map(UUID::toString).toArray(String[]::new);
                }
            }

            Array assigneeNamesArray = rs.getArray("assignee_names");
            String[] assigneeNames = new String[0];
            if (assigneeNamesArray != null) {
                assigneeNames = (String[]) assigneeNamesArray.getArray();
                if (assigneeNames.length > 0 && assigneeNames[0] == null) {
                    assigneeNames = new String[0];
                }
            }

            return new Chore(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getDate("due_date"),
                    rs.getDate("date_completed"),
                    assigneeIds,
                    assigneeNames);
        }, choreId);
    }

    @Transactional
    public void deleteChore(int choreId, boolean thisAndFuture) {
        if (thisAndFuture) {
            String sql2 = """
                    UPDATE 
                        chore_templates ct
                    SET 
                        end_date = ?
                    FROM chores c
                    WHERE c.id = ? and ct.id = c.chore_id;
                    """;

            jdbcTemplate.update(sql2,LocalDate.now().minusDays(1), choreId);
            
            String sql = """
                    DELETE FROM
                        chores c
                    USING
                        chore_templates ct
                    WHERE c.id = ? or c.chore_id = ct.id and due_date > ?;
                    """;
            jdbcTemplate.update(sql, choreId, LocalDate.now().minusDays(1));
        } else {
            String sql = """
                    DELETE FROM
                        chores
                    WHERE id = ?;
                    """;
            jdbcTemplate.update(sql, choreId);
        }
    }
        
}