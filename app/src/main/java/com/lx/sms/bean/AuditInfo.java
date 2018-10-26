package com.lx.sms.bean;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class AuditInfo implements Parcelable {


    /**
     * beginDate : 2018-10-10 10:57:00
     * createUserName : 赵超远
     * endDate : 2018-10-11 10:57:00
     * id : 25
     * preLeaveHours : 10
     * reason : 偷情
     * approvalStatusName : 科长审核
     * leaveTypeName : 事假
     */

    public String beginDate;
    public String lawOfferName;
    public String endDate;
    public int id;
    public String preLeaveHours;
    public String reason;
    public String approvalStatus;
    public String approvalStatusName;
    public String leaveType;
    public String remark;


    public AuditInfo(String beginDate, String lawOfferName, String endDate, int id, String preLeaveHours, String reason, String approvalStatus, String remark, String approvalStatusName, String leaveType) {
        this.beginDate = beginDate;
        this.lawOfferName = lawOfferName;
        this.endDate = endDate;
        this.id = id;
        this.preLeaveHours = preLeaveHours;
        this.reason = reason;
        this.approvalStatus = approvalStatus;
        this.approvalStatusName = approvalStatusName;
        this.leaveType = leaveType;
        this.remark = remark;
    }


    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getLawOfferName() {
        return lawOfferName;
    }

    public void setLawOfferName(String lawOfferName) {
        this.lawOfferName = lawOfferName;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPreLeaveHours() {
        return preLeaveHours;
    }

    public void setPreLeaveHours(String preLeaveHours) {
        this.preLeaveHours = preLeaveHours;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public String getApprovalStatusName() {
        return approvalStatusName;
    }

    public void setApprovalStatusName(String approvalStatusName) {
        this.approvalStatusName = approvalStatusName;
    }

    public String getleaveType() {
        return leaveType;
    }

    public void setleaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    //    序列化
    public AuditInfo(Parcel sourse) {
        beginDate = sourse.readString();
        lawOfferName = sourse.readString();
        endDate = sourse.readString();
        id = sourse.readInt();
        preLeaveHours = sourse.readString();
        reason = sourse.readString();
        approvalStatus = sourse.readString();
        approvalStatusName = sourse.readString();
        leaveType = sourse.readString();
        remark = sourse.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(beginDate);
        dest.writeString(lawOfferName);
        dest.writeString(endDate);
        dest.writeInt(id);
        dest.writeString(preLeaveHours);
        dest.writeString(reason);
        dest.writeString(approvalStatus);
        dest.writeString(approvalStatusName);
        dest.writeString(leaveType);
        dest.writeString(remark);
    }

    public static final Creator<AuditInfo> CREATOR = new Creator<AuditInfo>() {
        @Override
        public AuditInfo createFromParcel(Parcel source) {
            return new AuditInfo(source);
        }

        @Override
        public AuditInfo[] newArray(int size) {
            return new AuditInfo[size];
        }
    };
}
