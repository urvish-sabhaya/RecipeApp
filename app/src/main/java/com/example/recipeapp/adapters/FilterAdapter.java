package com.example.recipeapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipeapp.R;
import com.example.recipeapp.models.RecipeType;

import java.util.ArrayList;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.ViewHolder> {

    Context context;
    ArrayList<RecipeType> categoryList;
    public ArrayList<String> selectedCategories = new ArrayList<>();

    public FilterAdapter(Context context, ArrayList<RecipeType> categoryList, ArrayList<String> selectedCategoriesFilter) {
        this.context = context;
        this.categoryList = categoryList;
        selectedCategories.addAll(selectedCategoriesFilter);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.filter_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecipeType filter = categoryList.get(position);
        holder.category_check.setText(filter.getRecipe_name());

        if (!selectedCategories.isEmpty() &&
                selectedCategories.contains(filter.getDocument_id())) {
            holder.category_check.setChecked(true);
        }

        holder.category_check.setOnClickListener(v -> {
            boolean isChecked = holder.category_check.isChecked();
            if (isChecked) {
                //checkBox clicked and checked
                selectedCategories.add(filter.getDocument_id());
            } else {
                //checkBox clicked and unchecked
                selectedCategories.remove(filter.getDocument_id());
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CheckBox category_check;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            category_check = itemView.findViewById(R.id.category_check);
        }
    }
}
