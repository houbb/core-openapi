package io.coreplatform.openapi.marketplace.application.service;

import io.coreplatform.openapi.marketplace.domain.ApiListing;
import io.coreplatform.openapi.marketplace.port.ApiListingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApiListingService {

    private final ApiListingRepository listingRepository;

    @Transactional
    public ApiListing saveListing(Long productId, Integer featured, Integer sortOrder, String tags, String highlightText) {
        var existing = listingRepository.findByProductId(productId);
        ApiListing listing;
        if (existing.isPresent()) {
            listing = existing.get();
        } else {
            listing = ApiListing.builder()
                    .productId(productId)
                    .createTime(LocalDateTime.now())
                    .build();
        }
        if (featured != null) listing.setFeatured(featured);
        if (sortOrder != null) listing.setSortOrder(sortOrder);
        if (tags != null) listing.setTags(tags);
        if (highlightText != null) listing.setHighlightText(highlightText);
        listing.setUpdateTime(LocalDateTime.now());
        return listingRepository.save(listing);
    }

    public List<ApiListing> findFeatured(int limit) {
        return listingRepository.findFeatured(limit);
    }

    @Transactional
    public void deleteByProductId(Long productId) {
        listingRepository.deleteByProductId(productId);
    }
}