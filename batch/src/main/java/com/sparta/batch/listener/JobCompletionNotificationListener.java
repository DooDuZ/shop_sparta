package com.sparta.batch.listener;

import java.time.Duration;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;


@Slf4j
public class JobCompletionNotificationListener implements JobExecutionListener {
    private Instant startTime;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        startTime = Instant.now();
        log.info("Job 시작...");
        log.info("Job Name: {}", jobExecution.getJobInstance().getJobName());
        log.info("Job Parameters: {}", jobExecution.getJobParameters());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        Duration jobDuration = Duration.between(startTime, Instant.now());
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("Job 완료!");
            log.info("Job Duration: {} ms", jobDuration.toMillis());
        } else if (jobExecution.getStatus() == BatchStatus.FAILED) {
            log.error("Job 실패!");
            log.error("Job Duration: {} ms", jobDuration.toMillis());
        }

        jobExecution.getStepExecutions().forEach(stepExecution -> {
            log.info("Step Name: {}", stepExecution.getStepName());
            log.info("Read Count: {}", stepExecution.getReadCount());
            log.info("Write Count: {}", stepExecution.getWriteCount());
            log.info("Commit Count: {}", stepExecution.getCommitCount());
        });
    }
}
