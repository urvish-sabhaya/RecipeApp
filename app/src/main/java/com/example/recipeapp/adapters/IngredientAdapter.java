package com.example.recipeapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipeapp.R;
import com.example.recipeapp.models.RecipeIngredient;

import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder> {

    Context context;
    List<RecipeIngredient> recipeIngredients;

    public IngredientAdapter(Context context, List<RecipeIngredient> recipeIngredients) {
        this.context = context;
        this.recipeIngredients = recipeIngredients;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.ingredient_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecipeIngredient recipeIngredient = recipeIngredients.get(position);
        holder.matric_txt.setText(recipeIngredient.getMetric());
        holder.matric_for_txt.setText(recipeIngredient.getMatric_for());
    }

    @Override
    public int getItemCount() {
        return recipeIngredients.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView matric_txt, matric_for_txt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            matric_txt = itemView.findViewById(R.id.matric_txt);
            matric_for_txt = itemView.findViewById(R.id.matric_for_txt);
        }
    }
}
