package com.example.recipeapp.models;

public class Recipe {
    String recipe_image, recipe_name, uploaded_by_name, uploaded_by_image;
    int recipe_type, recipe_total_minute;

    public Recipe() {
    }

    public String getRecipe_image() {
        return recipe_image;
    }

    public void setRecipe_image(String recipe_image) {
        this.recipe_image = recipe_image;
    }

    public String getRecipe_name() {
        return recipe_name;
    }

    public void setRecipe_name(String recipe_name) {
        this.recipe_name = recipe_name;
    }

    public String getUploaded_by_name() {
        return uploaded_by_name;
    }

    public void setUploaded_by_name(String uploaded_by_name) {
        this.uploaded_by_name = uploaded_by_name;
    }

    public String getUploaded_by_image() {
        return uploaded_by_image;
    }

    public void setUploaded_by_image(String uploaded_by_image) {
        this.uploaded_by_image = uploaded_by_image;
    }

    public int getRecipe_type() {
        return recipe_type;
    }

    public void setRecipe_type(int recipe_type) {
        this.recipe_type = recipe_type;
    }

    public int getRecipe_total_minute() {
        return recipe_total_minute;
    }

    public void setRecipe_total_minute(int recipe_total_minute) {
        this.recipe_total_minute = recipe_total_minute;
    }
}
