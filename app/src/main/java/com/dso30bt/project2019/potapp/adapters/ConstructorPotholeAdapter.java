package com.dso30bt.project2019.potapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dso30bt.project2019.potapp.R;
import com.dso30bt.project2019.potapp.models.Report;
import com.dso30bt.project2019.potapp.utils.NavUtil;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by Joesta on 2019/07/17.
 */
public class ConstructorPotholeAdapter extends ArrayAdapter<Report> {
    private List<Report> reportList;
    private Context context;

    /*widgets*/
    private TextView tvAssingedBy;
    private TextView tvReporter;
    private TextView tvReportDate;
    private TextView tvPotholeStatus;


    public ConstructorPotholeAdapter(@NonNull Context context, @NonNull List<Report> objects) {
        super(context, 0, objects);
        this.reportList = objects;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(context).inflate(R.layout.constructor_pothole_row, parent, false);

            initUI(listItem);
            setUITextValues(position);
        }

        return listItem;
    }

    private void setUITextValues(final int position) {
        String assignedBy = reportList.get(position).getAssignedBy();
        String reportedBy = reportList
                .get(position)
                .getUser()
                .getFirstName() + " " + reportList
                .get(position)
                .getUser()
                .getLastName();
        String date = reportList.get(position).getReportDate().toString();


        tvAssingedBy.setText(assignedBy);
        tvReporter.setText(reportedBy);
        tvReportDate.setText(date);
        tvPotholeStatus.setText("");
    }

    private void initUI(View listItem) {
        tvAssingedBy = listItem.findViewById(R.id.tvAssignedBy);
        tvReporter = listItem.findViewById(R.id.tvReporter);
        tvReportDate = listItem.findViewById(R.id.tvReportDate);
        tvPotholeStatus = listItem.findViewById(R.id.tvPotholeStatus);
    }
}
