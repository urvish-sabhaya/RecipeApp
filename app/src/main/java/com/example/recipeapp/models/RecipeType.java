package com.example.recipeapp.models;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class RecipeType {
    String recipe_name, document_id;

    public RecipeType() {
    }

    @Exclude
    public String getDocument_id() {
        return document_id;
    }

    public void setDocument_id(String document_id) {
        this.document_id = document_id;
    }

    public String getRecipe_name() {
        return recipe_name;
    }

    public void setRecipe_name(String recipe_name) {
        this.recipe_name = recipe_name;
    }

    @NonNull
    @Override
    public String toString() {
        return recipe_name;
    }
}
