package com.example.recipeapp.activities;

import static com.example.recipeapp.utils.FireStoreConstants.RECIPES;
import static com.example.recipeapp.utils.FireStoreConstants.UPLOADED_BY_EMAIL;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipeapp.R;
import com.example.recipeapp.adapters.MyRecipeAdapter;
import com.example.recipeapp.models.Recipe;
import com.example.recipeapp.models.User;
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
    User currentUser;
    TextView no_recipes_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_recipe);

        db = FirebaseFirestore.getInstance();
        currentUser = appSharedPreference.getUserInfo();

        back_img = findViewById(R.id.back_img);
        no_recipes_txt = findViewById(R.id.no_recipes_txt);
        my_recipes_recycler = findViewById(R.id.my_recipes_recycler);
        my_recipes_recycler.setLayoutManager(new LinearLayoutManager(this));

        back_img.setOnClickListener(view -> {
            finish();
        });
    }

    private void fetchRecipes() {
        showProgressDialog();
        db.collection(RECIPES)
                .whereEqualTo(UPLOADED_BY_EMAIL, currentUser.getUser_email())
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

                        if (recipes.isEmpty()) {
                            no_recipes_txt.setVisibility(View.VISIBLE);
                            my_recipes_recycler.setVisibility(View.GONE);
                        } else {
                            no_recipes_txt.setVisibility(View.GONE);
                            my_recipes_recycler.setVisibility(View.VISIBLE);
                        }
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