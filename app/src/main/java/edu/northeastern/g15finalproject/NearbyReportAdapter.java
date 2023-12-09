package edu.northeastern.g15finalproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class NearbyReportAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;

    List<Report> reportList;

    String time[];
    String type[];
    String address[];


//    public NearbyReportAdapter(Context context, String[] time, String[] type, String[] address) {
//        this.context = context;
//        this.time = time;
//        this.type = type;
//        this.address = address;
//        inflater = LayoutInflater.from(context);
//
//    }

    public NearbyReportAdapter(Context context, List<Report> reportList) {
        this.context = context;
        this.reportList = reportList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return reportList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        convertView = inflater.inflate(R.layout.activity_nearby_report_list_view, null);
//        TextView time_tv = convertView.findViewById(R.id.time_card);
//        TextView type_tv = convertView.findViewById(R.id.type_card);
//        TextView address_tv = convertView.findViewById(R.id.address_card);
//        time_tv.setText(time[position]);
//        type_tv.setText(type[position]);
//        address_tv.setText(type[position]);
//        return convertView;
//    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.activity_nearby_report_list_view, null);
        TextView time_tv = convertView.findViewById(R.id.time_card);
        TextView type_tv = convertView.findViewById(R.id.type_card);
        TextView address_tv = convertView.findViewById(R.id.address_card);
        String formattedTime = convertUTCStampBackToReadable(reportList.get(position).getTime());
        time_tv.setText("Time: " + formattedTime);
//        time_tv.setText("Time: " + convertUTCStampBackToReadable(reportList.get(position).getTime()));
//        time_tv.setText("Time: " + reportList.get(position).getTime());
        type_tv.setText("Type: " + reportList.get(position).getType());
        address_tv.setText("Address: " + reportList.get(position).getFullAddress());
//        time_tv.setText(time[position]);
//        type_tv.setText(type[position]);
//        address_tv.setText(type[position]);
        return convertView;
    }

    public String convertUTCStampBackToReadable(long time) {
        Date utcDate = new Date(time);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String formattedUtcTime = dateFormat.format(utcDate);

        return formattedUtcTime;
    }
}
