package kr.co.seoulit.hisback.surgery.checklist.service;

import java.util.List;
import kr.co.seoulit.hisback.surgery.checklist.dto.SurgeryChecklistDto;

/**
 * 수술안전체크리스트 서비스 인터페이스 (구현체는 SurgeryChecklistServiceImpl)
 */
public interface SurgeryChecklistService {
    List<SurgeryChecklistDto> getChecklist(String surgeryId);

    /** SL2-46/47/48: SignIn/TimeOut/SignOut 단계 등록. 이전 단계 완료 여부는 서비스에서 검증한다. */
    SurgeryChecklistDto createChecklistItem(SurgeryChecklistDto request);

    /** SL2-49: 체크리스트 사후수정(완료여부 변경) */
    SurgeryChecklistDto updateChecklistItem(String checklistId, String completedYn);
}
