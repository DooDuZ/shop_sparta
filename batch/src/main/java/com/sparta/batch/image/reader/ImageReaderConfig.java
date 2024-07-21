package com.sparta.batch.image.reader;

import com.sparta.batch.domain.entity.product.ProductImageEntity;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ImageReaderConfig {
    private final EntityManagerFactory entityManagerFactory;

    @Bean
    @StepScope
    public JpaPagingItemReader<ProductImageEntity> imageReader(
            @Value("#{jobParameters['chunkSize']}") int chunkSize
    ) {
        JpaPagingItemReader<ProductImageEntity> reader = new JpaPagingItemReader<>();
        reader.setQueryString(
                "SELECT pi FROM productImage pi " +
                        "JOIN pi.productEntity p " +
                        "WHERE pi.imageVersion <> p.imageVersion AND pi.isDeleted = false"
        );

        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setPageSize(chunkSize);
        reader.setSaveState(false);
        return reader;
    }
}


/*@Bean
@StepScope
public JdbcPagingItemReader<ProductImageEntity> imageReader(
        @Value("#{jobParameters['chunkSize']}") int chunkSize,
        DataSource dataSource
) {
    Map<String, Object> parameterValues = new HashMap<>();
    parameterValues.put("deleted", false);

    return new JdbcPagingItemReaderBuilder<ProductImageEntity>()
            .name("imageReader")
            .dataSource(dataSource)
            .selectClause("SELECT pi.*")
            .fromClause("FROM product_image pi JOIN product p ON p.product_id = pi.product_id")
            .whereClause("WHERE pi.image_version != p.image_version AND pi.is_deleted = :deleted")
            .sortKeys(Collections.singletonMap("pi.product_image_id", Order.ASCENDING))
            .parameterValues(parameterValues)
            .rowMapper(new BeanPropertyRowMapper<>(ProductImageEntity.class))
            .pageSize(chunkSize)
            .saveState(false)
            .build();
}*/
