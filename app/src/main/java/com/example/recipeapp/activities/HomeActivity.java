package com.example.recipeapp.activities;

import static com.example.recipeapp.utils.Constants.recipesCategoryList;
import static com.example.recipeapp.utils.FireStoreConstants.RECIPES;
import static com.example.recipeapp.utils.FireStoreConstants.RECIPES_TYPES;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipeapp.R;
import com.example.recipeapp.adapters.FilterAdapter;
import com.example.recipeapp.adapters.RecipeAdapter;
import com.example.recipeapp.models.Recipe;
import com.example.recipeapp.models.RecipeType;
import com.example.recipeapp.utils.Constants;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseActivity implements View.OnClickListener {

    FirebaseFirestore db;
    ImageView drawer_btn;
    DrawerLayout drawer_layout;
    LinearLayout setting_nav, share_nav, rating_nav, privacy_nav, profile_nav;
    RelativeLayout rel_filter;
    ArrayList<Recipe> recipesList = new ArrayList<>();
    TextView no_recipes_txt;
    RecyclerView recycler_recipes;
    Dialog filterDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        db = FirebaseFirestore.getInstance();

        initViews();
        setClicks();
        fetchRecipes();
        fetchRecipeTypes();
    }

    private void fetchRecipeTypes() {
        db.collection(RECIPES_TYPES)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<RecipeType> recipesCategories = task.getResult().toObjects(RecipeType.class);
                        recipesCategoryList.addAll(recipesCategories);
                    }
                });
    }

    private void fetchRecipes() {
        showProgressDialog();
        db.collection(RECIPES)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Recipe> recipes = task.getResult().toObjects(Recipe.class);
                        recipesList.addAll(recipes);
                        setUpRecipeList(recipesList);
                    }
                    checkDataAvailability();
                    hideProgressDialog();
                });
    }

    private void setUpRecipeList(ArrayList<Recipe> recipesList) {
        recycler_recipes.setLayoutManager(new GridLayoutManager(this, 2));
        recycler_recipes.setAdapter(new RecipeAdapter(this, recipesList));
    }

    private void checkDataAvailability() {
        if (recipesList.isEmpty()) {
            no_recipes_txt.setVisibility(View.VISIBLE);
            recycler_recipes.setVisibility(View.GONE);
        } else {
            no_recipes_txt.setVisibility(View.GONE);
            recycler_recipes.setVisibility(View.VISIBLE);
        }
    }

    private void initViews() {
        drawer_btn = findViewById(R.id.drawer_btn);
        drawer_layout = findViewById(R.id.drawer_layout);
        setting_nav = findViewById(R.id.setting_nav);
        share_nav = findViewById(R.id.share_nav);
        rating_nav = findViewById(R.id.rating_nav);
        privacy_nav = findViewById(R.id.privacy_nav);
        profile_nav = findViewById(R.id.profile_nav);
        rel_filter = findViewById(R.id.rel_filter);
        no_recipes_txt = findViewById(R.id.no_recipes_txt);
        recycler_recipes = findViewById(R.id.recycler_recipes);
    }

    private void setClicks() {
        drawer_btn.setOnClickListener(this);
        setting_nav.setOnClickListener(this);
        share_nav.setOnClickListener(this);
        rating_nav.setOnClickListener(this);
        privacy_nav.setOnClickListener(this);
        profile_nav.setOnClickListener(this);
        rel_filter.setOnClickListener(this);

        drawer_layout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                hideKeyboard(HomeActivity.this);
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.drawer_btn:
                if (drawer_layout.isDrawerOpen(Gravity.LEFT)) {
                    drawer_layout.closeDrawer(Gravity.LEFT);
                } else {
                    drawer_layout.openDrawer(Gravity.LEFT);
                }
                break;
            case R.id.setting_nav:
                drawer_layout.closeDrawer(Gravity.LEFT);
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.share_nav:
                drawer_layout.closeDrawer(Gravity.LEFT);
                shareApp();
                break;
            case R.id.rating_nav:
                drawer_layout.closeDrawer(Gravity.LEFT);
                openWebPage(Constants.rating_app_url);
                break;
            case R.id.privacy_nav:
                drawer_layout.closeDrawer(Gravity.LEFT);
                openWebPage(Constants.privacy_policy_url);
                break;
            case R.id.profile_nav:
                drawer_layout.closeDrawer(Gravity.LEFT);
                startActivity(new Intent(this, ProfileActivity.class));
                break;
            case R.id.rel_filter:
                selectFilterDialog();
                break;
        }
    }

    public void shareApp() {
        String shareBody = "Download our app using this link " + Constants.share_app_url;
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share Vie"));
    }

    public void openWebPage(String url) {
        try {
            Uri webpage = Uri.parse(url);
            Intent myIntent = new Intent(Intent.ACTION_VIEW, webpage);
            startActivity(myIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No application can handle this request. Please install a web browser or check your URL.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public void selectFilterDialog() {
        if (filterDialog != null) {
            if (filterDialog.isShowing()) {
                return;
            }
        }

        View view = LayoutInflater.from(this).inflate(R.layout.apply_filter_lay, null);
        filterDialog = new Dialog(this);
        filterDialog.setCancelable(false);
        filterDialog.setContentView(view);

        TextView txt_apply;
        ImageView close_btn;
        txt_apply = filterDialog.findViewById(R.id.txt_apply);
        close_btn = filterDialog.findViewById(R.id.close_btn);

        txt_apply.setOnClickListener(view1 -> filterDialog.dismiss());
        close_btn.setOnClickListener(view12 -> filterDialog.dismiss());

        RecyclerView filter_items_recycler;
        filter_items_recycler = filterDialog.findViewById(R.id.filter_items_recycler);
        filter_items_recycler.setLayoutManager(new LinearLayoutManager(this));
        filter_items_recycler.setAdapter(new FilterAdapter(this, recipesCategoryList));

        filterDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        filterDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        filterDialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        filterDialog.show();
    }
}