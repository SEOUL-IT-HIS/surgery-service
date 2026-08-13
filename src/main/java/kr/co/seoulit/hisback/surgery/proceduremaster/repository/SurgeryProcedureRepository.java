package kr.co.seoulit.hisback.surgery.proceduremaster.repository;

import kr.co.seoulit.hisback.surgery.proceduremaster.entity.SurgeryProcedure;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 수술항목 마스터 JPA 리포지토리
 */
public interface SurgeryProcedureRepository extends JpaRepository<SurgeryProcedure, String> {
}
