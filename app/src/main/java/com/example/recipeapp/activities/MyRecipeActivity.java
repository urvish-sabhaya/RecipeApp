package com.example.recipeapp.activities;

import static com.example.recipeapp.utils.FireStoreConstants.RECIPES;
import static com.example.recipeapp.utils.FireStoreConstants.UPLOADED_BY_EMAIL;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipeapp.R;
import com.example.recipeapp.adapters.MyRecipeAdapter;
import com.example.recipeapp.models.Recipe;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyRecipeActivity extends BaseActivity {

    ImageView back_img;
    FirebaseFirestore db;
    RecyclerView my_recipes_recycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_recipe);

        db = FirebaseFirestore.getInstance();

        back_img = findViewById(R.id.back_img);
        my_recipes_recycler = findViewById(R.id.my_recipes_recycler);
        my_recipes_recycler.setLayoutManager(new LinearLayoutManager(this));

        back_img.setOnClickListener(view -> {
            finish();
        });
    }

    private void fetchRecipes() {
        showProgressDialog();
        db.collection(RECIPES)
                .whereEqualTo(UPLOADED_BY_EMAIL, "urvishsabhaya@yahoo.com")
                .get()
                .addOnCompleteListener((Task<QuerySnapshot> task) -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Recipe> recipes = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                            Recipe recipe = documentSnapshot.toObject(Recipe.class);
                            recipe.setDocument_id(documentSnapshot.getId());
                            recipes.add(recipe);
                        }
                        my_recipes_recycler.setAdapter(new MyRecipeAdapter(this, recipes));
                    }
                    hideProgressDialog();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchRecipes();
    }
}