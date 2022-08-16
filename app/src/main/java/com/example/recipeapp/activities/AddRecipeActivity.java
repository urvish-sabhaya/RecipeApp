package com.example.recipeapp.activities;

import static com.example.recipeapp.utils.Constants.EDIT_RECIPE;
import static com.example.recipeapp.utils.Constants.recipesCategoryList;
import static com.example.recipeapp.utils.Constants.viewableRecipe;
import static com.example.recipeapp.utils.FireStoreConstants.RECIPES;
import static com.example.recipeapp.utils.FireStoreConstants.RECIPE_INGREDIENTS;
import static com.example.recipeapp.utils.FireStoreConstants.RECIPE_STEPS;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.recipeapp.R;
import com.example.recipeapp.adapters.AddDeleteIngredientAdapter;
import com.example.recipeapp.adapters.AddDeleteRecipeStepsAdapter;
import com.example.recipeapp.adapters.AddDeleteSingleTextAdapter;
import com.example.recipeapp.models.Recipe;
import com.example.recipeapp.models.RecipeIngredient;
import com.example.recipeapp.models.RecipeStep;
import com.example.recipeapp.models.RecipeType;
import com.example.recipeapp.models.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddRecipeActivity extends BaseActivity {

    FirebaseFirestore db;

    RelativeLayout add_recipe_image_rel;
    ImageView recipe_image, back_img;
    EditText edt_recipe_name, edt_recipe_description, cooking_minutes_edt, baking_minutes_edt,
            resting_minutes_edt, calories_edt, protein_edt, fat_edt, carb_edt;
    RadioGroup difficulty_radio_grp;
    Button upload_recipe_btn;
    Spinner recipe_type_spinner;
    TextView toolbar_title;

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

    User currentUser;

    boolean isThisEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        initView();

        manageIntent();

        setUpListeners();
    }

    private void manageIntent() {
        if (getIntent().getExtras() != null &&
                getIntent().getExtras().containsKey(EDIT_RECIPE) &&
                viewableRecipe != null) {
            isThisEdit = true;
            toolbar_title.setText("Edit Your Recipe");

//            Recipe recipe = (Recipe) getIntent().getSerializableExtra(EDIT_RECIPE);
            Recipe recipe = viewableRecipe;

            /*Bind all data with views*/
            bindData(recipe);

            /*fetch steps data*/
            fetchSteps(recipe);

            /*fetch steps data*/
            fetchIngredients(recipe);
        }
    }

    private void fetchSteps(Recipe recipe) {
        showProgressDialog();
        db.collection(RECIPES)
                .document(recipe.getDocument_id())
                .collection(RECIPE_STEPS)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<RecipeStep> recipeSteps = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                            RecipeStep recipeStep = documentSnapshot.toObject(RecipeStep.class);
                            recipeStep.setDocument_id(documentSnapshot.getId());
                            recipeSteps.add(recipeStep);
                        }

                        recipeStepsList.addAll(recipeSteps);
                        steps_recycler.setAdapter(new AddDeleteRecipeStepsAdapter(this, recipeStepsList, recipeStep -> {
                            recipeStepsList.remove(recipeStep);
                            steps_recycler.getAdapter().notifyDataSetChanged();

                            if (isThisEdit) {
                                deleteStepToFireStore(recipeStep);
                            }
                        }));
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

                        List<RecipeIngredient> recipeIngredients = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                            RecipeIngredient recipeIngredient = documentSnapshot.toObject(RecipeIngredient.class);
                            recipeIngredient.setDocument_id(documentSnapshot.getId());
                            recipeIngredients.add(recipeIngredient);
                        }

                        recipeIngredientsArray.addAll(recipeIngredients);
                        ingredients_recycler.setAdapter(new AddDeleteIngredientAdapter(this, recipeIngredientsArray, recipeIngredient -> {
                            recipeIngredientsArray.remove(recipeIngredient);
                            ingredients_recycler.getAdapter().notifyDataSetChanged();

                            if (isThisEdit) {
                                deleteIngredientToFireStore(recipeIngredient);
                            }
                        }));
                    }
                    hideProgressDialog();
                }).addOnFailureListener(e -> {
                    hideProgressDialog();
                });
    }

    private void bindData(Recipe recipe) {

        this.recipe.setDocument_id(recipe.getDocument_id());

        if (!TextUtils.isEmpty(recipe.getRecipe_image())) {
            Glide.with(this)
                    .load(Base64.decode(recipe.getRecipe_image(), Base64.DEFAULT))
                    .into(recipe_image);
            this.recipe.setRecipe_image(recipe.getRecipe_image());
        }

        edt_recipe_name.setText(recipe.getRecipe_name());

        this.recipe.setRecipe_type(recipe.getRecipe_type());

        edt_recipe_description.setText(recipe.getRecipe_details());

        difficulty = recipe.getRecipe_difficulty();

        if (recipe.getRecipe_difficulty().equalsIgnoreCase("Easy")) {
            difficulty_radio_grp.check(difficulty_radio_grp.getChildAt(0).getId());
        } else if (recipe.getRecipe_difficulty().equalsIgnoreCase("Medium")) {
            difficulty_radio_grp.check(difficulty_radio_grp.getChildAt(1).getId());
        } else if (recipe.getRecipe_difficulty().equalsIgnoreCase("Hard")) {
            difficulty_radio_grp.check(difficulty_radio_grp.getChildAt(2).getId());
        }

        cooking_minutes_edt.setText(String.valueOf(recipe.getCooking_time()));
        baking_minutes_edt.setText(String.valueOf(recipe.getBaking_time()));
        resting_minutes_edt.setText(String.valueOf(recipe.getResting_time()));

        utensilsList.addAll(recipe.getUtensils());

        calories_edt.setText(String.valueOf(recipe.getNutrition_calorie()));
        protein_edt.setText(String.valueOf(recipe.getNutrition_protein()));
        fat_edt.setText(String.valueOf(recipe.getNutrition_fat()));
        carb_edt.setText(String.valueOf(recipe.getNutrition_carb()));

        tagsList.addAll(recipe.getTags());
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

        if (isThisEdit) {
            addRecipeStepToFireStore(recipeStep);
        }
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

        if (isThisEdit) {
            addIngredientToFireStore(recipeIngredient);
        }
    }

    private void addIngredientToFireStore(RecipeIngredient recipeIngredient) {
        showProgressDialog();
        CollectionReference collectionReference = db.collection(RECIPES).document(recipe.getDocument_id()).collection(RECIPE_INGREDIENTS);
        collectionReference.add(recipeIngredient)
                .addOnSuccessListener(documentReference -> {
                    hideProgressDialog();
                })
                .addOnFailureListener(e -> {
                    hideProgressDialog();
                    Log.e("failureListener", e.toString());
                    Toast.makeText(AddRecipeActivity.this, "Please Try Again", Toast.LENGTH_SHORT).show();
                });
    }

    private void addRecipeStepToFireStore(RecipeStep recipeStep) {
        showProgressDialog();
        CollectionReference collectionReference = db.collection(RECIPES).document(recipe.getDocument_id()).collection(RECIPE_STEPS);
        collectionReference.add(recipeStep)
                .addOnSuccessListener(documentReference -> {
                    hideProgressDialog();
                })
                .addOnFailureListener(e -> {
                    hideProgressDialog();
                    Log.e("failureListener", e.toString());
                    Toast.makeText(AddRecipeActivity.this, "Please Try Again", Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteIngredientToFireStore(RecipeIngredient recipeIngredient) {
        showProgressDialog();
        DocumentReference documentReference = db.collection(RECIPES)
                .document(recipe.getDocument_id())
                .collection(RECIPE_INGREDIENTS)
                .document(recipeIngredient.getDocument_id());
        documentReference.delete().addOnSuccessListener(unused -> hideProgressDialog()).addOnFailureListener(e -> {
            hideProgressDialog();
            Log.e("failureListener", e.toString());
            Toast.makeText(AddRecipeActivity.this, "Please Try Again", Toast.LENGTH_SHORT).show();
        });
    }

    private void deleteStepToFireStore(RecipeStep recipeStep) {
        showProgressDialog();
        DocumentReference documentReference = db.collection(RECIPES)
                .document(recipe.getDocument_id())
                .collection(RECIPE_STEPS)
                .document(recipeStep.getDocument_id());
        documentReference.delete().addOnSuccessListener(unused -> hideProgressDialog()).addOnFailureListener(e -> {
            hideProgressDialog();
            Log.e("failureListener", e.toString());
            Toast.makeText(AddRecipeActivity.this, "Please Try Again", Toast.LENGTH_SHORT).show();
        });
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

        recipe.setUploaded_by_email(currentUser.getUser_email());
        if (TextUtils.isEmpty(currentUser.getUser_image())) {
            recipe.setUploaded_by_image("");
        } else {
            recipe.setUploaded_by_image(currentUser.getUser_image());
        }
        recipe.setUploaded_by_name(currentUser.getUser_name());

        if (isThisEdit) {
            editRecipe(recipe);
        } else {
            uploadRecipe(recipe);
        }
    }

    private void editRecipe(Recipe recipe) {
        showProgressDialog();
        db.collection(RECIPES)
                .document(recipe.getDocument_id())
                .set(recipe)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(AddRecipeActivity.this, "Your recipe has been updated successfully", Toast.LENGTH_SHORT).show();
                    hideProgressDialog();
                    onBackPressed();
                })
                .addOnFailureListener(e -> {
                    Log.e("failureListener", e.toString());
                    Toast.makeText(AddRecipeActivity.this, "Please Try Again", Toast.LENGTH_SHORT).show();
                    hideProgressDialog();
                });
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
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }

    private void initView() {
        db = FirebaseFirestore.getInstance();

        currentUser = appSharedPreference.getUserInfo();

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
        toolbar_title = findViewById(R.id.toolbar_title);

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
                recipe.setRecipe_type(recipesCategoryList.get(position).getDocument_id());
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