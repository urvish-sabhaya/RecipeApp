package com.example.recipeapp.activities;

import static com.example.recipeapp.utils.Constants.recipesCategoryList;
import static com.example.recipeapp.utils.FireStoreConstants.RECIPES;
import static com.example.recipeapp.utils.FireStoreConstants.RECIPE_INGREDIENTS;
import static com.example.recipeapp.utils.FireStoreConstants.RECIPE_STEPS;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipeapp.R;
import com.example.recipeapp.adapters.AddDeleteIngredientAdapter;
import com.example.recipeapp.adapters.AddDeleteRecipeStepsAdapter;
import com.example.recipeapp.adapters.AddDeleteSingleTextAdapter;
import com.example.recipeapp.models.Recipe;
import com.example.recipeapp.models.RecipeIngredient;
import com.example.recipeapp.models.RecipeStep;
import com.example.recipeapp.models.RecipeType;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.IOException;
import java.util.ArrayList;

public class AddRecipeActivity extends BaseActivity {

    FirebaseFirestore db;

    RelativeLayout add_recipe_image_rel;
    ImageView recipe_image, back_img;
    EditText edt_recipe_name, edt_recipe_description, cooking_minutes_edt, baking_minutes_edt,
            resting_minutes_edt, calories_edt, protein_edt, fat_edt, carb_edt;
    RadioGroup difficulty_radio_grp;
    Button upload_recipe_btn;
    Spinner recipe_type_spinner;

    /*Add Ingredient*/
    RecyclerView ingredients_recycler;
    EditText ingredient_name_edt, ingredient_measure_edt;
    Button add_ingredient_btn;

    /*Add Utensils*/
    RecyclerView utensils_recycler;
    EditText utensils_name_edt;
    Button add_utensils_btn;

    /*Add Recipe Steps*/
    RecyclerView steps_recycler;
    EditText new_recipe_description_edt;
    RoundedImageView new_recipe_step_image;
    RelativeLayout add_recipe_step_image_rel;
    Button add_recipe_step_btn;

    /*Add Tags*/
    RecyclerView tags_recycler;
    EditText new_tag_name_edt;
    Button add_tag_btn;

    Boolean isMainRecipeImage = false;
    String difficulty = "";
    ArrayList<RecipeIngredient> recipeIngredientsArray = new ArrayList<>();
    ArrayList<String> utensilsList = new ArrayList<>();
    ArrayList<String> tagsList = new ArrayList<>();
    ArrayList<RecipeStep> recipeStepsList = new ArrayList<>();
    Bitmap selectedRecipeStepImage = null;

    Recipe recipe = new Recipe();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        initView();

