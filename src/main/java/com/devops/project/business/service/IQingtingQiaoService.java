package com.devops.project.business.service;

public interface IQingtingQiaoService {
    String getToken(Long userId);

    void syncStopword();

    void syncBotCorpus();

    boolean sendSms(String phone, String content);
}
