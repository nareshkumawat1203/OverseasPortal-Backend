package com.overseas.portal.repository;

import com.overseas.portal.entity.StudentApplication;
import com.overseas.portal.enums.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentApplicationRepository extends JpaRepository<StudentApplication, Long> {
    Page<StudentApplication> findByStudentId(Long studentId, Pageable pageable);
    Page<StudentApplication> findByUniversityId(Long universityId, Pageable pageable);
    List<StudentApplication> findByStudentIdAndStatus(Long studentId, ApplicationStatus status);
    long countByStudentId(Long studentId);
    long countByStudentIdAndStatusNot(Long studentId, ApplicationStatus status);

    @Query("SELECT a FROM StudentApplication a WHERE " +
           "(:status IS NULL OR a.status = :status) AND " +
           "(:universityId IS NULL OR a.university.id = :universityId)")
    Page<StudentApplication> findWithFilters(@Param("status") ApplicationStatus status,
                                             @Param("universityId") Long universityId,
                                             Pageable pageable);
}
