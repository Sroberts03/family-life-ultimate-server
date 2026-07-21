package com.app.meal.types;

import java.time.LocalDateTime;
import java.util.List;

public class Recipe {
    int id;
    int recipeBookId;
    String name;
    String description;
    List<RecipeIngredient> ingredients;
    List<RecipeStep> instructions;
    int prepTime;
    int cookTime;
    int servings;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    public Recipe(
        int id,
        int recipeBookId,
        String name,
        String description,
        List<RecipeIngredient> ingredients,
        List<RecipeStep> instructions,
        int prepTime,
        int cookTime,
        int servings,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {
        this.id = id;
        this.recipeBookId = recipeBookId;
        this.name = name;
        this.description = description;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.prepTime = prepTime;
        this.cookTime = cookTime;
        this.servings = servings;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRecipe_book_id() {
        return recipeBookId;
    }

    public void setRecipe_book_id(int recipe_book_id) {
        this.recipeBookId = recipe_book_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<RecipeIngredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<RecipeIngredient> ingredients) {
        this.ingredients = ingredients;
    }

    public List<RecipeStep> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<RecipeStep> instructions) {
        this.instructions = instructions;
    }

    public int getPrepTime() {
        return prepTime;
    }

    public void setPrepTime(int prepTime) {
        this.prepTime = prepTime;
    }

    public int getCookTime() {
        return cookTime;
    }

    public void setCookTime(int cookTime) {
        this.cookTime = cookTime;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
