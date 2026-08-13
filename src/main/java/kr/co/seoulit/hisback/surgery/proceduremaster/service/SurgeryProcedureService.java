package kr.co.seoulit.hisback.surgery.proceduremaster.service;

import java.util.List;
import kr.co.seoulit.hisback.surgery.proceduremaster.dto.SurgeryProcedureDto;

/**
 * 수술항목 마스터 서비스 로직 (구현체는 SurgeryProcedureServiceImpl)
 */
public interface SurgeryProcedureService {

    /**
     * SL2-70: 수술항목 마스터 목록 조회
     *
     * <p><b>{@code List<SurgeryProcedureDto>} 로 적어야 한다.</b> {@code <List>SurgeryProcedureDto}
     * 는 꺾쇠가 반대라 다른 뜻이 된다 — {@code List} 라는 이름의 타입 변수를 선언하는
     * <b>제네릭 메서드</b>가 되고, 반환 타입은 {@code SurgeryProcedureDto} 하나가 된다.
     * 그래서 구현체의 {@code List<...>} 와 시그니처가 어긋나 컴파일이 실패한다.</p>
     */
    List<SurgeryProcedureDto> getSurgeryProcedure();
}
