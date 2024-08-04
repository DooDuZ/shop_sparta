package com.sparta.batch.config;

import com.sparta.batch.domain.entity.order.OrderEntity;
import com.sparta.batch.domain.entity.product.ProductImageEntity;
import com.sparta.batch.domain.entity.product.ReservationEntity;
import lombok.RequiredArgsConstructor;
import com.sparta.batch.listener.JobCompletionNotificationListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class BatchConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    @Bean
    public JobCompletionNotificationListener jobCompletionNotificationListener() {
        return new JobCompletionNotificationListener();
    }

    @Bean
    public Job reservationJob(JobCompletionNotificationListener listener, Step reservationStep) {
        return new JobBuilder("reservationJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(reservationStep)
                .build();
    }

    @Bean
    public Step reservationStep(
            JpaPagingItemReader<ReservationEntity> reservationReader,
            ItemProcessor<ReservationEntity, ReservationEntity> reservationProcessor,
            ItemWriter<ReservationEntity> reservationWriter,
            JobCompletionNotificationListener jobCompletionNotificationListener
    ) {
        return new StepBuilder("reservationStep", jobRepository)
                .<ReservationEntity, ReservationEntity>chunk(1000, platformTransactionManager)
                .reader(reservationReader)
                .processor(reservationProcessor)
                .writer(reservationWriter)
                .listener(jobCompletionNotificationListener)
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public Job imageJob(JobCompletionNotificationListener listener, Step imageStep) {
        return new JobBuilder("imageJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(imageStep)
                .build();
    }

    @Bean
    public Step imageStep(
            JpaPagingItemReader<ProductImageEntity> imageReader,
            ItemProcessor<ProductImageEntity, ProductImageEntity> imageProcessor,
            ItemWriter<ProductImageEntity> imageWriter,
            JobCompletionNotificationListener jobCompletionNotificationListener
    ) {
        return new StepBuilder("imageStep", jobRepository)
                .<ProductImageEntity, ProductImageEntity>chunk(1000, platformTransactionManager)
                .reader(imageReader)
                .processor(imageProcessor)
                .writer(imageWriter)
                .listener(jobCompletionNotificationListener)
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public Job orderJob(JobCompletionNotificationListener listener, Step orderStep) {
        return new JobBuilder("orderJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(orderStep)
                .build();
    }

    @Bean
    public Step orderStep(
            JpaPagingItemReader<OrderEntity> orderReader,
            ItemProcessor<OrderEntity, OrderEntity> orderProcessor,
            ItemWriter<OrderEntity> orderWriter,
            JobCompletionNotificationListener jobCompletionNotificationListener
    ) {
        return new StepBuilder("orderStep", jobRepository)
                .<OrderEntity, OrderEntity>chunk(1000, platformTransactionManager)
                .reader(orderReader)
                //.processor(orderProcessor)
                .writer(orderWriter)
                .listener(jobCompletionNotificationListener)
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5); // 기본 스레드 풀 사이즈
        executor.setMaxPoolSize(10); // 최대 스레드 수
        executor.setQueueCapacity(25); // 대기 큐 사이즈
        executor.setThreadNamePrefix("batch-");
        executor.initialize();
        return executor;
    }
}