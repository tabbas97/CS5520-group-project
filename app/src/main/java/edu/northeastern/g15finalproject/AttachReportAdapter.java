package edu.northeastern.g15finalproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicReference;

public class AttachReportAdapter extends RecyclerView.Adapter<AttachReportItemHolder> {
    private final Context context;
    private final List<Report> reportList;
    private boolean isPaired = false;

    private static List<Report> selectedReports;

    public boolean isPaired() {
        return isPaired;
    }

    public AttachReportAdapter(Context context, List<Report> reportList) {
        // , AtomicReference<List<Report>>
        this.context = context;
        this.reportList = reportList;
        this.isPaired = false;
        selectedReports = new java.util.ArrayList<>();
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
                selectedReports.add(reportList.get(position));
                System.out.println("ATTACH REPORT : selected = " + reportList.get(position).toString());
                holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
            } else {
                selectedReports.remove(reportList.get(position));
                System.out.println("ATTACH REPORT : unselected = " + reportList.get(position).toString());
                holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.white));
            }
            System.out.println("ATTACH REPORT : selectedReports size = " + selectedReports.toString());
        });
    }

    public List<Report> getSelectedReports() {

        System.out.println("ATTACH REPORT : getSelectedReports");
        System.out.println("ATTACH REPORT : selectedReports size = " + selectedReports.toString());

        return selectedReports;
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
