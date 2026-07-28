package com.app.meal.types;

import java.time.LocalDateTime;
import jakarta.annotation.Nullable;

public class Recipe {
    @Nullable Integer id;
    @Nullable Integer recipeBookId;
    String name;
    String description;
    String url;
    @Nullable LocalDateTime createdAt;
    @Nullable LocalDateTime updatedAt;

    public Recipe(
        Integer id,
        Integer recipeBookId,
        String name,
        String description,
        String url,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {
        this.id = id;
        this.recipeBookId = recipeBookId;
        this.name = name;
        this.description = description;
        this.url = url;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRecipeBookId() {
        return recipeBookId;
    }

    public void setRecipeBookId(Integer recipe_book_id) {
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
