//package com.dso30bt.project2019.potapp.adapters;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.dso30bt.project2019.potapp.R;
//import com.dso30bt.project2019.potapp.models.Constructor;
//import com.dso30bt.project2019.potapp.models.enums.ConstructorStatusEnum;
//
//import java.util.List;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//
///**
// * Created by Joesta on 2019/07/16.
// */
//public class ConstructorCustomAdapter extends ArrayAdapter<Constructor> {
//    private List<Constructor> constructorList;
//    private Context context;
//
//
//    public ConstructorCustomAdapter(@NonNull Context context, @NonNull List<Constructor> objects) {
//        super(context, 0, objects);
//        this.constructorList = objects;
//        this.context = context;
//    }
//
//    @NonNull
//    @Override
//    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//
//        View listItem = convertView;
//        if (listItem == null) {
//            listItem = LayoutInflater.from(context).inflate(R.layout.engineer_constructor_row, parent, false);
//            Constructor constructor = constructorList.get(position);
//
//            TextView tvConstructorFirstName = listItem.findViewById(R.id.tvConstructorFirstName);
//            ImageView imageStatus = listItem.findViewById(R.id.tvConstructorStatus);
//
//            final String fullName = constructor.getName() + " " + constructor.getSurname();
//            tvConstructorFirstName.setText(fullName);
//
//            String status = constructor.getStatus();
//            if (status.equalsIgnoreCase(ConstructorStatusEnum.AVAILABLE.value)) {
//                imageStatus.setImageResource(R.drawable.available);
//            } else {
//                if (status.equalsIgnoreCase(ConstructorStatusEnum.BUSY.value)) {
//                    imageStatus.setImageResource(R.drawable.busy);
//                } else {
//                    imageStatus.setImageResource(R.drawable.on_leave);
//                }
//            }
//        }
//
//        return listItem;
//    }
//}
//
