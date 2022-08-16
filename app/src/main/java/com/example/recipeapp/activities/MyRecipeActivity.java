package com.example.recipeapp.activities;

import static com.example.recipeapp.utils.FireStoreConstants.RECIPES;
import static com.example.recipeapp.utils.FireStoreConstants.UPLOADED_BY_EMAIL;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipeapp.R;
import com.example.recipeapp.adapters.MyRecipeAdapter;
import com.example.recipeapp.interfaces.DeleteRecipeInterface;
import com.example.recipeapp.models.Recipe;
import com.example.recipeapp.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyRecipeActivity extends BaseActivity implements DeleteRecipeInterface {

    ImageView back_img;
    FirebaseFirestore db;
    RecyclerView my_recipes_recycler;
    User currentUser;
    TextView no_recipes_txt;
    List<Recipe> recipeList = new ArrayList<>();

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
        recipeList.clear();

        showProgressDialog();
        db.collection(RECIPES)
                .whereEqualTo(UPLOADED_BY_EMAIL, currentUser.getUser_email())
                .get()
                .addOnCompleteListener((Task<QuerySnapshot> task) -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                            Recipe recipe = documentSnapshot.toObject(Recipe.class);
                            recipe.setDocument_id(documentSnapshot.getId());
                            recipeList.add(recipe);
                        }
                        my_recipes_recycler.setAdapter(new MyRecipeAdapter(this, recipeList, this));

                        manageNoData();
                    }
                    hideProgressDialog();
                });
    }

    private void manageNoData() {
        if (recipeList.isEmpty()) {
            no_recipes_txt.setVisibility(View.VISIBLE);
            my_recipes_recycler.setVisibility(View.GONE);
        } else {
            no_recipes_txt.setVisibility(View.GONE);
            my_recipes_recycler.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchRecipes();
    }

    @Override
    public void deleteRecipe(String recipeID, int position) {
        if (TextUtils.isEmpty(recipeID)) {
            Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show();
            return;
        }

        showProgressDialog();
        DocumentReference documentReference = db.collection(RECIPES).document(recipeID);
        documentReference.delete().addOnSuccessListener(unused -> {
            Toast.makeText(this, "Your recipe has been deleted successfully", Toast.LENGTH_SHORT).show();

            recipeList.remove(position);
            my_recipes_recycler.getAdapter().notifyItemRemoved(position);

            manageNoData();

            hideProgressDialog();
        }).addOnFailureListener(e -> {
            hideProgressDialog();
            Log.e("failureListener", e.toString());
            Toast.makeText(this, "Please Try Again", Toast.LENGTH_SHORT).show();
        });
    }
}