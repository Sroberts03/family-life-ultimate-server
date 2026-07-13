package com.app.chore;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.sql.Array;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import com.app.chore.types.Chore;

@Repository
public class ChoreDao {

    private final JdbcTemplate jdbcTemplate;

    public ChoreDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Chore> getAllChoresForFamilyForDate(String familyId, Instant date) {
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
                JOIN user_chore uc on c.id = uc.chore_id
                JOIN auth.users as a ON uc.user_id = a.id
                WHERE ct.family_id = ?
                AND c.due_date = ?
                GROUP BY 
                    c.id,
                    ct.name,
                    ct.description,
                    c.due_date,
                    c.date_completed;
                """;

        LocalDate localDate = date.atZone(ZoneOffset.UTC).toLocalDate();

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
                Array assigneeIdsArray = rs.getArray("assignee_ids");
                UUID[] assigneeUuids = (UUID[]) assigneeIdsArray.getArray();
                @SuppressWarnings("null")
                String[] assigneeIds = Arrays.stream(assigneeUuids)
                        .map(UUID::toString)
                        .toArray(String[]::new);

                return new Chore(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getDate("due_date"),
                    rs.getDate("date_completed"),
                    assigneeIds,
                    (String[]) rs.getArray("assignee_names").getArray()
                );
            }, UUID.fromString(familyId), localDate);
    }
    
}