package com.example.recipeapp.utils;

import com.example.recipeapp.models.Recipe;
import com.example.recipeapp.models.RecipeType;

import java.util.ArrayList;

public class Constants {
    public static String privacy_policy_url = "https://conestogac.apparmor.com/Privacy/";
    public static String rating_app_url = "https://play.google.com/store/apps/details?id=com.cutcom.apparmor.conestogac";
    public static String share_app_url = "https://play.google.com/store/apps/details?id=com.cutcom.apparmor.conestogac";
    public static String PREF_DATA_NAME = "CookBookSharedPref";
    public static String PREF_USER = "user";
    public static ArrayList<RecipeType> recipesCategoryList = new ArrayList<>();

    /*Intents*/
    final public static String VIEW_RECIPE = "view_recipe";
    final public static String EDIT_RECIPE = "edit_recipe";

    /*Recipe*/
    public static Recipe viewableRecipe;
}
