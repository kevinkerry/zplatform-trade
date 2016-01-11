package com.zlebank.zplatform.trade.model;
// default package
// Generated 2015-12-1 14:50:13 by Hibernate Tools 4.0.0

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * TTransferBatch generated by hbm2java
 */
@Entity
@Table(name = "T_TRANSFER_BATCH")
public class PojoTransferBatch implements java.io.Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -3940668644699180076L;
	private long tid;
    private String batchno;
    private Long sumitem;
    private Long sumamount;
    private Long succitem;
    private Long succamount;
    private Long failitem;
    private Long failamount;
    private String status;
    private String createtime;
    private String transfertime;
    private String chnlcode;
    private Integer retrytimes;
    private String transfertype;
    private String insteadpaybatchno;
    private String merchid; 
    private String requestfilename;
    private String responsefilename;
    private String accstatus;
    private String busicode;
    private String busitype;
    
    public PojoTransferBatch() {
    }

    public PojoTransferBatch(long tid) {
        this.tid = tid;
    }
    

    public PojoTransferBatch(long tid, String batchno, Long sumitem,
            Long sumamount, Long succitem, Long succamount, Long failitem,
            Long failamount, String status, String createtime,
            String transfertime, String chnlcode, Integer retrytimes,
            String transfertype, String insteadpaybatchno, String merchid,
            String requestfilename, String responsefilename, String accstatus) {
        super();
        this.tid = tid;
        this.batchno = batchno;
        this.sumitem = sumitem;
        this.sumamount = sumamount;
        this.succitem = succitem;
        this.succamount = succamount;
        this.failitem = failitem;
        this.failamount = failamount;
        this.status = status;
        this.createtime = createtime;
        this.transfertime = transfertime;
        this.chnlcode = chnlcode;
        this.retrytimes = retrytimes;
        this.transfertype = transfertype;
        this.insteadpaybatchno = insteadpaybatchno;
        this.merchid = merchid;
        this.requestfilename = requestfilename;
        this.responsefilename = responsefilename;
        this.accstatus = accstatus;
    }
    @GenericGenerator(name = "id_gen", strategy = "enhanced-table", parameters = {
            @Parameter(name = "table_name", value = "T_C_PRIMAY_KEY"),
            @Parameter(name = "value_column_name", value = "NEXT_ID"),
            @Parameter(name = "segment_column_name", value = "KEY_NAME"),
            @Parameter(name = "segment_value", value = "T_TRANSFER_DATA_ID"),
            @Parameter(name = "increment_size", value = "1"),
            @Parameter(name = "optimizer", value = "pooled-lo") })
    @Id
    @GeneratedValue(generator = "id_gen")
    @Column(name = "TID", unique = true, nullable = false, precision = 12, scale = 0)
    public long getTid() {
        return this.tid;
    }

    public void setTid(long tid) {
        this.tid = tid;
    }

    @Column(name = "BATCHNO", length = 8)
    public String getBatchno() {
        return this.batchno;
    }

    public void setBatchno(String batchno) {
        this.batchno = batchno;
    }

    @Column(name = "SUMITEM", precision = 12, scale = 0)
    public Long getSumitem() {
        return this.sumitem;
    }

    public void setSumitem(Long sumitem) {
        this.sumitem = sumitem;
    }

    @Column(name = "SUMAMOUNT", precision = 12, scale = 0)
    public Long getSumamount() {
        return this.sumamount;
    }

    public void setSumamount(Long sumamount) {
        this.sumamount = sumamount;
    }

    @Column(name = "SUCCITEM", precision = 12, scale = 0)
    public Long getSuccitem() {
        return this.succitem;
    }

    public void setSuccitem(Long succitem) {
        this.succitem = succitem;
    }

    @Column(name = "SUCCAMOUNT", precision = 12, scale = 0)
    public Long getSuccamount() {
        return this.succamount;
    }

    public void setSuccamount(Long succamount) {
        this.succamount = succamount;
    }

    @Column(name = "FAILITEM", precision = 12, scale = 0)
    public Long getFailitem() {
        return this.failitem;
    }

    public void setFailitem(Long failitem) {
        this.failitem = failitem;
    }

    @Column(name = "FAILAMOUNT", precision = 12, scale = 0)
    public Long getFailamount() {
        return this.failamount;
    }

    public void setFailamount(Long failamount) {
        this.failamount = failamount;
    }

    @Column(name = "STATUS", length = 2)
    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Column(name = "CREATETIME", length = 14)
    public String getCreatetime() {
        return this.createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    @Column(name = "TRANSFERTIME", length = 14)
    public String getTransfertime() {
        return this.transfertime;
    }

    public void setTransfertime(String transfertime) {
        this.transfertime = transfertime;
    }

    @Column(name = "CHNLCODE", length = 8)
    public String getChnlcode() {
        return this.chnlcode;
    }

    public void setChnlcode(String chnlcode) {
        this.chnlcode = chnlcode;
    }

    @Column(name = "RETRYTIMES", precision = 2, scale = 0)
    public Integer getRetrytimes() {
        return this.retrytimes;
    }

    public void setRetrytimes(Integer retrytimes) {
        this.retrytimes = retrytimes;
    }
    @Column(name = "TRANSFERTYPE", length=2)
    public String getTransfertype() {
        return transfertype;
    }

    public void setTransfertype(String transfertype) {
        this.transfertype = transfertype;
    }
    @Column(name = "INSTEADPAYBATCHNO", length=32)
    public String getInsteadpaybatchno() {
        return insteadpaybatchno;
    }

    public void setInsteadpaybatchno(String insteadpaybatchno) {
        this.insteadpaybatchno = insteadpaybatchno;
    }
    @Column(name = "MERCHID", length=15)
    public String getMerchid() {
        return merchid;
    }

    public void setMerchid(String merchid) {
        this.merchid = merchid;
    }
    @Column(name = "REQUESTFILENAME", length=128)
    public String getRequestfilename() {
        return requestfilename;
    }

    public void setRequestfilename(String requestfilename) {
        this.requestfilename = requestfilename;
    }
    @Column(name = "RESPONSEFILENAME", length=128)
    public String getResponsefilename() {
        return responsefilename;
    }

    public void setResponsefilename(String responsefilename) {
        this.responsefilename = responsefilename;
    }
    @Column(name = "ACCSTATUS", length=2)
    public String getAccstatus() {
        return accstatus;
    }

    public void setAccstatus(String accstatus) {
        this.accstatus = accstatus;
    }
    @Column(name = "BUSICODE", length=8)
    public String getBusicode() {
        return busicode;
    }

    public void setBusicode(String busicode) {
        this.busicode = busicode;
    }
    @Column(name = "BUSITYPE", length=4)
    public String getBusitype() {
        return busitype;
    }

    public void setBusitype(String busitype) {
        this.busitype = busitype;
    }
    
    
    
    
    

}
