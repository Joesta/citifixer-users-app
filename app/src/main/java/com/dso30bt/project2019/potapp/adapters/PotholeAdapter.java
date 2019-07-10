package com.dso30bt.project2019.potapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dso30bt.project2019.potapp.R;
import com.dso30bt.project2019.potapp.models.Pothole;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Joesta on 2019/07/05.
 */
public class PotholeAdapter extends RecyclerView.Adapter<PotholeAdapter.ViewHolder> {

    //widgets
    private View view;

    //vars
    private Context context;
    private List<Pothole> potholeList;
    private String name;

    // constructor
    public PotholeAdapter(Context context, List<Pothole> potholeList, String name) {
        this.context = context;
        this.potholeList = potholeList;
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

        holder.descriptionTextValue.setText(potholeList.get(position).getDescription());
        holder.latitudeTextValue.setText(String.valueOf(potholeList.get(position).getCoordinates().getLatitude()));
        holder.longitudeTextValue.setText(String.valueOf(potholeList.get(position).getCoordinates().getLongitude()));
        holder.dateReportedTextValue.setText(String.valueOf(potholeList.get(position).getCoordinates().getDate()));
        holder.reporterName.setText(name);
    }

    @Override
    public int getItemCount() {
        return potholeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView potholeImageView;
        TextView descriptionTextValue;
        TextView latitudeTextValue;
        TextView longitudeTextValue;
        TextView dateReportedTextValue;
        TextView reporterName;

        public ViewHolder(@NonNull View itemView) {
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
