package com.app.meal;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import com.app.meal.types.MealPlanItem;
import com.app.meal.types.MealType;
import com.app.meal.types.Recipe;
import com.app.meal.types.RecipeBook;
import com.app.meal.types.RecipeIngredient;
import com.app.meal.types.RecipeStep;

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
                    r.id AS recipe_id, 
                    r.recipe_book_id, 
                    r.name AS recipe_name, 
                    r.description, 
                    r.servings, 
                    r.prep_time, 
                    r.cook_time, 
                    r.created_at, 
                    r.updated_at,
                    
                    ri.id AS ingredient_id, 
                    ri.name AS ingredient_name, 
                    ri.quantity, 
                    ri.unit,
                    
                    rs.id AS step_id, 
                    rs.step_order, 
                    rs.step_text
                FROM recipes r
                LEFT JOIN recipe_ingredients ri ON r.id = ri.recipe_id
                LEFT JOIN recipe_steps rs ON r.id = rs.recipe_id
                WHERE r.id = ?;
                """;

        return jdbcTemplate.query(sql, rs -> {
            Recipe recipe = null;
            
            // We use Sets to track what we've already added so we don't create duplicates
            Set<Integer> addedIngredientIds = new HashSet<>();
            Set<Integer> addedStepIds = new HashSet<>();

            while (rs.next()) {
                // Initialize the core Recipe object on the very first row
                if (recipe == null) {
                    recipe = new Recipe(
                            rs.getInt("recipe_id"),
                            rs.getInt("recipe_book_id"),
                            rs.getString("recipe_name"),
                            rs.getString("description"),
                            new ArrayList<>(), // Empty list ready for ingredients
                            new ArrayList<>(), // Empty list ready for steps
                            rs.getInt("prep_time"),
                            rs.getInt("cook_time"),
                            rs.getInt("servings"),
                            rs.getTimestamp("created_at").toLocalDateTime(),
                            rs.getTimestamp("updated_at").toLocalDateTime());
                }

                // Extract the ingredient. 
                int ingredientId = rs.getInt("ingredient_id");
                // rs.wasNull() ensures we don't add an empty ingredient if the recipe has none
                if (!rs.wasNull() && addedIngredientIds.add(ingredientId)) {
                    recipe.getIngredients().add(new RecipeIngredient(
                            ingredientId,
                            rs.getString("ingredient_name"),
                            rs.getDouble("quantity"),
                            rs.getString("unit")));
                }

                // Extract the step.
                int stepId = rs.getInt("step_id");
                if (!rs.wasNull() && addedStepIds.add(stepId)) {
                    recipe.getInstructions().add(new RecipeStep(
                            stepId,
                            rs.getString("step_text"),
                            rs.getInt("step_order")));
                }
            }
            
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
                    r.servings,
                    r.prep_time as "prepTime",
                    r.cook_time as "cookTime",
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
                    new ArrayList<>(),
                    new ArrayList<>(),
                    rs.getInt("prepTime"),
                    rs.getInt("cookTime"),
                    rs.getInt("servings"),
                    rs.getTimestamp("createdAt").toLocalDateTime(),
                    rs.getTimestamp("updatedAt").toLocalDateTime());
        }, recipeBookId);
    }
}
