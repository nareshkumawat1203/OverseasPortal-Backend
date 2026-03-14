package com.overseas.portal.repository;

import com.overseas.portal.entity.ServiceListing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceListingRepository extends JpaRepository<ServiceListing, Long> {
    Page<ServiceListing> findByActiveTrue(Pageable pageable);
    List<ServiceListing> findByProviderIdAndActiveTrue(Long providerId);
    Page<ServiceListing> findByCategoryIdAndActiveTrue(Long categoryId, Pageable pageable);

    @Query("SELECT s FROM ServiceListing s WHERE s.active = true AND " +
           "(:categoryId IS NULL OR s.category.id = :categoryId) AND " +
           "(:keyword IS NULL OR LOWER(s.title) LIKE LOWER(CONCAT('%',:keyword,'%'))) AND " +
           "(:minPrice IS NULL OR s.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR s.price <= :maxPrice)")
    Page<ServiceListing> searchServices(@Param("categoryId") Long categoryId,
                                        @Param("keyword") String keyword,
                                        @Param("minPrice") Double minPrice,
                                        @Param("maxPrice") Double maxPrice,
                                        Pageable pageable);
}
