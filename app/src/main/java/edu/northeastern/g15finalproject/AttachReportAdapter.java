package edu.northeastern.g15finalproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class AttachReportAdapter extends RecyclerView.Adapter<AttachReportItemHolder> {
    private final Context context;
    private final List<Report> reportList;

    public AttachReportAdapter(Context context, List<Report> reportList) {
        this.context = context;
        this.reportList = reportList;

        System.out.println("ATTACH REPORT : constructor");
        System.out.println("ATTACH REPORT : reportList size = " + reportList.size());
    }

    @Override
    public AttachReportItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        System.out.println("ATTACH REPORT : onCreateViewHolder");
        return new AttachReportItemHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.activity_attach_report_item_view, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(AttachReportItemHolder holder, int position) {
        holder.reportAddress.setText(reportList.get(position).getFullAddress());
        String formattedTime = convertUTCStampBackToReadable(reportList.get(position).getTime());
        holder.reportTime.setText(formattedTime);
        holder.reportType.setText(reportList.get(position).getType());

        System.out.println("ATTACH REPORT : onBindViewHolder");
        System.out.println("ATTACH REPORT : reportList size = " + reportList.size());
        System.out.println("ATTACH REPORT : position = " + position);

        holder.itemView.setOnClickListener(v -> {
            // check if report is selected
            holder.isSelect = !holder.isSelect;
            if (holder.isSelect) {
                holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.white));
            } else {
                holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.black));
            }
        });
    }

    @Override
    public int getItemCount() {
        System.out.println("ATTACH REPORT : getItemCount");
        System.out.println("ATTACH REPORT : reportList size = " + reportList.size());
        return reportList.size();
    }

    public String convertUTCStampBackToReadable(long time) {
        Date utcDate = new Date(time);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String formattedUtcTime = dateFormat.format(utcDate);

        return formattedUtcTime;
    }


}
