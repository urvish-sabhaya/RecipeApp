package com.example.recipeapp.models;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.util.ArrayList;

public class Recipe implements Serializable {

    String document_id, recipe_image, recipe_name, uploaded_by_name, uploaded_by_image, recipe_details,
            recipe_difficulty, uploaded_by_email, recipe_type;

    int baking_time, cooking_time, resting_time, nutrition_calorie,
            nutrition_carb, nutrition_fat, nutrition_protein;

    ArrayList<String> tags, utensils;

    public Recipe() {
    }

    public String getUploaded_by_email() {
        return uploaded_by_email;
    }

    public void setUploaded_by_email(String uploaded_by_email) {
        this.uploaded_by_email = uploaded_by_email;
    }

    @Exclude
    public String getDocument_id() {
        return document_id;
    }

    public void setDocument_id(String document_id) {
        this.document_id = document_id;
    }

    public String getRecipe_details() {
        return recipe_details;
    }

    public void setRecipe_details(String recipe_details) {
        this.recipe_details = recipe_details;
    }

    public String getRecipe_difficulty() {
        return recipe_difficulty;
    }

    public void setRecipe_difficulty(String recipe_difficulty) {
        this.recipe_difficulty = recipe_difficulty;
    }

    public int getBaking_time() {
        return baking_time;
    }

    public void setBaking_time(int baking_time) {
        this.baking_time = baking_time;
    }

    public int getCooking_time() {
        return cooking_time;
    }

    public void setCooking_time(int cooking_time) {
        this.cooking_time = cooking_time;
    }

    public int getResting_time() {
        return resting_time;
    }

    public void setResting_time(int resting_time) {
        this.resting_time = resting_time;
    }

    public int getNutrition_calorie() {
        return nutrition_calorie;
    }

    public void setNutrition_calorie(int nutrition_calorie) {
        this.nutrition_calorie = nutrition_calorie;
    }

    public int getNutrition_carb() {
        return nutrition_carb;
    }

    public void setNutrition_carb(int nutrition_carb) {
        this.nutrition_carb = nutrition_carb;
    }

    public int getNutrition_fat() {
        return nutrition_fat;
    }

    public void setNutrition_fat(int nutrition_fat) {
        this.nutrition_fat = nutrition_fat;
    }

    public int getNutrition_protein() {
        return nutrition_protein;
    }

    public void setNutrition_protein(int nutrition_protein) {
        this.nutrition_protein = nutrition_protein;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public ArrayList<String> getUtensils() {
        return utensils;
    }

    public void setUtensils(ArrayList<String> utensils) {
        this.utensils = utensils;
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

    public String getRecipe_type() {
        return recipe_type;
    }

    public void setRecipe_type(String recipe_type) {
        this.recipe_type = recipe_type;
    }

    @Exclude
    public int getRecipe_total_minute() {
        return getBaking_time() + getResting_time() + getCooking_time();
    }
}
