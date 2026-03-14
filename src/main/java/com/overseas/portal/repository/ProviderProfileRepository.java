package com.overseas.portal.repository;

import com.overseas.portal.entity.ProviderProfile;
import com.overseas.portal.enums.ProviderType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProviderProfileRepository extends JpaRepository<ProviderProfile, Long> {
    Optional<ProviderProfile> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
    Page<ProviderProfile> findByActiveTrue(Pageable pageable);
    Page<ProviderProfile> findByProviderTypeAndActiveTrue(ProviderType type, Pageable pageable);
    Page<ProviderProfile> findByCountryAndActiveTrue(String country, Pageable pageable);

    @Query("SELECT p FROM ProviderProfile p WHERE p.active = true AND " +
           "(:country IS NULL OR p.country = :country) AND " +
           "(:providerType IS NULL OR p.providerType = :providerType) AND " +
           "(:verified IS NULL OR p.verified = :verified)")
    Page<ProviderProfile> findWithFilters(@Param("country") String country,
                                          @Param("providerType") ProviderType providerType,
                                          @Param("verified") Boolean verified,
                                          Pageable pageable);
}
