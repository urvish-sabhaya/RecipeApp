package com.example.recipeapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipeapp.R;
import com.example.recipeapp.interfaces.IngredientInterface;
import com.example.recipeapp.models.RecipeIngredient;

import java.util.ArrayList;

public class AddDeleteIngredientAdapter extends RecyclerView.Adapter<AddDeleteIngredientAdapter.ViewHolder> {

    Context context;
    ArrayList<RecipeIngredient> recipeIngredients;
    IngredientInterface ingredientInterface;

    public AddDeleteIngredientAdapter(Context context, ArrayList<RecipeIngredient> recipeIngredients, IngredientInterface ingredientInterface) {
        this.context = context;
        this.recipeIngredients = recipeIngredients;
        this.ingredientInterface = ingredientInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.ingredient_item_delete, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecipeIngredient recipeIngredient = recipeIngredients.get(position);
        holder.matric_txt.setText(recipeIngredient.getMetric());
        holder.matric_for_txt.setText(recipeIngredient.getMatric_for());

        holder.delete_rel.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder.setTitle("Delete Ingredient");
            builder.setMessage("Are you sure you want to delete the " + recipeIngredient.getMatric_for() + "?");

            builder.setPositiveButton("YES", (dialog, which) -> {
                ingredientInterface.deleteIngredient(recipeIngredient);
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
        return recipeIngredients.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView matric_txt, matric_for_txt;
        RelativeLayout delete_rel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            matric_txt = itemView.findViewById(R.id.matric_txt);
            matric_for_txt = itemView.findViewById(R.id.matric_for_txt);
            delete_rel = itemView.findViewById(R.id.delete_rel);
        }
    }
}
