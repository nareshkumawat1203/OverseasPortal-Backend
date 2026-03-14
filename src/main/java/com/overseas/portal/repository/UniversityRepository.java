package com.overseas.portal.repository;

import com.overseas.portal.entity.University;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UniversityRepository extends JpaRepository<University, Long> {
    Page<University> findByActiveTrue(Pageable pageable);
    Page<University> findByCountryAndActiveTrue(String country, Pageable pageable);

    @Query("SELECT u FROM University u WHERE u.active = true AND " +
           "(:country IS NULL OR LOWER(u.country) = LOWER(:country)) AND " +
           "(:keyword IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<University> searchUniversities(@Param("country") String country,
                                        @Param("keyword") String keyword,
                                        Pageable pageable);
}
