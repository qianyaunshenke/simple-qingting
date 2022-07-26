package com.devops.test.wxpub;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import com.devops.test.BaseTest;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @date: 2021/3/31 14:42
 */
public class WxPubTest extends BaseTest {

    @Autowired
    private WxMpService wxService;
    @Autowired
    private WxMaService wxMaService;

    @Test
    public void testWxPub() throws Exception {
//        String openid = "oNhIixOt-2mt24AYrT_K6JCNBB3w";
//        WxMpUser wxMpUser = this.wxService.getUserService()
//                .userInfo(openid, null);
//        System.out.println("wxMpUser:" + wxMpUser);
//        SmtSubscribeRecord subscribeRecord = subscribeRecordService.selectSubscribeRecordByOpenid(openid);
//        if (subscribeRecord == null) {
//            subscribeRecord = new SmtSubscribeRecord();
//            BeanUtils.copyProperties(wxMpUser, subscribeRecord);
//            subscribeRecordService.insertSmtSubscribeRecord(subscribeRecord);
//            System.out.println(" insert wx subscribe success.");
//
//        } else {
//            System.out.println("wx subscribe record  already exist.");
//
//        }

        wxMaService.getAccessToken();
    }

    @Test
    public void testSendTemplateMsg() throws WxErrorException {
        WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
                .toUser("oNhIixOt-2mt24AYrT_K6JCNBB3w")
                .templateId("RY-St3Rgns1jWP-np_sYKYDenMa_ipFIWtC9WA3BWwM")
                .url("https://developers.weixin.qq.com/doc/offiaccount/Message_Management/Template_Message_Interface.html")
                .build();

        templateMessage.addData(new WxMpTemplateData("first", "尊敬的家长，您已经充值成功"))
                .addData(new WxMpTemplateData("keyword1", "小程序充值"))
                .addData(new WxMpTemplateData("keyword2", "1008686100"))
                .addData(new WxMpTemplateData("keyword3", "100.12元"))
                .addData(new WxMpTemplateData("keyword4", "2021年2月29日"))
                .addData(new WxMpTemplateData("remark", "感谢您的使用"));
        String msgId = this.wxService.getTemplateMsgService().sendTemplateMsg(templateMessage);
        System.out.println("testSendTemplateMsg:" + msgId);
    }

    @Test
    public void testGetSessionKey() throws Exception {

        WxMaJscode2SessionResult sessionResult = this.wxMaService.getUserService().getSessionInfo("041Oak000KqnBL1W1x0000Zlzt0Oak0z");
        System.out.println("testGetSessionKey:" + sessionResult);
    }

    @Test
    public void testGetUserInfo() {
        WxMaUserInfo userInfo = this.wxMaService.getUserService().getUserInfo("/WW3WIrgNu+n2RAiRMY5jg==",
                "VVPl+t7Rht29jHXatldrV7u9Z7RkFgfxu2p2CdxDdlUOk+4y1RjL3seRjRs6uUw+O5ZvZIZdiU/TkemPMd3aAH0L+GHgbrfCnEcg7ZygCMnp35SFM7BxKikfwPzUKh1bSHK6o9969nL0T0pO9kGAm1Qt5b0KHQ/j24fcU5WHAkqYtrqRYe02+zfd7oafvMigikpLdw+HrRJ3zvSgOVZIwpTHSXLOv7domfHUuyPr4mW4JjOoabrEar0S6lKE+NM0TVlua/YDXJiFLDjtnDndUdZVWJgcmwYz7XvpSUdRVbgGHOeuXJ4vocjZBWY+WuHEtGq4+iicTN0mQS/VFc64t7THYBZJOpuo8wMrtJhRq4tm2DxpwWhSBlGwVTi3jpWAkO5SO0tH2V7McFNIAgLernFh7FspVAIC10Leqjjh70m2FYvprGrLu6jQkDaQC+Av51TDhXGyYQ9fYnDxniq8sA==",
                "7pfDFeVoZO9BIgyZIlwOgg==");
        System.out.println("testGetUserInfo：" + userInfo.toString());


    }
}
