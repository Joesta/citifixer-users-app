package com.dso30bt.project2019.potapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dso30bt.project2019.potapp.R;
import com.dso30bt.project2019.potapp.models.Constructor;
import com.dso30bt.project2019.potapp.models.enums.StatusEnum;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by Joesta on 2019/07/16.
 */
public class ConstructorCustomeAdapter extends ArrayAdapter<Constructor> {
    private List<Constructor> constorList;
    private Context context;


    public ConstructorCustomeAdapter(@NonNull Context context, int resource, @NonNull List<Constructor> objects) {
        super(context, 0, objects);
        this.constorList = objects;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(context).inflate(R.layout.construtor_row, parent, false);
            Constructor constructor = constorList.get(position);

            TextView tvConstructorFirstName = listItem.findViewById(R.id.tvConstructorFirstName);
            ImageView imageStatus = listItem.findViewById(R.id.tvConstructorStatus);

            String status = constructor.getStatus();
            if (status == StatusEnum.AVAILABLE.value) {
                imageStatus.setImageResource(R.drawable.available);
            } else {
                if (status == StatusEnum.BUSY.value) {
                    imageStatus.setImageResource(R.drawable.busy);
                } else {
                    imageStatus.setImageResource(R.drawable.on_leave);
                }
            }
        }

        return listItem;
    }
}

