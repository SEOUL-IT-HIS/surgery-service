package kr.co.seoulit.hisback.surgery.anesthesia.repository;

import java.util.List;
import kr.co.seoulit.hisback.surgery.anesthesia.entity.AnesthesiaRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 마취기록 JPA 리포지토리
 */
public interface AnesthesiaRecordRepository extends JpaRepository<AnesthesiaRecord, String> {

    List<AnesthesiaRecord> findBySurgeryId(String surgeryId);

    /**
     * SL2-246: 한 수술의 마취기록을 페이지 단위로 조회한다.
     *
     * <p>같은 이름에 {@code Pageable} 을 하나 더 받는 메서드를 나란히 둘 수 있다 —
     * Spring Data 가 반환 타입과 인자로 구분한다. {@code Page} 로 받으면 전체 건수를
     * 세는 count 쿼리가 함께 나가므로, 화면이 마지막 페이지 번호를 알 수 있다.</p>
     */
    Page<AnesthesiaRecord> findBySurgeryId(String surgeryId, Pageable pageable);
}
