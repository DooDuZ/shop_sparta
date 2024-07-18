package com.sparta.batch.service;

import lombok.RequiredArgsConstructor;
import com.sparta.batch.repository.ProductRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

}
