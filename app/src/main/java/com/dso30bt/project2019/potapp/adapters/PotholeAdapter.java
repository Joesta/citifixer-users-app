package com.dso30bt.project2019.potapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.dso30bt.project2019.potapp.R;
import com.dso30bt.project2019.potapp.models.Pothole;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Joesta on 2019/07/05.
 */
public class PotholeAdapter extends RecyclerView.Adapter<PotholeAdapter.ViewHolder> implements Filterable {

    //widgets
    private View view;

    //vars
    private Context context;
    private String name;
    private List<Pothole> potholeList;
    private List<Pothole> filterPotholes;

    // constructor
    public PotholeAdapter(Context context, List<Pothole> potholeList, String name) {
        this.context = context;
        this.potholeList = potholeList;
        this.filterPotholes = potholeList;
        this.name = name;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Picasso
                .get()
                .load(potholeList.get(position).getPotholeUrl())
                .into(holder.potholeImageView);

//        holder.descriptionTextValue.setText(potholeList.get(position).getDescription());
//        holder.latitudeTextValue.setText(String.valueOf(potholeList.get(position).getCoordinates().getLatitude()));
//        holder.longitudeTextValue.setText(String.valueOf(potholeList.get(position).getCoordinates().getLongitude()));
//        holder.dateReportedTextValue.setText(String.valueOf(potholeList.get(position).getCoordinates().getDate()));
//        holder.reporterName.setText(name);
    }

    @Override
    public int getItemCount() {
        return potholeList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filterPotholes = potholeList;
                } else {
                    List<Pothole> filteredList = new ArrayList<>();
                    for (Pothole pothole : potholeList) {

                        // pothole match condition.
                        // here we are looking for pothole status match
//                        if (pothole.get().toLowerCase().contains(charString.toLowerCase())) {
//                            filteredList.add(vehicle);
//                        }
                    }

                    filterPotholes = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filterPotholes;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filterPotholes = (List<Pothole>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView potholeImageView;
        TextView descriptionTextValue;
        TextView latitudeTextValue;
        TextView longitudeTextValue;
        TextView dateReportedTextValue;
        TextView reporterName;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            initUI();
        }

        private void initUI() {
            potholeImageView = view.findViewById(R.id.potholeImageView);
            descriptionTextValue = view.findViewById(R.id.descriptionTextValue);
            latitudeTextValue = view.findViewById(R.id.latitudeTextValue);
            longitudeTextValue = view.findViewById(R.id.longitudeTextValue);
            dateReportedTextValue = view.findViewById(R.id.dateReportedTextValue);
            reporterName = view.findViewById(R.id.reporterTextValue);

        }
    }

}
