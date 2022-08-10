package com.example.recipeapp.activities;

import static com.example.recipeapp.utils.Constants.RECIPE_MODEL;
import static com.example.recipeapp.utils.FireStoreConstants.RECIPES;
import static com.example.recipeapp.utils.FireStoreConstants.RECIPE_INGREDIENTS;
import static com.example.recipeapp.utils.FireStoreConstants.RECIPE_STEPS;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.recipeapp.R;
import com.example.recipeapp.adapters.IngredientAdapter;
import com.example.recipeapp.adapters.RecipeStepsAdapter;
import com.example.recipeapp.models.Recipe;
import com.example.recipeapp.models.RecipeIngredient;
import com.example.recipeapp.models.RecipeStep;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ViewRecipeActivity extends BaseActivity {

    /*Declared all views used in the layout*/
    ImageView back_img, recipe_image, img_user_image;
    TextView txt_recipe_name, txt_user_name, txt_recipe_category, recipe_details_txt,
            difficulty_txt, cooking_min_txt, baking_min_txt, resting_min_txt, utensils_txt,
            carb_txt, fat_txt, protein_txt, cal_txt;
    CircularProgressIndicator cooking_progress, resting_progress, baking_progress;
    RecyclerView ingredients_recycler, steps_recycler;

    FirebaseFirestore db;

    ChipGroup tags_chips_group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recipe);

        db = FirebaseFirestore.getInstance();

        /*Initialised all used views in the screen*/
        initViews();


        if (getIntent().getExtras().containsKey(RECIPE_MODEL)) {

            Recipe recipe = (Recipe) getIntent().getSerializableExtra(RECIPE_MODEL);

            /*Bind all data with views*/
            bindData(recipe);

            /*fetch steps data*/
            fetchSteps(recipe);

            /*fetch steps data*/
            fetchIngredients(recipe);
        }

        back_img.setOnClickListener(view -> {
            finish();
        });
    }

    private void fetchSteps(Recipe recipe) {
        showProgressDialog();
        db.collection(RECIPES)
                .document(recipe.getDocument_id())
                .collection(RECIPE_STEPS)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<RecipeStep> recipeSteps = task.getResult().toObjects(RecipeStep.class);
                        steps_recycler.setAdapter(new RecipeStepsAdapter(this, recipeSteps));
                    }
                    hideProgressDialog();
                }).addOnFailureListener(e -> {
                    hideProgressDialog();
                });
    }

    private void fetchIngredients(Recipe recipe) {
        showProgressDialog();
        db.collection(RECIPES)
                .document(recipe.getDocument_id())
                .collection(RECIPE_INGREDIENTS)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<RecipeIngredient> recipeIngredients = task.getResult().toObjects(RecipeIngredient.class);
                        ingredients_recycler.setAdapter(new IngredientAdapter(this, recipeIngredients));
                    }
                    hideProgressDialog();
                }).addOnFailureListener(e -> {
                    hideProgressDialog();
                });
    }

    private void bindData(Recipe recipe) {
        if (!TextUtils.isEmpty(recipe.getRecipe_image())) {
            Glide.with(this)
                    .load(Base64.decode(recipe.getRecipe_image(), Base64.DEFAULT))
                    .into(recipe_image);
        }

        txt_recipe_name.setText(recipe.getRecipe_name());

        if (!TextUtils.isEmpty(recipe.getUploaded_by_image())) {
            Glide.with(this)
                    .load(Base64.decode(recipe.getUploaded_by_image(), Base64.DEFAULT))
                    .into(img_user_image);
        }

        txt_user_name.setText(recipe.getUploaded_by_name());
        txt_recipe_category.setText(getRecipeType(recipe.getRecipe_type()));
        recipe_details_txt.setText(recipe.getRecipe_details());
        difficulty_txt.setText(recipe.getRecipe_difficulty());

        cooking_min_txt.setText(recipe.getCooking_time() + " min.");
        cooking_progress.setProgress(recipe.getCooking_time());

        baking_min_txt.setText(recipe.getBaking_time() + " min.");
        baking_progress.setProgress(recipe.getBaking_time());

        resting_min_txt.setText(recipe.getResting_time() + " min.");
        resting_progress.setProgress(recipe.getResting_time());

        utensils_txt.setText(getStringFromArray(recipe.getUtensils()));

        cal_txt.setText("Cal\n" + recipe.getNutrition_calorie());
        protein_txt.setText("Protein\n" + recipe.getNutrition_protein());
        fat_txt.setText("Fat\n" + recipe.getNutrition_fat());
        carb_txt.setText("Carb\n" + recipe.getNutrition_carb());

        for (int i = 0; i < recipe.getTags().size(); i++) {
            Chip chip = new Chip(this);
            chip.setText("#" + recipe.getTags().get(i));
            chip.setId(i);
            chip.setTextColor(getResources().getColor(R.color.chips_font));
            int textSize = getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._5sdp);
            chip.setTextSize(textSize);
            tags_chips_group.addView(chip);
        }
    }

    private String getStringFromArray(ArrayList<String> utensils) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < utensils.size(); i++) {
            String item = utensils.get(i);

            if (i == utensils.size() - 1) {
                stringBuilder.append(item);
            } else {
                stringBuilder.append(item + ", ");
            }
        }
        return stringBuilder.toString();
    }

    private void initViews() {
        back_img = findViewById(R.id.back_img);
        recipe_image = findViewById(R.id.recipe_image);
        txt_recipe_name = findViewById(R.id.txt_recipe_name);
        img_user_image = findViewById(R.id.img_user_image);
        txt_user_name = findViewById(R.id.txt_user_name);
        txt_recipe_category = findViewById(R.id.txt_recipe_category);
        recipe_details_txt = findViewById(R.id.recipe_details_txt);
        difficulty_txt = findViewById(R.id.difficulty_txt);
        cooking_min_txt = findViewById(R.id.cooking_min_txt);
        cooking_progress = findViewById(R.id.cooking_progress);
        baking_progress = findViewById(R.id.baking_progress);
        baking_min_txt = findViewById(R.id.baking_min_txt);
        resting_progress = findViewById(R.id.resting_progress);
        resting_min_txt = findViewById(R.id.resting_min_txt);
        ingredients_recycler = findViewById(R.id.ingredients_recycler);
        utensils_txt = findViewById(R.id.utensils_txt);
        steps_recycler = findViewById(R.id.steps_recycler);
        carb_txt = findViewById(R.id.carb_txt);
        fat_txt = findViewById(R.id.fat_txt);
        protein_txt = findViewById(R.id.protein_txt);
        cal_txt = findViewById(R.id.cal_txt);
        tags_chips_group = findViewById(R.id.tags_chips_group);

        ingredients_recycler.setLayoutManager(new LinearLayoutManager(this));
        steps_recycler.setLayoutManager(new LinearLayoutManager(this));

        ingredients_recycler.setNestedScrollingEnabled(false);
        steps_recycler.setNestedScrollingEnabled(false);
    }
}