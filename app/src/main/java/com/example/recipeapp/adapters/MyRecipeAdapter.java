package com.example.recipeapp.adapters;

import static com.example.recipeapp.utils.Constants.EDIT_RECIPE;
import static com.example.recipeapp.utils.Constants.VIEW_RECIPE;
import static com.example.recipeapp.utils.Constants.viewableRecipe;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.recipeapp.R;
import com.example.recipeapp.activities.AddRecipeActivity;
import com.example.recipeapp.activities.ViewRecipeActivity;
import com.example.recipeapp.interfaces.DeleteRecipeInterface;
import com.example.recipeapp.models.Recipe;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class MyRecipeAdapter extends RecyclerView.Adapter<MyRecipeAdapter.ViewHolder> {

    Context context;
    List<Recipe> recipeList;
    DeleteRecipeInterface deleteRecipeInterface;

    public MyRecipeAdapter(Context context, List<Recipe> recipeList, DeleteRecipeInterface deleteRecipeInterface) {
        this.context = context;
        this.recipeList = recipeList;
        this.deleteRecipeInterface = deleteRecipeInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.my_recipe_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);
        if (!TextUtils.isEmpty(recipe.getRecipe_image())) {
            Glide.with(context)
                    .load(Base64.decode(recipe.getRecipe_image(), Base64.DEFAULT))
                    .into(holder.img_recipe_image);
        }
        holder.txt_recipe_name.setText(recipe.getRecipe_name());
        holder.txt_recipe_description.setText(recipe.getRecipe_details());

        holder.itemView.setOnClickListener(view -> {
            viewableRecipe = recipe;
            Intent intent = new Intent(context, ViewRecipeActivity.class);
            intent.putExtra(VIEW_RECIPE, "recipe");
            context.startActivity(intent);
        });

        holder.edit_rel.setOnClickListener(view -> {
            Intent intent = new Intent(context, AddRecipeActivity.class);
            viewableRecipe = recipe;
            intent.putExtra(EDIT_RECIPE, "recipe");
            context.startActivity(intent);
        });

        holder.delete_rel.setOnClickListener(view -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder.setTitle("Delete Recipe");
            builder.setMessage("Are you sure you want to delete " + recipe.getRecipe_name() + "?");

            builder.setPositiveButton("YES", (dialog, which) -> {
                deleteRecipeInterface.deleteRecipe(recipe.getDocument_id(), position);
                dialog.dismiss();
            });

            builder.setNegativeButton("NO", (dialog, which) -> {
                dialog.dismiss();
            });

            AlertDialog alert = builder.create();
            alert.show();
        });
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RoundedImageView img_recipe_image;
        TextView txt_recipe_description, txt_recipe_name;
        RelativeLayout edit_rel, delete_rel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            img_recipe_image = itemView.findViewById(R.id.img_recipe_image);
            txt_recipe_description = itemView.findViewById(R.id.txt_recipe_description);
            txt_recipe_name = itemView.findViewById(R.id.txt_recipe_name);
            edit_rel = itemView.findViewById(R.id.edit_rel);
            delete_rel = itemView.findViewById(R.id.delete_rel);
        }
    }
}
