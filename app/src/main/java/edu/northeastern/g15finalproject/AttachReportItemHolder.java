package edu.northeastern.g15finalproject;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AttachReportItemHolder extends RecyclerView.ViewHolder {

    public TextView reportTime;
    public TextView reportType;
    public TextView reportAddress;

    public boolean isSelect = false;

    public AttachReportItemHolder(@NonNull View itemView) {
        super(itemView);
        this.reportAddress = itemView.findViewById(R.id.address_tv_attach_report);
        this.reportTime = itemView.findViewById(R.id.time_tv_attach_report);
        this.reportType = itemView.findViewById(R.id.type_tv_attach_report);
    }
}
