package com.example.recipeapp.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.recipeapp.R;
import com.example.recipeapp.interfaces.DeleteRecipeStepInterface;
import com.example.recipeapp.models.RecipeStep;

import java.util.ArrayList;

public class AddDeleteRecipeStepsAdapter extends RecyclerView.Adapter<AddDeleteRecipeStepsAdapter.ViewHolder> {

    Context context;
    ArrayList<RecipeStep> recipeSteps;
    DeleteRecipeStepInterface deleteRecipeStepInterface;

    public AddDeleteRecipeStepsAdapter(Context context, ArrayList<RecipeStep> recipeSteps, DeleteRecipeStepInterface deleteRecipeStepInterface) {
        this.context = context;
        this.recipeSteps = recipeSteps;
        this.deleteRecipeStepInterface = deleteRecipeStepInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.steps_item_delete, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecipeStep recipeStep = recipeSteps.get(position);
        if (!TextUtils.isEmpty(recipeStep.getStep_image())) {
            Glide.with(context)
                    .load(Base64.decode(recipeStep.getStep_image(), Base64.DEFAULT))
                    .into(holder.recipe_image);
        }

        holder.steps_position_txt.setText("STEP " + (position + 1) + "/" + recipeSteps.size());
        holder.step_description_txt.setText(recipeStep.getStep_detail());

        holder.delete_rel.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder.setTitle("Delete");
            builder.setMessage("Are you sure you want to delete the step" + (position + 1) + "/" + recipeSteps.size() + "?");

            builder.setPositiveButton("YES", (dialog, which) -> {
                deleteRecipeStepInterface.deleteRecipeStep(recipeStep);
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
        return recipeSteps.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView recipe_image;
        TextView steps_position_txt, step_description_txt;
        RelativeLayout delete_rel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            recipe_image = itemView.findViewById(R.id.recipe_image);
            steps_position_txt = itemView.findViewById(R.id.steps_position_txt);
            step_description_txt = itemView.findViewById(R.id.step_description_txt);
            delete_rel = itemView.findViewById(R.id.delete_rel);
        }
    }
}
