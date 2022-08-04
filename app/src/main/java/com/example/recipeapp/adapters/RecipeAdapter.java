package com.example.recipeapp.adapters;

import static com.example.recipeapp.activities.BaseActivity.getRecipeType;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.recipeapp.R;
import com.example.recipeapp.activities.ViewRecipeActivity;
import com.example.recipeapp.models.Recipe;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    Context context;
    ArrayList<Recipe> recipeList = new ArrayList<>();

    public RecipeAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.recipe_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);
        if (!TextUtils.isEmpty(recipe.getRecipe_image())) {
            Glide.with(context)
                    .load(Base64.decode(recipe.getRecipe_image(), Base64.DEFAULT))
                    .into(holder.img_recipe_image);
        }
        if (!TextUtils.isEmpty(recipe.getUploaded_by_image())) {
            Glide.with(context)
                    .load(Base64.decode(recipe.getUploaded_by_image(), Base64.DEFAULT))
                    .into(holder.img_user_image);
        }
        holder.recipe_minutes.setText(recipe.getRecipe_total_minute() + " min.");
        holder.txt_recipe_name.setText(recipe.getRecipe_name());
        holder.txt_user_name.setText(recipe.getUploaded_by_name());
        holder.txt_recipe_category.setText(getRecipeType(recipe.getRecipe_type()));

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, ViewRecipeActivity.class);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public void setList(ArrayList<Recipe> recipes) {
        recipeList.clear();
        recipeList.addAll(recipes);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RoundedImageView img_recipe_image, img_user_image;
        TextView recipe_minutes, txt_recipe_name, txt_user_name, txt_recipe_category;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            img_recipe_image = itemView.findViewById(R.id.img_recipe_image);
            recipe_minutes = itemView.findViewById(R.id.recipe_minutes);
            txt_recipe_name = itemView.findViewById(R.id.txt_recipe_name);
            img_user_image = itemView.findViewById(R.id.img_user_image);
            txt_user_name = itemView.findViewById(R.id.txt_user_name);
            txt_recipe_category = itemView.findViewById(R.id.txt_recipe_category);
        }
    }
}
