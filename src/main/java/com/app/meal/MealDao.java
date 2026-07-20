package com.app.meal;

import java.time.LocalDate;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.app.meal.types.MealPlanItem;
import com.app.meal.types.MealType;

@Repository
public class MealDao {

    private final JdbcTemplate jdbcTemplate;

    public MealDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<MealPlanItem> getMealPlansForFamilyForDate(String familyId, LocalDate date) {
        String sql = """
                SELECT
                    mp.id,
                    mp.family_id as "familyId",
                    mp.recipe_id as "recipeId",
                    mp.name,
                    mp.date,
                    mp.time,
                    mp.meal_type as "mealType",
                    mp.created_at as "createdAt",
                    mp.updated_at as "updatedAt"
                FROM meal_plan_item mp
                WHERE mp.family_id = ? AND mp.date = ?;
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            return new MealPlanItem(
                    rs.getInt("id"),
                    rs.getString("familyId"),
                    rs.getInt("recipeId"),
                    rs.getString("name"),
                    rs.getDate("date").toLocalDate(),
                    rs.getTime("time").toLocalTime(),
                    MealType.valueOf(rs.getString("mealType").toUpperCase()),
                    rs.getTimestamp("createdAt").toLocalDateTime(),
                    rs.getTimestamp("updatedAt").toLocalDateTime());
        }, java.util.UUID.fromString(familyId), date);
    }
}
