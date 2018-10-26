package com.lx.sms.bean;


public class LeaveInfo {


    public String leaveType;
    public String id;
    public String beginDate;
    public String endDate;
    public String preLeaveHours;
    public String reason;
    public String flag;

    public LeaveInfo(String id, String leaveType, String beginDate, String endDate, String preLeaveHours, String reason, String flag) {
        this.id = id;
        this.leaveType = leaveType;
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.preLeaveHours = preLeaveHours;
        this.reason = reason;
        this.flag = flag;
    }

    @Override
    public String toString() {
        return id == null ? "leaveType:\"" + leaveType + '\"' +
                ",beginDate:\"" + beginDate + '\"' +
                ", endDate:\"" + endDate + '\"' +
                ", preLeaveHours:\"" + preLeaveHours + '\"' +
                ", reason:\"" + reason + '\"' +
                ", flag:\"" + flag + '\"' :
                "id:\"" + id + '\"' +
                        ",leaveType:\"" + leaveType + '\"' +
                        ",beginDate:\"" + beginDate + '\"' +
                        ", endDate:\"" + endDate + '\"' +
                        ", preLeaveHours:\"" + preLeaveHours + '\"' +
                        ", reason:\"" + reason + '\"' +
                        ", flag:\"" + flag + '\"'
                ;
    }
}
