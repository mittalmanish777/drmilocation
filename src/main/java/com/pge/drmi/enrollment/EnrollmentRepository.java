package com.pge.drmi.enrollment;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface EnrollmentRepository extends CrudRepository<Enrollment, Integer> {

	List<Enrollment> findByStatus(String status, Pageable topTen);

	List<Enrollment> findByEnrollmentId(Integer id, Pageable topTen);

	List<Enrollment> findByUuidAndEnrollmentIdNotIn(String UuId, Integer enrollmentId);
}
