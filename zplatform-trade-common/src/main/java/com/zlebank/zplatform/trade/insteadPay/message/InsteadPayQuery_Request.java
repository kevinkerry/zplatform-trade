/* 
 * InsteadPay_Request.java  
 * 
 * version TODO
 *
 * 2015年11月24日 
 * 
 * Copyright (c) 2015,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.trade.insteadPay.message;

import com.zlebank.zplatform.trade.common.validator.N;

/**
 * 代付查询【请求报文】
 *
 * @author Luxiaoshuai
 * @version
 * @date 2015年11月24日 下午2:22:28
 * @since 
 */
public class InsteadPayQuery_Request extends BaseMessage{
    /**批次号**/
    @N(max=4)
    private String batchNo;
    /**订单发送时间**/
    @N(max=14,min=14)
    private String txnTime;
    /**渠道类型**/
    @N(max=2,isNull=false)
    private String channelType;
    /**接入类型**/
    @N(max=1,isNull=false)
    private String accessType;

    
    public String getBatchNo() {
        return batchNo;
    }
    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }
    public String getTxnTime() {
        return txnTime;
    }
    public void setTxnTime(String txnTime) {
        this.txnTime = txnTime;
    }
    public String getChannelType() {
        return channelType;
    }
    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }
    public String getAccessType() {
        return accessType;
    }
    public void setAccessType(String accessType) {
        this.accessType = accessType;
    }
    
}