        setUpListeners();
    }

    private void setUpListeners() {
        add_recipe_image_rel.setOnClickListener(view -> {
            isMainRecipeImage = true;
            if (checkAndRequestPermissions(AddRecipeActivity.this)) {
                chooseImage(AddRecipeActivity.this);
            }
        });

        add_recipe_step_image_rel.setOnClickListener(view -> {
            if (checkAndRequestPermissions(AddRecipeActivity.this)) {
                chooseImage(AddRecipeActivity.this);
            }
        });

        difficulty_radio_grp.setOnCheckedChangeListener((radioGroup, id) -> {
            View radioB = radioGroup.findViewById(id);
            int position = radioGroup.indexOfChild(radioB);

            if (position == 0) {
                difficulty = "Easy";
            } else if (position == 1) {
                difficulty = "Medium";
            } else if (position == 2) {
                difficulty = "Hard";
            }
        });

        upload_recipe_btn.setOnClickListener(view -> {
            validateAndUploadRecipe();
        });

        add_ingredient_btn.setOnClickListener(view -> {
            validateAndAddIngredient();
        });

        add_utensils_btn.setOnClickListener(view -> {
            validateAndAddUtensil();
        });

        add_tag_btn.setOnClickListener(view -> {
            validateAndAddTag();
        });

        add_recipe_step_btn.setOnClickListener(view -> {
            validateAndAddRecipeStep();
        });
    }

    private void validateAndAddTag() {
        String tagName = new_tag_name_edt.getText().toString();
        if (TextUtils.isEmpty(tagName)) {
            Toast.makeText(this, "Please enter tag name", Toast.LENGTH_SHORT).show();
            return;
        }

        tagsList.add(tagName);
        tags_recycler.getAdapter().notifyDataSetChanged();

        new_tag_name_edt.setText("");
    }

    private void validateAndAddRecipeStep() {
        RecipeStep recipeStep = new RecipeStep();

        String recipeStepDescription = new_recipe_description_edt.getText().toString();
        if (TextUtils.isEmpty(recipeStepDescription)) {
            Toast.makeText(this, "Please enter recipe description", Toast.LENGTH_SHORT).show();
            return;
        }
        recipeStep.setStep_detail(recipeStepDescription);

        if (selectedRecipeStepImage == null) {
            Toast.makeText(this, "Please select recipe step image", Toast.LENGTH_SHORT).show();
            return;
        }
        recipeStep.setStep_image(generateBitmapToBase64(selectedRecipeStepImage));

        recipeStepsList.add(recipeStep);
        steps_recycler.getAdapter().notifyDataSetChanged();

        new_recipe_description_edt.setText("");
        new_recipe_step_image.setImageBitmap(null);
        selectedRecipeStepImage = null;
    }

    private void validateAndAddUtensil() {
        String utensilName = utensils_name_edt.getText().toString();
        if (TextUtils.isEmpty(utensilName)) {
            Toast.makeText(this, "Please enter utensil name", Toast.LENGTH_SHORT).show();
            return;
        }

        utensilsList.add(utensilName);
        utensils_recycler.getAdapter().notifyDataSetChanged();

        utensils_name_edt.setText("");
    }

    private void validateAndAddIngredient() {
        RecipeIngredient recipeIngredient = new RecipeIngredient();

        String ingredientName = ingredient_name_edt.getText().toString();
        if (TextUtils.isEmpty(ingredientName)) {
            Toast.makeText(this, "Please enter ingredient name", Toast.LENGTH_SHORT).show();
            return;
        }
        recipeIngredient.setMatric_for(ingredientName);

        String ingredientMeasure = ingredient_measure_edt.getText().toString();
        if (TextUtils.isEmpty(ingredientMeasure)) {
            Toast.makeText(this, "Please enter ingredient measure", Toast.LENGTH_SHORT).show();
            return;
        }
        recipeIngredient.setMetric(ingredientMeasure);

        recipeIngredientsArray.add(recipeIngredient);
        ingredients_recycler.getAdapter().notifyDataSetChanged();

        ingredient_name_edt.setText("");
        ingredient_measure_edt.setText("");
    }

    private void validateAndUploadRecipe() {
        if (TextUtils.isEmpty(recipe.getRecipe_image())) {
            Toast.makeText(this, "Please select recipe image", Toast.LENGTH_SHORT).show();
            return;
        }

        String recipeName = edt_recipe_name.getText().toString();
        if (TextUtils.isEmpty(recipeName)) {
            Toast.makeText(this, "Please enter recipe name", Toast.LENGTH_SHORT).show();
            return;
        }
        recipe.setRecipe_name(recipeName);

        String recipeDescription = edt_recipe_description.getText().toString();
        if (TextUtils.isEmpty(recipeDescription)) {
            Toast.makeText(this, "Please enter recipe description", Toast.LENGTH_SHORT).show();
            return;
        }
        recipe.setRecipe_details(recipeDescription);

        if (TextUtils.isEmpty(difficulty)) {
            Toast.makeText(this, "Please select difficulty level", Toast.LENGTH_SHORT).show();
            return;
        }
        recipe.setRecipe_difficulty(difficulty);

        String cookingTime = cooking_minutes_edt.getText().toString();
        if (TextUtils.isEmpty(cookingTime)) {
            Toast.makeText(this, "Please enter cooking time", Toast.LENGTH_SHORT).show();
            return;
        }
        recipe.setCooking_time(Integer.parseInt(cookingTime));

        String bakingTime = baking_minutes_edt.getText().toString();
        if (TextUtils.isEmpty(bakingTime)) {
            Toast.makeText(this, "Please enter baking time", Toast.LENGTH_SHORT).show();
            return;
        }
        recipe.setBaking_time(Integer.parseInt(bakingTime));

        String restingTime = resting_minutes_edt.getText().toString();
        if (TextUtils.isEmpty(restingTime)) {
            Toast.makeText(this, "Please enter resting time", Toast.LENGTH_SHORT).show();
            return;
        }
        recipe.setResting_time(Integer.parseInt(restingTime));

        if (recipeIngredientsArray.isEmpty()) {
            Toast.makeText(this, "Please add ingredients", Toast.LENGTH_SHORT).show();
            return;
        }

        if (utensilsList.isEmpty()) {
            Toast.makeText(this, "Please add utensils", Toast.LENGTH_SHORT).show();
            return;
        }
        recipe.setUtensils(utensilsList);

        String calories = calories_edt.getText().toString();
        if (TextUtils.isEmpty(calories)) {
            Toast.makeText(this, "Please enter calories", Toast.LENGTH_SHORT).show();
            return;
        }
        recipe.setNutrition_calorie(Integer.parseInt(calories));

        String protein = protein_edt.getText().toString();
        if (TextUtils.isEmpty(protein)) {
            Toast.makeText(this, "Please enter protein", Toast.LENGTH_SHORT).show();
            return;
        }
        recipe.setNutrition_protein(Integer.parseInt(protein));

        String fat = fat_edt.getText().toString();
        if (TextUtils.isEmpty(fat)) {
            Toast.makeText(this, "Please enter fat", Toast.LENGTH_SHORT).show();
            return;
        }
        recipe.setNutrition_fat(Integer.parseInt(fat));

        String carb = carb_edt.getText().toString();
        if (TextUtils.isEmpty(carb)) {
            Toast.makeText(this, "Please enter carb", Toast.LENGTH_SHORT).show();
            return;
        }
        recipe.setNutrition_carb(Integer.parseInt(carb));

        if (recipeStepsList.isEmpty()) {
            Toast.makeText(this, "Please add recipe steps", Toast.LENGTH_SHORT).show();
            return;
        }

        if (tagsList.isEmpty()) {
            Toast.makeText(this, "Please add tags", Toast.LENGTH_SHORT).show();
            return;
        }
        recipe.setTags(tagsList);

        recipe.setUploaded_by_email("urvishsabhaya@yahoo.com");
        recipe.setUploaded_by_image("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");
        recipe.setUploaded_by_name("Urvish Sabhaya");

        uploadRecipe(recipe);
    }

    private void uploadRecipe(Recipe recipe) {
        showProgressDialog();
        db.collection(RECIPES)
                .add(recipe)
                .addOnSuccessListener(documentReference -> {
                    uploadIngredients(documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e("failureListener", e.toString());
                    Toast.makeText(AddRecipeActivity.this, "Please Try Again", Toast.LENGTH_SHORT).show();
                    hideProgressDialog();
                });
    }

    private void uploadIngredients(String documentId) {
        CollectionReference collectionReference = db.collection(RECIPES).document(documentId).collection(RECIPE_INGREDIENTS);
        for (int i = 0; i < recipeIngredientsArray.size(); i++) {
            collectionReference.add(recipeIngredientsArray.get(i))
                    .addOnSuccessListener(documentReference -> {

                    })
                    .addOnFailureListener(e -> {
                        hideProgressDialog();
                        Log.e("failureListener", e.toString());
                        Toast.makeText(AddRecipeActivity.this, "Please Try Again", Toast.LENGTH_SHORT).show();
                    });

            if (i == recipeIngredientsArray.size() - 1) {
                uploadSteps(documentId);
            }
        }
    }

    private void uploadSteps(String documentId) {
        CollectionReference collectionReference = db.collection(RECIPES).document(documentId).collection(RECIPE_STEPS);
        for (int i = 0; i < recipeStepsList.size(); i++) {
            collectionReference.add(recipeStepsList.get(i))
                    .addOnSuccessListener(documentReference -> {

                    })
                    .addOnFailureListener(e -> {
                        hideProgressDialog();
                        Log.e("failureListener", e.toString());
                        Toast.makeText(AddRecipeActivity.this, "Please Try Again", Toast.LENGTH_SHORT).show();
                    });

            if (i == recipeStepsList.size() - 1) {
                Toast.makeText(AddRecipeActivity.this, "Your recipe has been uploaded successfully", Toast.LENGTH_SHORT).show();
                hideProgressDialog();
                onBackPressed();
            }
        }
    }

    private void initView() {
        db = FirebaseFirestore.getInstance();

        add_recipe_image_rel = findViewById(R.id.add_recipe_image_rel);
        recipe_image = findViewById(R.id.recipe_image);
        ingredients_recycler = findViewById(R.id.ingredients_recycler);
        ingredient_name_edt = findViewById(R.id.ingredient_name_edt);
        ingredient_measure_edt = findViewById(R.id.ingredient_measure_edt);
        add_ingredient_btn = findViewById(R.id.add_ingredient_btn);
        utensils_recycler = findViewById(R.id.utensils_recycler);
        utensils_name_edt = findViewById(R.id.utensils_name_edt);
        add_utensils_btn = findViewById(R.id.add_utensils_btn);
        steps_recycler = findViewById(R.id.steps_recycler);
        new_recipe_description_edt = findViewById(R.id.new_recipe_description_edt);
        new_recipe_step_image = findViewById(R.id.new_recipe_step_image);
        add_recipe_step_image_rel = findViewById(R.id.add_recipe_step_image_rel);
        add_recipe_step_btn = findViewById(R.id.add_recipe_step_btn);
        tags_recycler = findViewById(R.id.tags_recycler);
        new_tag_name_edt = findViewById(R.id.new_tag_name_edt);
        add_tag_btn = findViewById(R.id.add_tag_btn);
        edt_recipe_name = findViewById(R.id.edt_recipe_name);
        edt_recipe_description = findViewById(R.id.edt_recipe_description);
        difficulty_radio_grp = findViewById(R.id.difficulty_radio_grp);
        cooking_minutes_edt = findViewById(R.id.cooking_minutes_edt);
        baking_minutes_edt = findViewById(R.id.baking_minutes_edt);
        resting_minutes_edt = findViewById(R.id.resting_minutes_edt);
        calories_edt = findViewById(R.id.calories_edt);
        protein_edt = findViewById(R.id.protein_edt);
        fat_edt = findViewById(R.id.fat_edt);
        carb_edt = findViewById(R.id.carb_edt);
        back_img = findViewById(R.id.back_img);
        upload_recipe_btn = findViewById(R.id.upload_recipe_btn);
        recipe_type_spinner = findViewById(R.id.recipe_type_spinner);

        back_img.setOnClickListener(view -> finish());

        steps_recycler.setLayoutManager(new LinearLayoutManager(this));
        steps_recycler.setAdapter(new AddDeleteRecipeStepsAdapter(this, recipeStepsList, recipeStep -> {
            recipeStepsList.remove(recipeStep);
            steps_recycler.getAdapter().notifyDataSetChanged();
        }));

        utensils_recycler.setLayoutManager(new LinearLayoutManager(this));
        utensils_recycler.setAdapter(new AddDeleteSingleTextAdapter(this, utensilsList, itemName -> {
            utensilsList.remove(itemName);
            utensils_recycler.getAdapter().notifyDataSetChanged();
        }));

        tags_recycler.setLayoutManager(new LinearLayoutManager(this));
        tags_recycler.setAdapter(new AddDeleteSingleTextAdapter(this, tagsList, itemName -> {
            tagsList.remove(itemName);
            tags_recycler.getAdapter().notifyDataSetChanged();
        }));

        ingredients_recycler.setLayoutManager(new LinearLayoutManager(this));
        ingredients_recycler.setAdapter(new AddDeleteIngredientAdapter(this, recipeIngredientsArray, recipeIngredient -> {
            recipeIngredientsArray.remove(recipeIngredient);
            ingredients_recycler.getAdapter().notifyDataSetChanged();
        }));

        ArrayAdapter<RecipeType> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, recipesCategoryList);
        recipe_type_spinner.setAdapter(adapter);
        recipe_type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                recipe.setRecipe_type(recipesCategoryList.get(position).getRecipe_id());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        setImage(selectedImage);
                    }
                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage = data.getData();
                        if (selectedImage != null) {
                            Bitmap selectedImageBitmap = null;
                            try {
                                selectedImageBitmap
                                        = MediaStore.Images.Media.getBitmap(
                                        this.getContentResolver(),
                                        selectedImage);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (selectedImageBitmap != null) {
                                setImage(selectedImageBitmap);
                            }
                        }
                    }
                    break;
            }
        }
    }

    private void setImage(Bitmap selectedImage) {
        if (isMainRecipeImage) {
            recipe_image.setImageBitmap(selectedImage);
            isMainRecipeImage = false;
            recipe.setRecipe_image(generateBitmapToBase64(selectedImage));
        } else {
            new_recipe_step_image.setImageBitmap(selectedImage);
            selectedRecipeStepImage = selectedImage;
        }
    }
}