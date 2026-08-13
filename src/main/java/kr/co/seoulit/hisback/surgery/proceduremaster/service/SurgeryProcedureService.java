package kr.co.seoulit.hisback.surgery.proceduremaster.service;

import java.util.List;
import kr.co.seoulit.hisback.surgery.proceduremaster.dto.SurgeryProcedureDto;

/**
 * 수술항목 마스터 서비스 로직 (구현체는 SurgeryProcedureServiceImpl)
 *
 * <p>삭제가 없는 것에 주목할 것 — 과거 수술기록이 코드를 참조하므로 지우지 않고
 * {@code activeYn='N'} 으로 내린다(§21.6).</p>
 */
public interface SurgeryProcedureService {

    /** SL2-70: 수술항목 마스터 목록 조회. 사용 여부와 무관하게 전부 돌려준다. */
    List<SurgeryProcedureDto> getSurgeryProcedures();

    /** SL2-70: 수술항목 단건 조회. 없으면 404 SUR055. */
    SurgeryProcedureDto getSurgeryProcedureById(String procedureCd);

    /** SL2-71: 수술항목 등록. 코드가 중복이면 400 SUR056. */
    SurgeryProcedureDto createSurgeryProcedure(SurgeryProcedureDto request);

    /** SL2-71: 수술항목 수정(전체 교체). 코드는 바꿀 수 없다. */
    SurgeryProcedureDto updateSurgeryProcedure(SurgeryProcedureDto request);

    /** SL2-71: 수술항목 부분 수정. 보낸 항목만 반영한다 — 주로 사용 여부 토글에 쓴다. */
    SurgeryProcedureDto patchSurgeryProcedure(SurgeryProcedureDto request);
}
