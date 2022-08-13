package com.example.recipeapp.models;

import com.google.firebase.firestore.Exclude;

public class RecipeStep {

    String document_id;

    String step_detail, step_image;

    public RecipeStep() {
    }

    @Exclude
    public String getDocument_id() {
        return document_id;
    }

    public void setDocument_id(String document_id) {
        this.document_id = document_id;
    }

    public String getStep_detail() {
        return step_detail;
    }

    public void setStep_detail(String step_detail) {
        this.step_detail = step_detail;
    }

    public String getStep_image() {
        return step_image;
    }

    public void setStep_image(String step_image) {
        this.step_image = step_image;
    }
}
