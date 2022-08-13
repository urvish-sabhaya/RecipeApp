package com.example.recipeapp.activities;

import static com.example.recipeapp.utils.Constants.recipesCategoryList;
import static com.example.recipeapp.utils.FireStoreConstants.RECIPES;
import static com.example.recipeapp.utils.FireStoreConstants.RECIPES_TYPES;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipeapp.R;
import com.example.recipeapp.adapters.FilterAdapter;
import com.example.recipeapp.adapters.RecipeAdapter;
import com.example.recipeapp.models.Recipe;
import com.example.recipeapp.models.RecipeType;
import com.example.recipeapp.utils.Constants;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

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
    FloatingActionButton add_recipe_fab;
    EditText edt_search;
    RecipeAdapter recipeAdapter;
    ArrayList<Integer> selectedCategoriesFilter = new ArrayList<>();
    TextWatcher textWatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        db = FirebaseFirestore.getInstance();

        initViews();
        setClicks();
        fetchRecipeTypes();
    }

    private void fetchRecipeTypes() {
        db.collection(RECIPES_TYPES)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<RecipeType> recipesCategories = task.getResult().toObjects(RecipeType.class);
                        recipesCategoryList.clear();
                        recipesCategoryList.addAll(recipesCategories);
                    }
                });
    }

    private void fetchRecipes() {
        showProgressDialog();
        db.collection(RECIPES)
                .get()
                .addOnCompleteListener((Task<QuerySnapshot> task) -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Recipe> recipes = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                            Recipe recipe = documentSnapshot.toObject(Recipe.class);
                            recipe.setDocument_id(documentSnapshot.getId());
                            recipes.add(recipe);
                        }
                        recipesList.clear();
                        recipesList.addAll(recipes);
                        setUpRecipeList(recipesList);
                    }
                    hideProgressDialog();
                });
    }

    private void setUpRecipeList(ArrayList<Recipe> recipesList) {
        if (recipesList.isEmpty()) {
            no_recipes_txt.setVisibility(View.VISIBLE);
            recycler_recipes.setVisibility(View.GONE);
        } else {
            no_recipes_txt.setVisibility(View.GONE);
            recycler_recipes.setVisibility(View.VISIBLE);
        }
        recipeAdapter.setList(recipesList);
        recipeAdapter.notifyDataSetChanged();
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
        add_recipe_fab = findViewById(R.id.add_recipe_fab);
        edt_search = findViewById(R.id.edt_search);

        recycler_recipes.setLayoutManager(new LinearLayoutManager(this));
        recipeAdapter = new RecipeAdapter(this);
        recycler_recipes.setAdapter(recipeAdapter);
    }

    private void setClicks() {
        drawer_btn.setOnClickListener(this);
        setting_nav.setOnClickListener(this);
        share_nav.setOnClickListener(this);
        rating_nav.setOnClickListener(this);
        privacy_nav.setOnClickListener(this);
        profile_nav.setOnClickListener(this);
        rel_filter.setOnClickListener(this);
        add_recipe_fab.setOnClickListener(this);

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

        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                selectedCategoriesFilter.clear();
                searchRecipes(editable.toString().toLowerCase());
            }
        };

        edt_search.addTextChangedListener(textWatcher);
    }

    private void searchRecipes(String searchText) {
        if (TextUtils.isEmpty(searchText)) {
            setUpRecipeList(recipesList);
            return;
        }

        ArrayList<Recipe> searchList = new ArrayList<>();

        for (Recipe recipe : recipesList) {
            if (recipe.getRecipe_name().toLowerCase().contains(searchText)) {
                searchList.add(recipe);
            }
        }

        setUpRecipeList(searchList);
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
            case R.id.add_recipe_fab:
                startActivity(new Intent(this, AddRecipeActivity.class));
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

        FilterAdapter filterAdapter = new FilterAdapter(this, recipesCategoryList, selectedCategoriesFilter);

        txt_apply.setOnClickListener(view1 -> {
            edt_search.removeTextChangedListener(textWatcher);
            edt_search.setText("");
            edt_search.addTextChangedListener(textWatcher);

            filterDialog.dismiss();
            selectedCategoriesFilter = filterAdapter.selectedCategories;
            if (!selectedCategoriesFilter.isEmpty()) {
                filterRecipes(selectedCategoriesFilter);
            } else {
                setUpRecipeList(recipesList);
            }
        });
        close_btn.setOnClickListener(view12 -> filterDialog.dismiss());

        RecyclerView filter_items_recycler;
        filter_items_recycler = filterDialog.findViewById(R.id.filter_items_recycler);
        filter_items_recycler.setLayoutManager(new LinearLayoutManager(this));
        filter_items_recycler.setAdapter(filterAdapter);

        filterDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        filterDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        filterDialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        filterDialog.show();
    }

    private void filterRecipes(ArrayList<Integer> selectedCat) {
        ArrayList<Recipe> searchList = new ArrayList<>();

        for (Recipe recipe : recipesList) {
            if (selectedCat.contains(recipe.getRecipe_type())) {
                searchList.add(recipe);
            }
        }

        setUpRecipeList(searchList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchRecipes();
    }
}