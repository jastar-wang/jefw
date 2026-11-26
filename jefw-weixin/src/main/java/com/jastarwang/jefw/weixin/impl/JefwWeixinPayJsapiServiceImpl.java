/*
 * Copyright 2020 Jastar Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jastarwang.jefw.weixin.impl;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.jastarwang.jefw.weixin.JefwWeixinException;
import com.jastarwang.jefw.weixin.JefwWeixinPayJsapiService;
import com.jastarwang.jefw.weixin.WeixinPayChannel;
import com.jastarwang.jefw.weixin.config.JefwWeixinProperties;
import com.jastarwang.jefw.weixin.model.pay.OrderResponse;
import com.jastarwang.jefw.weixin.model.pay.PrepayJsapiRequest;
import com.jastarwang.jefw.weixin.model.pay.PrepayJsapiResponse;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.util.PemUtil;
import com.wechat.pay.java.service.payments.jsapi.JsapiService;
import com.wechat.pay.java.service.payments.jsapi.model.Amount;
import com.wechat.pay.java.service.payments.jsapi.model.CloseOrderRequest;
import com.wechat.pay.java.service.payments.jsapi.model.Payer;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayResponse;
import com.wechat.pay.java.service.payments.jsapi.model.QueryOrderByIdRequest;
import com.wechat.pay.java.service.payments.jsapi.model.QueryOrderByOutTradeNoRequest;
import com.wechat.pay.java.service.payments.jsapi.model.SceneInfo;
import com.wechat.pay.java.service.payments.model.Transaction;

import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.Signature;
import java.time.Instant;
import java.util.Base64;

/**
 * 实现微信支付 JSAPI 下单、查单和关单接口。
 *
 * @author Jastar Wang
 * @since 2.2.0
 */
public class JefwWeixinPayJsapiServiceImpl extends JefwWeixinPayServiceImpl implements JefwWeixinPayJsapiService {

    private final JsapiService jsapiService;

    public JefwWeixinPayJsapiServiceImpl(Config config, JefwWeixinProperties jefwWeixinProperties) {
        super(config, jefwWeixinProperties);
        this.jsapiService = new JsapiService.Builder().config(config).build();
    }

    @Override
    public WeixinPayChannel getChannel() {
        return WeixinPayChannel.JSAPI;
    }

    @Override
    public OrderResponse query(String outTradeNo, String transactionId) {
        Transaction transaction;
        if (StrUtil.isNotBlank(outTradeNo)) {
            QueryOrderByOutTradeNoRequest request = new QueryOrderByOutTradeNoRequest();
            request.setOutTradeNo(outTradeNo);
            request.setMchid(jefwWeixinProperties.getPay().getMchId());
            transaction = jsapiService.queryOrderByOutTradeNo(request);
        } else if (StrUtil.isNotBlank(transactionId)) {
            QueryOrderByIdRequest request = new QueryOrderByIdRequest();
            request.setMchid(jefwWeixinProperties.getPay().getMchId());
            request.setTransactionId(transactionId);
            transaction = jsapiService.queryOrderById(request);
        } else {
            throw new IllegalArgumentException("Either outTradeNo or transactionId must have a value");
        }
        return new OrderResponse(transaction);
    }

    @Override
    public void close(String outTradeNo) {
        CloseOrderRequest request = new CloseOrderRequest();
        request.setOutTradeNo(outTradeNo);
        request.setMchid(jefwWeixinProperties.getPay().getMchId());
        jsapiService.closeOrder(request);
    }

    @Override
    public PrepayJsapiResponse prepay(PrepayJsapiRequest request) {
        String appId = getAppId();
        PrepayRequest wxRequest = buildPrepayRequest(jefwWeixinProperties, request, appId);
        PrepayResponse wxResponse = jsapiService.prepay(wxRequest);
        return buildPrepayResponse(jefwWeixinProperties, wxResponse, appId);
    }

    @Override
    protected String getAppId() {
        return jefwWeixinProperties.getOa().getAppId();
    }

    private PrepayRequest buildPrepayRequest(JefwWeixinProperties jefwWeixinProperties, PrepayJsapiRequest request, String appId) {
        PrepayRequest wxRequest = new PrepayRequest();
        wxRequest.setAppid(appId);
        wxRequest.setMchid(jefwWeixinProperties.getPay().getMchId());
        wxRequest.setDescription(request.getDescription());
        wxRequest.setOutTradeNo(request.getOutTradeNo());
        wxRequest.setNotifyUrl(request.getNotifyUrl());
        wxRequest.setAmount(getAmount(request.getTotalAmount()));
        wxRequest.setPayer(getPayer(request.getPayerOpenid()));
        wxRequest.setSceneInfo(getSceneInfo(request.getPayerClientIp()));
        if (request.getPayExpireTime() != null) {
            wxRequest.setTimeExpire(formatRFC3339(request.getPayExpireTime()));
        }
        if (StrUtil.isNotBlank(request.getAttach())) {
            wxRequest.setAttach(request.getAttach());
        }
        // wxRequest.setGoodsTag();
        // wxRequest.setLimitPay();
        // wxRequest.setSupportFapiao();
        // wxRequest.setDetail();
        // wxRequest.setSettleInfo();
        return wxRequest;
    }

    private PrepayJsapiResponse buildPrepayResponse(JefwWeixinProperties jefwWeixinProperties, PrepayResponse wxResponse, String appId) {
        PrepayJsapiResponse response = new PrepayJsapiResponse();
        response.setPrepayId(wxResponse.getPrepayId());
        response.setPrepayPkg("prepay_id=" + response.getPrepayId());
        response.setTimeStamp(Instant.now().getEpochSecond() + "");
        response.setNonceStr(RandomUtil.randomStringUpper(32));
        response.setSignType("RSA");
        response.setPaySign(buildPrepaySign(appId, jefwWeixinProperties.getPay().getPrivateKeyPath(), response));
        return response;
    }

    private String buildPrepaySign(String appId, String privateKeyPath, PrepayJsapiResponse response) {
        String sb = appId + "\n" + response.getTimeStamp() + "\n" + response.getNonceStr() + "\n" + response.getPrepayPkg() + "\n";
        try {
            // 签名方式
            Signature sign = Signature.getInstance("SHA256withRSA");
            // 读取私钥
            InputStream stream = this.getClass().getClassLoader().getResourceAsStream(privateKeyPath);
            PrivateKey privateKey = PemUtil.loadPrivateKeyFromString(IoUtil.readUtf8(stream));
            sign.initSign(privateKey);
            sign.update(sb.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(sign.sign());
        } catch (Exception e) {
            throw new JefwWeixinException("预支付发起成功，但构建签名时失败", e);
        }
    }

    private Amount getAmount(BigDecimal amountYuan) {
        if (amountYuan == null || amountYuan.signum() <= 0) {
            throw new IllegalArgumentException("预支付金额必须大于零");
        }
        Amount amount = new Amount();
        try {
            amount.setTotal(amountYuan.scaleByPowerOfTen(2).intValueExact());
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException("预支付金额必须为整数分，且不能超过 2147483647 分", e);
        }
        return amount;
    }

    private Payer getPayer(String openid) {
        Payer payer = new Payer();
        payer.setOpenid(openid);
        return payer;
    }

    private SceneInfo getSceneInfo(String clientIp) {
        SceneInfo info = new SceneInfo();
        info.setPayerClientIp(clientIp);
        return info;
    }

}
