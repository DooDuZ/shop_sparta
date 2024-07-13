package com.sparta.shop_sparta.util.thread;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ThreadMonitor {
    private final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

    @Scheduled(fixedRate = 60000)  // 1분마다 실행
    public void logThreadStatus() {
        int threadCount = threadMXBean.getThreadCount();
        int daemonThreadCount = threadMXBean.getDaemonThreadCount();
        int peakThreadCount = threadMXBean.getPeakThreadCount();
        long totalStartedThreadCount = threadMXBean.getTotalStartedThreadCount();

        log.info("Current Thread Count: {}", threadCount);
        log.info("Daemon Thread Count: {}", daemonThreadCount);
        log.info("Peak Thread Count: {}", peakThreadCount);
        log.info("Total Started Thread Count: {}", totalStartedThreadCount);
        /*
        long[] threadIds = threadMXBean.getAllThreadIds();
        for (long threadId : threadIds) {
            logThreadInfo(threadId);
        }
         */
    }

    private void logThreadInfo(long threadId) {
        String threadInfo = threadMXBean.getThreadInfo(threadId).toString();
        log.info("Thread Info: {}", threadInfo);
    }

}
