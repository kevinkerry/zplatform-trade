/* 
 * TxncodeDefServiceImpl.java  
 * 
 * version TODO
 *
 * 2015年9月10日 
 * 
 * Copyright (c) 2015,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.trade.service.impl;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.zlebank.zplatform.trade.dao.ITxncodeDefDAO;
import com.zlebank.zplatform.trade.model.TxncodeDefModel;
import com.zlebank.zplatform.trade.service.ITxncodeDefService;
import com.zlebank.zplatform.trade.service.base.BaseServiceImpl;

/**
 * Class Description
 *
 * @author guojia
 * @version
 * @date 2015年9月10日 下午3:59:56
 * @since 
 */
@Service("txncodeDefService")
public class TxncodeDefServiceImpl extends BaseServiceImpl<TxncodeDefModel, Long> implements ITxncodeDefService{

    @Autowired
    private ITxncodeDefDAO txncodeDefDAO;
    /**
     *
     * @return
     */
    @Override
    public Session getSession() {
        // TODO Auto-generated method stub
        return txncodeDefDAO.getSession();
    }
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public TxncodeDefModel getBusiCode(String txntype,String txnsubtype,String biztype){
        return super.getUniqueByHQL("from TxncodeDefModel where txntype=? and txnsubtype=? and biztype=? ", new Object[]{txntype,txnsubtype,biztype});
    }

}
