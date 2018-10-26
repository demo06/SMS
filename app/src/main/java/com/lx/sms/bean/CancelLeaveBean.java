package com.lx.sms.bean;


public class CancelLeaveBean {


    /**
     * id : 20
     * reportDate : 2018-10-12 9:06:47
     * workDate : 2018-10-12 9:06:47
     * leaveHours : 1.0
     */

    public String id;
    public String reportDate;
    public String workDate;
    public String leaveHours;

    public CancelLeaveBean(String id, String reportDate, String workDate, String leaveHours) {
        this.id = id;
        this.reportDate = reportDate;
        this.workDate = workDate;
        this.leaveHours = leaveHours;
    }

    @Override
    public String toString() {
        return "id:'" + id + '\'' +
                ", reportDate:'" + reportDate + '\'' +
                ", workDate:'" + workDate + '\'' +
                ", leaveHours:'" + leaveHours + '\''
                ;
    }
}
