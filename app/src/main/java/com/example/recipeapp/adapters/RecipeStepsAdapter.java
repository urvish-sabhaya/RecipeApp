package com.example.recipeapp.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.recipeapp.R;
import com.example.recipeapp.models.RecipeStep;

import java.util.List;

public class RecipeStepsAdapter extends RecyclerView.Adapter<RecipeStepsAdapter.ViewHolder> {

    Context context;
    List<RecipeStep> recipeSteps;
    int size;

    public RecipeStepsAdapter(Context context, List<RecipeStep> recipeSteps) {
        this.context = context;
        this.recipeSteps = recipeSteps;
        size = recipeSteps.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.steps_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecipeStep recipeStep = recipeSteps.get(position);
        if (!TextUtils.isEmpty(recipeStep.getStep_image())) {
            Glide.with(context)
                    .load(Base64.decode(recipeStep.getStep_image(), Base64.DEFAULT))
                    .into(holder.recipe_image);
        }

        holder.steps_position_txt.setText("STEP " + (position + 1) + "/" + size);
        holder.step_description_txt.setText(recipeStep.getStep_detail());
    }

    @Override
    public int getItemCount() {
        return recipeSteps.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView recipe_image;
        TextView steps_position_txt, step_description_txt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            recipe_image = itemView.findViewById(R.id.recipe_image);
            steps_position_txt = itemView.findViewById(R.id.steps_position_txt);
            step_description_txt = itemView.findViewById(R.id.step_description_txt);
        }
    }
}
