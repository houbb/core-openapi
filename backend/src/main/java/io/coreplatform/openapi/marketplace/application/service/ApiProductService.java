package io.coreplatform.openapi.marketplace.application.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.marketplace.domain.ApiProduct;
import io.coreplatform.openapi.marketplace.port.ApiProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApiProductService {

    private final ApiProductRepository productRepository;

    public IPage<ApiProduct> findPage(long page, long size, String keyword, String category, String status, Long providerId) {
        return productRepository.findPage(page, size, keyword, category, status, providerId);
    }

    public ApiProduct findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("商品不存在: " + id));
    }

    @Transactional
    public ApiProduct createProduct(String name, String description, Long providerId, Long apiId, String category) {
        ApiProduct product = ApiProduct.builder()
                .name(name)
                .description(description)
                .providerId(providerId)
                .apiId(apiId)
                .category(category != null ? category : "OTHER")
                .status("DRAFT")
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        return productRepository.save(product);
    }

    @Transactional
    public ApiProduct updateProduct(Long id, String name, String description, Long apiId, String category, String iconUrl) {
        ApiProduct product = findById(id);
        if (name != null) product.setName(name);
        if (description != null) product.setDescription(description);
        if (apiId != null) product.setApiId(apiId);
        if (category != null) product.setCategory(category);
        if (iconUrl != null) product.setIconUrl(iconUrl);
        product.setUpdateTime(LocalDateTime.now());
        return productRepository.save(product);
    }

    @Transactional
    public ApiProduct publishProduct(Long id) {
        ApiProduct product = findById(id);
        product.setStatus("PUBLISHED");
        product.setUpdateTime(LocalDateTime.now());
        return productRepository.save(product);
    }

    @Transactional
    public ApiProduct deprecateProduct(Long id) {
        ApiProduct product = findById(id);
        product.setStatus("DEPRECATED");
        product.setUpdateTime(LocalDateTime.now());
        return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public List<ApiProduct> findByProviderId(Long providerId) {
        return productRepository.findByProviderId(providerId);
    }

    public Long countByStatus(String status) {
        return productRepository.countByStatus(status);
    }

    public Long countAll() {
        return productRepository.countAll();
    }
}