package com.sparta.batch.config;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class SchedulerConfig {
    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry;

    @Scheduled(cron = "0 0/10 * * * ?")
    public void performReservationJob() {
        try {
            log.info("Performing reservation Job");
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .addLocalDateTime("date", LocalDateTime.now().minus(24, ChronoUnit.HOURS))
                    .addLong("chunkSize", 1000L)
                    .toJobParameters();

            Job job = jobRegistry.getJob("reservationJob");
            jobLauncher.run(job, jobParameters);
        } catch (NoSuchJobException e) {
            // 해당 이름의 Job이 없을 경우 예외 처리
            log.error("No such job found", e);
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            // Job 실행 중 발생할 수 있는 예외 처리
            log.error("Job execution failed", e);
        }
    }

    //@Scheduled(cron = "0 0 4 * * ?")
    @Scheduled(cron = "0 * * * * ?")
    public void performImageJob() {
        try {
            log.info("Performing Image Job");
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .addLong("chunkSize", 1000L)
                    .toJobParameters();

            Job job = jobRegistry.getJob("imageJob");
            jobLauncher.run(job, jobParameters);
        } catch (NoSuchJobException e) {
            // 해당 이름의 Job이 없을 경우 예외 처리
            log.error("No such job found", e);
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            // Job 실행 중 발생할 수 있는 예외 처리
            log.error("Job execution failed", e);
        }
    }

    @Scheduled(cron = "0 * * * * ?")
    public void performOrderJob() {
        try {
            log.info("Performing Order Job");
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .addLocalDateTime("date", LocalDateTime.now())
                    .addLong("chunkSize", 1000L)
                    .toJobParameters();

            Job job = jobRegistry.getJob("orderJob");
            jobLauncher.run(job, jobParameters);
        } catch (NoSuchJobException e) {
            // 해당 이름의 Job이 없을 경우 예외 처리
            log.error("No such job found", e);
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            // Job 실행 중 발생할 수 있는 예외 처리
            log.error("Job execution failed", e);
        }
    }
}

/*    @Scheduled(cron = "0 * * * * ?")
    public void performReservationJob() throws Exception {
        //log.info("Performing reservation Job");
        //jobLauncher.run(reservationJob, new JobParametersBuilder().addLong("time", System.currentTimeMillis()).toJobParameters());
        try {
            log.info("Performing reservation Job");
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis(), true)
                    .toJobParameters();
            jobLauncher.run(reservationJob, jobParameters);
        } catch (Exception e) {
            log.error("Error occurred while performing reservation Job", e);
        }
    }*/