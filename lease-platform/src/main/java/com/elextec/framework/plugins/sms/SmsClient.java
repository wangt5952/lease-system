package com.elextec.framework.plugins.sms;

import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.common.response.MessageResponse;
import com.elextec.framework.exceptions.BizException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 短信工具类.
 * Created by wangtao on 2018/1/24.
 */
@Component
public class SmsClient {
    /** 日志. */
    private final Logger logger = LoggerFactory.getLogger(SmsClient.class);

    @Value("${localsetting.sms.url}")
    private String url;

    @Value("${localsetting.sms.username}")
    private String userName;

    @Value("${localsetting.sms.password}")
    private String password;

    @Value("${localsetting.sms.verycode}")
    private String veryCode;

    @Value("${localsetting.sms.tempid1}")
    private String tempId1;

    /**
     * 根据模板1发送短信.
     * @param mobile 手机号码(多个手机号码用 , 分割，最多100个手机号码)
     * @param content 发送内容
     * @return 发送结果
     */
    public MessageResponse sendSmsByTemplate1(final String mobile, final String content) {
        try {
            // 准备参数
            StringBuffer httpEntityStr = new StringBuffer("method=sendMsg");
            httpEntityStr.append("&username=").append(userName);
            httpEntityStr.append("&password=").append(password);
            httpEntityStr.append("&veryCode=").append(veryCode);
            httpEntityStr.append("&msgtype=2");
            httpEntityStr.append("&tempid=").append(tempId1);
            httpEntityStr.append("&mobile=").append(mobile);
            httpEntityStr.append("&content=").append(URLEncoder.encode("@1@=" + content, "UTF-8"));
            httpEntityStr.append("&code=utf-8");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity httpEntity = new HttpEntity(httpEntityStr.toString(), headers);
            // 发送短信并获得发送结果
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
            // 解析发送结果
            JAXBContext context = JAXBContext.newInstance(SmsResult.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            SmsResult sr = (SmsResult) unmarshaller.unmarshal(new StringReader(responseEntity.getBody()));
            if (SmsConstants.CODE_SEND_MSG_0.equals(sr.getStatusAndMessage().getStatus())) {
                return new MessageResponse(RunningResult.SUCCESS.code(), "短信(" + mobile + ")已发送", sr);
            } else {
                return new MessageResponse(RunningResult.SMS_SEND_FAIL.code(), SmsConstants.getSendMsgMessage(sr.getStatusAndMessage().getStatus()), sr);
            }
        } catch (Exception ex) {
            logger.error("短信(" + mobile + ")发送失败", ex);
            throw new BizException(RunningResult.SMS_SEND_FAIL.code(), "短信(" + mobile + ")发送失败", ex);
        }
    }
}
