package com.example.recipeapp.models;

import com.google.firebase.firestore.Exclude;

public class RecipeIngredient {

    String document_id;

    String matric_for, metric;

    public RecipeIngredient() {
    }

    @Exclude
    public String getDocument_id() {
        return document_id;
    }

    public void setDocument_id(String document_id) {
        this.document_id = document_id;
    }

    public String getMatric_for() {
        return matric_for;
    }

    public void setMatric_for(String matric_for) {
        this.matric_for = matric_for;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }
}
