package com.overseas.portal.repository;

import com.overseas.portal.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByUniversityIdAndActiveTrue(Long universityId);
    Page<Course> findByActiveTrue(Pageable pageable);

    @Query("SELECT c FROM Course c WHERE c.active = true AND " +
           "(:degreeLevel IS NULL OR c.degreeLevel = :degreeLevel) AND " +
           "(:fieldOfStudy IS NULL OR LOWER(c.fieldOfStudy) LIKE LOWER(CONCAT('%',:fieldOfStudy,'%'))) AND " +
           "(:universityId IS NULL OR c.university.id = :universityId)")
    Page<Course> searchCourses(@Param("degreeLevel") String degreeLevel,
                               @Param("fieldOfStudy") String fieldOfStudy,
                               @Param("universityId") Long universityId,
                               Pageable pageable);
}
