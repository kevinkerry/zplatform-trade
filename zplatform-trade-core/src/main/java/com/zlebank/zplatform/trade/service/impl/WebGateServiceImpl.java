/* 
 * WebGateServiceImpl.java  
 * 
 * version TODO
 *
 * 2015年11月17日 
 * 
 * Copyright (c) 2015,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.trade.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.zlebank.zplatform.acc.bean.BusiAcct;
import com.zlebank.zplatform.acc.bean.BusiAcctQuery;
import com.zlebank.zplatform.acc.bean.enums.Usage;
import com.zlebank.zplatform.acc.pojo.Money;
import com.zlebank.zplatform.acc.service.AccountQueryService;
import com.zlebank.zplatform.commons.utils.Md5;
import com.zlebank.zplatform.commons.utils.StringUtil;
import com.zlebank.zplatform.member.dao.PersonDAO;
import com.zlebank.zplatform.member.pojo.PojoPersonDeta;
import com.zlebank.zplatform.member.service.MerchMKService;
import com.zlebank.zplatform.trade.adapter.quickpay.IQuickPayTrade;
import com.zlebank.zplatform.trade.bean.AccountTradeBean;
import com.zlebank.zplatform.trade.bean.ReaPayResultBean;
import com.zlebank.zplatform.trade.bean.ResultBean;
import com.zlebank.zplatform.trade.bean.TradeBean;
import com.zlebank.zplatform.trade.bean.enums.ChannelEnmu;
import com.zlebank.zplatform.trade.bean.enums.TradeTypeEnum;
import com.zlebank.zplatform.trade.bean.gateway.OrderRespBean;
import com.zlebank.zplatform.trade.dao.ITxnsOrderinfoDAO;
import com.zlebank.zplatform.trade.exception.TradeException;
import com.zlebank.zplatform.trade.factory.TradeAdapterFactory;
import com.zlebank.zplatform.trade.model.QuickpayCustModel;
import com.zlebank.zplatform.trade.model.TxnsLogModel;
import com.zlebank.zplatform.trade.model.TxnsOrderinfoModel;
import com.zlebank.zplatform.trade.service.IAccountPayService;
import com.zlebank.zplatform.trade.service.ICashBankService;
import com.zlebank.zplatform.trade.service.IGateWayService;
import com.zlebank.zplatform.trade.service.IMemberService;
import com.zlebank.zplatform.trade.service.IProdCaseService;
import com.zlebank.zplatform.trade.service.IQuickpayCustService;
import com.zlebank.zplatform.trade.service.IRouteConfigService;
import com.zlebank.zplatform.trade.service.ITxncodeDefService;
import com.zlebank.zplatform.trade.service.ITxnsLogService;
import com.zlebank.zplatform.trade.service.ITxnsQuickpayService;
import com.zlebank.zplatform.trade.service.ITxnsRefundService;
import com.zlebank.zplatform.trade.service.ITxnsSplitAccountService;
import com.zlebank.zplatform.trade.service.ITxnsWithdrawService;
import com.zlebank.zplatform.trade.service.IWebGateWayService;
import com.zlebank.zplatform.trade.service.base.BaseServiceImpl;
import com.zlebank.zplatform.trade.utils.ConsUtil;
import com.zlebank.zplatform.trade.utils.DateUtil;
import com.zlebank.zplatform.trade.utils.ObjectDynamic;
import com.zlebank.zplatform.trade.utils.OrderNumber;
import com.zlebank.zplatform.trade.utils.SynHttpRequestThread;

/**
 * Class Description
 *
 * @author guojia
 * @version
 * @date 2015年11月17日 下午2:04:49
 * @since 
 */
@Service("webGateWayService")
public class WebGateServiceImpl extends BaseServiceImpl<TxnsOrderinfoModel, Long>implements IWebGateWayService{

