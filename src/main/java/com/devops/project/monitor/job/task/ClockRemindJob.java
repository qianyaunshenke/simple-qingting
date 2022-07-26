package com.devops.project.monitor.job.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component("clockRemindJob")
public class ClockRemindJob {


    public void doWork() {

        log.info("打卡提醒定时任务处理开始...");
        log.info("ttttttttt...tttttt");

        log.info("打卡提醒定时任务处理结束...");
    }
}
