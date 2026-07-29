package com.app.meal;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import com.app.meal.types.MealPlanItem;
import com.app.meal.types.MealType;
import com.app.meal.types.Recipe;
import com.app.meal.types.RecipeBook;
import jakarta.transaction.Transactional;

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
                FROM meal_plan_items mp
                WHERE mp.family_id = ? AND mp.date = ?
                ORDER BY mp.time;
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

    public List<String> getFamilyIdFromRecipe(int recipeId) {
        String sql = """
                SELECT
                    fb.family_id as "familyId"
                FROM
                    recipes r
                JOIN recipe_books rb ON r.recipe_book_id = rb.id
                JOIN family_recipe_book fb ON rb.id = fb.recipe_book_id
                WHERE
                    r.id = ?;
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("familyId"), recipeId);
    }

    public Recipe getRecipeDetail(int recipeId) {
        String sql = """
                SELECT
                    r.id,
                    r.recipe_book_id as "recipeBookId",
                    r.name,
                    r.description,
                    r.url,
                    r.created_at as "createdAt",
                    r.updated_at as "updatedAt"
                FROM
                    recipes r
                WHERE
                    r.id = ?;
                """;

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            Recipe recipe = new Recipe(
                    rs.getInt("id"),
                    rs.getInt("recipeBookId"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getString("url"),
                    rs.getTimestamp("createdAt").toLocalDateTime(),
                    rs.getTimestamp("updatedAt").toLocalDateTime());
            return recipe;
        }, recipeId);
    }

    public List<RecipeBook> getRecipeBooksForFamily(String familyId) {
        String sql = """
                SELECT
                    rb.id,
                    rb.name,
                    rb.created_at as "createdAt",
                    rb.updated_at as "updatedAt"
                FROM
                    recipe_books rb
                JOIN family_recipe_book fb ON rb.id = fb.recipe_book_id
                WHERE
                    fb.family_id = ?;
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            return new RecipeBook(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getTimestamp("createdAt").toLocalDateTime(),
                    rs.getTimestamp("updatedAt").toLocalDateTime());
        }, java.util.UUID.fromString(familyId));
    }

    public List<String> getFamilyIdFromRecipeBook(int recipeBookId) {
        String sql = """
                SELECT
                    family_id
                FROM
                    family_recipe_book
                WHERE
                    recipe_book_id = ?;
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("family_id"), recipeBookId);
    }

    public List<Recipe> getRecipesForRecipeBook(int recipeBookId) {
        String sql = """
                SELECT
                    r.id,
                    r.recipe_book_id as "recipeBookId",
                    r.name,
                    r.description,
                    r.url,
                    r.created_at as "createdAt",
                    r.updated_at as "updatedAt"
                FROM
                    recipes r
                WHERE
                    r.recipe_book_id = ?;
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            return new Recipe(
                    rs.getInt("id"),
                    rs.getInt("recipeBookId"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getString("url"),
                    rs.getTimestamp("createdAt").toLocalDateTime(),
                    rs.getTimestamp("updatedAt").toLocalDateTime());
        }, recipeBookId);
    }

    @Transactional
    public RecipeBook createRecipeBook(String familyId, String name) {
        String sql = """
                WITH new_book AS (
                    INSERT INTO recipe_books (name)
                    VALUES (?)
                    RETURNING *
                ),
                new_relation AS (
                    INSERT INTO family_recipe_book (family_id, recipe_book_id)
                    SELECT ?, id FROM new_book
                )
                SELECT * FROM new_book;
                """;

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            return new RecipeBook(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getTimestamp("updated_at").toLocalDateTime());
        }, name, java.util.UUID.fromString(familyId));
    }

    public RecipeBook updateRecipeBook(int recipeBookId, String name) {
        String sql = """
                UPDATE
                    recipe_books
                SET
                    name = ?,
                    updated_at = NOW()
                WHERE
                    id = ?
                RETURNING *;
                """;
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            return new RecipeBook(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getTimestamp("updated_at").toLocalDateTime());
        }, name, recipeBookId);
    }

    @Transactional
    public void deleteRecipeBook(int recipeBookId) {
        String sql = """
                WITH deleted_relation AS (
                    DELETE FROM family_recipe_book WHERE recipe_book_id = ?
                ),
                deleted_recipes AS (
                    DELETE FROM recipes WHERE recipe_book_id = ? RETURNING id
                ),
                updated_meal_plans AS (
                    UPDATE meal_plan_items SET recipe_id = NULL WHERE recipe_id IN (SELECT id FROM deleted_recipes)
                )
                DELETE FROM recipe_books WHERE id = ?;
                """;
        jdbcTemplate.update(sql, recipeBookId, recipeBookId, recipeBookId);
    }

    public int getRecipeBookIdFromRecipeId(int recipeId) {
        String sql = """
                SELECT
                    recipe_book_id
                FROM
                    recipes
                WHERE
                    id = ?;
                """;
        return jdbcTemplate.queryForObject(sql, Integer.class, recipeId);
    }

    public Recipe updateRecipe(int recipeId, String name, String description, String url) {
        String sql = """
                UPDATE
                    recipes
                SET
                    name = ?,
                    description = ?,
                    url = ?,
                    updated_at = NOW()
                WHERE
                    id = ?
                RETURNING *;
                """;
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            return new Recipe(
                    rs.getInt("id"),
                    rs.getInt("recipe_book_id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getString("url"),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getTimestamp("updated_at").toLocalDateTime());
        }, name, description, url, recipeId);
    }

    public Recipe createRecipe(String name, String description, String url, int recipeBookId) {
        String sql = """
                INSERT INTO recipes (name, description, url, recipe_book_id)
                VALUES (?, ?, ?, ?)
                RETURNING *;
                """;
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            return new Recipe(
                    rs.getInt("id"),
                    rs.getInt("recipe_book_id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getString("url"),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getTimestamp("updated_at").toLocalDateTime());
        }, name, description, url, recipeBookId);
    }

    public void deleteRecipe(int recipeId) {
        String sql = """
                WITH updated_meal_plans AS (
                    UPDATE meal_plan_items SET recipe_id = NULL WHERE recipe_id = ?
                )
                DELETE FROM recipes WHERE id = ?;
                """;
        jdbcTemplate.update(sql, recipeId, recipeId);
    }

    public List<Recipe> getAllRecipesForFamily(String familyId) {
        String sql = """
                SELECT
                    r.id,
                    r.recipe_book_id as "recipeBookId",
                    r.name,
                    r.description,
                    r.url,
                    r.created_at as "createdAt",
                    r.updated_at as "updatedAt"
                FROM
                    recipes r
                JOIN family_recipe_book fb ON r.recipe_book_id = fb.recipe_book_id
                WHERE
                    fb.family_id = ?;
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            return new Recipe(
                    rs.getInt("id"),
                    rs.getInt("recipeBookId"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getString("url"),
                    rs.getTimestamp("createdAt").toLocalDateTime(),
                    rs.getTimestamp("updatedAt").toLocalDateTime());
        }, java.util.UUID.fromString(familyId));
    }

    public MealPlanItem createMealPlanItem(
        String familyId, 
        Integer recipeId,
        String name, 
        LocalDate date, 
        LocalTime time, 
        MealType mealType
    ) {
        String sql = """
                INSERT INTO meal_plan_items (family_id, recipe_id, name, date, time, meal_type)
                VALUES (?::uuid, ?, ?, ?, ?, ?::meal_type)
                RETURNING *;
                """;
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            return new MealPlanItem(
                    rs.getInt("id"),
                    rs.getString("family_id"),
                    rs.getInt("recipe_id"),
                    rs.getString("name"),
                    rs.getDate("date").toLocalDate(),
                    rs.getTime("time").toLocalTime(),
                    MealType.valueOf(rs.getString("meal_type").toUpperCase()),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getTimestamp("updated_at").toLocalDateTime());
        }, familyId, recipeId, name, date, time, mealType.name().toLowerCase());
    }

    public List<Recipe> searchRecipesForFamily(String familyId, String searchQuery) {
        String sql = """
                SELECT
                    r.id,
                    r.recipe_book_id as "recipeBookId",
                    r.name,
                    r.description,
                    r.url,
                    r.created_at as "createdAt",
                    r.updated_at as "updatedAt"
                FROM
                    recipes r
                JOIN family_recipe_book fb ON r.recipe_book_id = fb.recipe_book_id
                WHERE
                    fb.family_id = ?
                    AND (
                        r.name ILIKE ?
                        OR r.description ILIKE ?
                    );
                """;
        String searchPattern = "%" + searchQuery + "%";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            return new Recipe(
                    rs.getInt("id"),
                    rs.getInt("recipeBookId"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getString("url"),
                    rs.getTimestamp("createdAt").toLocalDateTime(),
                    rs.getTimestamp("updatedAt").toLocalDateTime());
        }, java.util.UUID.fromString(familyId), searchPattern, searchPattern);
    }

    public String getFamilyIdFromMealPlan(int mealPlanItemId) {
        String sql = """
                SELECT
                    family_id
                FROM
                    meal_plan_items
                WHERE
                    id = ?;
                """;
        return jdbcTemplate.queryForObject(sql, String.class, mealPlanItemId);
    }

    public MealPlanItem updateMealPlanItem(
        int mealPlanItemId, 
        Integer recipeId, 
        String name, 
        LocalDate date, 
        LocalTime time, 
        MealType mealType
    ) {
        String sql = """
                UPDATE
                    meal_plan_items
                SET
                    recipe_id = ?,
                    name = ?,
                    date = ?,
                    time = ?,
                    meal_type = ?::meal_type,
                    updated_at = NOW()
                WHERE
                    id = ?
                RETURNING *;
                """;
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            return new MealPlanItem(
                    rs.getInt("id"),
                    rs.getString("family_id"),
                    rs.getInt("recipe_id"),
                    rs.getString("name"),
                    rs.getDate("date").toLocalDate(),
                    rs.getTime("time").toLocalTime(),
                    MealType.valueOf(rs.getString("meal_type").toUpperCase()),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getTimestamp("updated_at").toLocalDateTime());
        }, recipeId, name, date, time, mealType.name().toLowerCase(), mealPlanItemId);
    }

    public List<Recipe> searchRecipes(int recipeBookId, String searchTerm) {
        String sql = """
                SELECT
                    r.id,
                    r.recipe_book_id as "recipeBookId",
                    r.name,
                    r.description,
                    r.url,
                    r.created_at as "createdAt",
                    r.updated_at as "updatedAt"
                FROM
                    recipes r
                WHERE
                    r.recipe_book_id = ?
                    AND (
                        r.name ILIKE ?
                        OR r.description ILIKE ?
                    );
                """;
        String searchPattern = "%" + searchTerm + "%";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            return new Recipe(
                    rs.getInt("id"),
                    rs.getInt("recipeBookId"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getString("url"),
                    rs.getTimestamp("createdAt").toLocalDateTime(),
                    rs.getTimestamp("updatedAt").toLocalDateTime());
        }, recipeBookId, searchPattern, searchPattern);
    }
}
