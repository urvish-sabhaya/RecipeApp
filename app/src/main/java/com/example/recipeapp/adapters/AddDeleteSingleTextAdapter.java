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
import com.example.recipeapp.interfaces.DeleteInterface;

import java.util.ArrayList;

public class AddDeleteSingleTextAdapter extends RecyclerView.Adapter<AddDeleteSingleTextAdapter.ViewHolder> {

    Context context;
    ArrayList<String> textsList;
    DeleteInterface deleteInterface;

    public AddDeleteSingleTextAdapter(Context context, ArrayList<String> textsList, DeleteInterface deleteInterface) {
        this.context = context;
        this.textsList = textsList;
        this.deleteInterface = deleteInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.single_text_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String itemName = textsList.get(position);
        holder.item_name.setText(itemName);

        holder.delete_rel.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder.setTitle("Delete");
            builder.setMessage("Are you sure you want to delete the " + itemName + "?");

            builder.setPositiveButton("YES", (dialog, which) -> {
                deleteInterface.deleteSingleText(itemName);
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
        return textsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView item_name;
        RelativeLayout delete_rel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            item_name = itemView.findViewById(R.id.item_name);
            delete_rel = itemView.findViewById(R.id.delete_rel);
        }
    }
}