    private static final Log log = LogFactory.getLog(WebGateServiceImpl.class);
    @Autowired
    private ITxnsLogService txnsLogService;
    @Autowired
    private ITxnsOrderinfoDAO txnsOrderinfoDAO;
    @Autowired
    private IRouteConfigService routeConfigService;
    @Autowired
    private IMemberService memberService;
    @Autowired
    private MerchMKService merchMKService;
    @Autowired
    private ITxncodeDefService txncodeDefService;
    @Autowired
    private ITxnsSplitAccountService txnsSplitAccountService;
    @Autowired
    private IQuickpayCustService quickpayCustService;
    @Autowired 
    private ITxnsQuickpayService txnsQuickpayService;
    @Autowired
    private IAccountPayService accountPayService;
    @Autowired
    private IProdCaseService prodCaseService;
    @Autowired
    private ICashBankService cashBankService;
    @Autowired
    private ITxnsRefundService txnsRefundService;
    @Autowired
    private ITxnsWithdrawService txnsWithdrawService;
    @Autowired
    private AccountQueryService accountQueryService; 
    @Autowired
    private PersonDAO personDAO;
    @Autowired
    private IGateWayService gateWayService;
    
    
    @Transactional
    public TxnsOrderinfoModel getOrderinfoByOrderNoAndMemberId(String orderNo,String memberId) {
        return super.getUniqueByHQL("from TxnsOrderinfoModel where orderno = ? and  firmemberno = ?", new Object[]{orderNo,memberId});
    }
    
    @Transactional
    public void updateOrderToStartPay(String orderNo,String memberId) throws TradeException{
        TxnsOrderinfoModel orderinfo = super.getUniqueByHQL("from TxnsOrderinfoModel where orderno=? and  firmemberno = ? and (status=? or status = ?)", new Object[]{orderNo,memberId,"01","03"});
        if(orderinfo==null){
            throw new TradeException("T010");
        }else{
           int rows =  super.updateByHQL("update TxnsOrderinfoModel set status = ? where orderno=? and firmemberno = ? and (status=? or status = ?)", new Object[]{"02",orderNo,memberId,"01","03"});
           if(rows!=1){
               throw new TradeException("T011");
           }
        }
    }
    
    @Transactional
    public void updateOrderToFail(String orderNo,String memberId) {
        TxnsOrderinfoModel orderinfo = getOrderinfoByOrderNoAndMemberId(orderNo,memberId);
        if("02".equals(orderinfo.getStatus())){
            int rows =  super.updateByHQL("update TxnsOrderinfoModel set status = ? where orderno=? and firmemberno = ?", new Object[]{"03",orderNo,memberId});
        }
    }
    
