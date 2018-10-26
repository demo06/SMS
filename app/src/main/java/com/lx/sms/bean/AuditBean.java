package com.lx.sms.bean;


public class AuditBean {

    /**
     * id : 34
     * approvalOpinion : 1
     * remark : 小伙子最近加班比较多，辛苦了，多多休息，同意
     */

    public String id;
    public String approvalOpinion;
    public String remark;

    public AuditBean(String id, String approvalOpinion, String remark) {
        this.id = id;
        this.approvalOpinion = approvalOpinion;
        this.remark = remark;
    }

    @Override
    public String toString() {
        return remark == null ?
                "id:'" + id + '\'' +
                        ", approvalOpinion:'" + approvalOpinion + '\'' +
                        ", remark:\'\'" :
                "id:'" + id + '\'' +
                        ", approvalOpinion:'" + approvalOpinion + '\'' +
                        ", remark:'" + remark + '\''
                ;
    }
}