    @Transactional
    public void submitPay(TradeBean tradeBean) throws TradeException{
        TxnsOrderinfoModel orderinfo = getOrderinfoByOrderNoAndMemberId(tradeBean.getOrderId(),tradeBean.getMerchId());
        TxnsLogModel txnsLog = txnsLogService.get(orderinfo.getRelatetradetxn());
        String reapayOrderNo = txnsQuickpayService.getReapayOrderNo(txnsLog.getTxnseqno());
        if("95000001".equals(tradeBean.getPayinstiId())){
            if(StringUtil.isEmpty(reapayOrderNo)){
                throw new TradeException("T006");
            }
        }
        if(orderinfo==null){
            throw new TradeException("T003");
        }
        if("00".equals(orderinfo.getStatus())){
            throw new TradeException("T004");
        }    
        if("02".equals(orderinfo.getStatus())){
            throw new TradeException("T009");
        }
        if(!orderinfo.getOrderamt().toString().equals(tradeBean.getAmount())){
            throw new TradeException("T033");
        }
        QuickpayCustModel card = quickpayCustService.get(tradeBean.getCardId());//quickpayCustService.getCardByBindId(tradeBean.getBindCardId());
        ResultBean routResultBean = routeConfigService.getWapTransRout(DateUtil.getCurrentDateTime(), orderinfo.getOrderamt()+"", tradeBean.getMerchId(), txnsLog.getBusicode(), tradeBean.getCardNo());
        String routId = routResultBean.getResultObj().toString();
        IQuickPayTrade quickPayTrade = null;
        try {
            quickPayTrade = TradeAdapterFactory.getInstance().getQuickPayTrade(routId);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        txnsLogService.initretMsg(txnsLog.getTxnseqno());
        //交易风控
        txnsLogService.tradeRiskControl(txnsLog.getTxnseqno(),txnsLog.getAccfirmerno(),txnsLog.getAccsecmerno(),txnsLog.getAccmemberid(),txnsLog.getBusicode(),txnsLog.getAmount()+"",card.getCardtype(),card.getCardno());
        updateOrderToStartPay(orderinfo.getOrderno(),orderinfo.getFirmemberno());
        quickPayTrade.setTradeType(TradeTypeEnum.SUBMITPAY);
        quickPayTrade.setTradeBean(tradeBean);
        TradeAdapterFactory.getInstance().getThreadPool(routId).executeMission(quickPayTrade);
    }
    /**
     * 银行卡签约
     *
     * @param trade
     * @throws TradeException
     */
    public void bankCardSign(TradeBean trade) throws TradeException{
        if (log.isDebugEnabled()) {
            log.debug("银行卡签约：开始");
        }
      
        TxnsOrderinfoModel orderinfo = getOrderinfoByOrderNoAndMemberId(trade.getOrderId(),trade.getMerchId());//获取订单信息
        if (log.isDebugEnabled()) {
            log.debug("获取订单信息："+JSON.toJSON(orderinfo));
        }
        TxnsLogModel txnsLog = txnsLogService.get(orderinfo.getRelatetradetxn());//获取交易流水信息
        if (log.isDebugEnabled()) {
            log.debug("获取交易流水信息："+JSON.toJSON(orderinfo));
        }
        if(orderinfo==null){
            throw new TradeException("T003");
        }
        if("00".equals(orderinfo.getStatus())){
            throw new TradeException("T004");
        }    
        if("02".equals(orderinfo.getStatus())){
            throw new TradeException("T009");
        }
        
        
        
        
        //获取路由信息
        ResultBean routResultBean = routeConfigService.getWapTransRout(DateUtil.getCurrentDateTime(), orderinfo.getOrderamt()+"", trade.getMerchId(), txnsLog.getBusicode(), trade.getCardNo());
        if(routResultBean.isResultBool()){
            String routId = routResultBean.getResultObj().toString();
            IQuickPayTrade quickPayTrade = null;
            try {
                quickPayTrade = TradeAdapterFactory.getInstance().getQuickPayTrade(routId);
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InstantiationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            trade.setPayinstiId(routId);
            Long Id=quickpayCustService.saveQuickpayCust(trade);
            //trade.setBindCardId(Id+"");
            trade.setCardId(Id);
            trade.setReaPayOrderNo(OrderNumber.getInstance().generateReaPayOrderId());
            trade.setMerUserId(txnsLog.getAccmemberid());
            quickPayTrade.setTradeType(TradeTypeEnum.MARGINREGISTER);
            quickPayTrade.setTradeBean(trade);
            //TradeAdapterFactory.getInstance().getThreadPool(routId).executeMission(quickPayTrade);
            ResultBean resultBean = quickPayTrade.bankSign(trade);
            
            if (resultBean.isResultBool()) {
                if(routId.equals("93000002")||routId.equals("93000003")){
                    
                }else{
                    ReaPayResultBean bean = (ReaPayResultBean) resultBean
                            .getResultObj();
                    if (!"0000".equals(bean.getResult_code())) {
                        throw new TradeException("T000",bean.getResult_msg());
                    }
                }
                
            }
        }else {
            throw new TradeException("T001");
        }
    }
    
    
    
    @Transactional
    public void bindPay(TradeBean trade) throws TradeException{
        String bindCardId = trade.getBindCardId();
        // 直接获取短信验证码
        QuickpayCustModel custCard = quickpayCustService.get(Long.valueOf(trade.getBindCardId()));
        ResultBean routResultBean = routeConfigService.getWapTransRout(DateUtil.getCurrentDateTime(), trade.getAmount()+"", trade.getMerchId(), trade.getBusicode(), trade.getCardNo());
        String routId = routResultBean.getResultObj().toString();
        trade.setReaPayOrderNo(OrderNumber.getInstance()
                .generateReaPayOrderId());
        IQuickPayTrade quickPayTrade=null;
        try {
            quickPayTrade = TradeAdapterFactory.getInstance()
                    .getQuickPayTrade(routId);
        } catch (TradeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if ("00".equals(custCard.getStatus())) {
            trade.setBindCardId(custCard.getBindcardid());

        } else {
            trade.setBindCardId("");
        }
        trade.setCertType(custCard.getIdtype());
        trade.setCardNo(custCard.getCardno());
        trade.setMobile(custCard.getPhone());
        trade.setAcctName(custCard.getAccname());
        trade.setCertId(custCard.getIdnum());
        trade.setValidthru(custCard.getValidtime());// web收银台使用
        trade.setCvv2(custCard.getCvv2());
        quickPayTrade.setTradeType(TradeTypeEnum.MARGINREGISTER);
        quickPayTrade.setTradeBean(trade);
        //ReaPayTradeThreadPool.getInstance().executeMission(quickPayTrade);
        //TradeAdapterFactory.getInstance().getThreadPool(routId).executeMission(quickPayTrade);
        ResultBean bean =quickPayTrade.marginRegister(trade);
        if (bean.isResultBool()) {
            ChannelEnmu channel = ChannelEnmu.fromValue(routId);
            switch (channel) {
                case REAPAY :
                    ReaPayResultBean resultBean = (ReaPayResultBean) bean
                    .getResultObj();
                    if (!"0000".equals(resultBean.getResult_code())) {
                        throw new TradeException("T000",resultBean.getResult_msg());
                    }
                    break;

                case CMBCWITHHOLDING :
                    if(!bean.isResultBool()){
                        throw new TradeException(bean.getErrCode(),bean.getErrMsg());
                    }
                    break;
            }
            
        }
    }
    @Transactional
    public void initMemeberPwd(String memberId,String pwd) throws TradeException{
        
        try {
            PojoPersonDeta  person =  personDAO.getPersonByMemberId(memberId);
            String passwordKey = ConsUtil.getInstance().cons.getMember_pay_password_key();
            String payPassword = Md5.getInstance().md5s(pwd+passwordKey);
            person.setPayPwd(payPassword);
            personDAO.update(person);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new TradeException("GW28");
        }
    }
    
    private int validateBalance(String memberId, Long amt) {
        List<BusiAcct> busiAccList = accountQueryService
                .getBusiACCByMid(memberId);
        BusiAcct fundAcct = null;
        // 取得资金账户
        for (BusiAcct busiAcct : busiAccList) {
            if (Usage.BASICPAY == busiAcct.getUsage()) {
                fundAcct = busiAcct;
            }
        }
        BusiAcctQuery memberAcct = accountQueryService
                .getMemberQueryByID(fundAcct.getBusiAcctCode());
        // 会员资金账户余额
        return memberAcct.getBalance().compareTo(
                Money.valueOf(new BigDecimal(amt)));
    }
    
    
    public String accountPay(TradeBean tradeBean) throws TradeException,Exception{
        Map<String, Object> model = new HashMap<String, Object>();
        ResultBean resultBean = null;
       
            if (validateBalance(tradeBean.getMerUserId(),
                    Long.valueOf(tradeBean.getAmount())) < 0) {
                throw new TradeException("T025");
            }

            AccountTradeBean accountTrade = new AccountTradeBean(
                    tradeBean.getMerUserId(), tradeBean.getMerchId(),
                    tradeBean.getPay_pwd(), tradeBean.getAmount(),
                    tradeBean.getTxnseqno());
            accountTrade.setMerchId("888888888888888");
            String pwd = accountPayService.encryptPWD(
                    accountTrade.getMerchId(), tradeBean.getPay_pwd());
            accountTrade.setPay_pwd(pwd);
            TxnsOrderinfoModel orderinfo = 
                    getOrderinfoByOrderNoAndMemberId(tradeBean.getOrderId(),
                            tradeBean.getMerchId());
            if("00".equals(orderinfo.getStatus())){
                throw new TradeException("T004");
            }    
            if("02".equals(orderinfo.getStatus())){
                throw new TradeException("T009");
            }
            if(!orderinfo.getOrderamt().toString().equals(tradeBean.getAmount())){
                throw new TradeException("T033");
            }
            accountPayService.accountPay(accountTrade);
            resultBean = new ResultBean("success");
            resultBean.setErrCode("0000");
            resultBean.setErrMsg("交易成功");
            gateWayService.saveAcctTrade(tradeBean.getTxnseqno(),
                    tradeBean.getOrderId(), resultBean);
            if (resultBean.isResultBool()) {
                ResultBean orderResp = gateWayService.generateRespMessage(
                        tradeBean.getOrderId(), tradeBean.getMerchId());
                if (orderResp.isResultBool()) {
                    TxnsOrderinfoModel gatewayOrderBean = gateWayService
                            .getOrderinfoByOrderNoAndMemberId(
                                    tradeBean.getOrderId(), tradeBean.getMerchId());
                    OrderRespBean respBean = (OrderRespBean) orderResp
                            .getResultObj();
                   
                        model.put(
                                "suburl",
                                gatewayOrderBean.getFronturl()
                                        + "?"
                                        + ObjectDynamic.generateReturnParamer(
                                                respBean, false, null));
                    
                    // 异步消息通知
                    new SynHttpRequestThread(tradeBean.getMerchId(),
                            tradeBean.getTxnseqno(), gatewayOrderBean.getBackurl(),
                            respBean.getNotifyParam()).start();
                    return gatewayOrderBean.getFronturl() + "?" + ObjectDynamic.generateReturnParamer( respBean, false, null);
                }
            } 
       
        return "";
    }
    
    
    
    
    /**
     *
     * @return
     */
    @Override
    public Session getSession() {
        
        return txnsOrderinfoDAO.getSession();
    }

}
