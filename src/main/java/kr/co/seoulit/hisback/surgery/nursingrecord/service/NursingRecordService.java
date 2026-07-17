package kr.co.seoulit.hisback.surgery.nursingrecord.service;

import java.util.List;
import kr.co.seoulit.hisback.surgery.nursingrecord.dto.NursingRecordDto;

/**
 * 수술간호기록 서비스 인터페이스 (구현체는 NursingRecordServiceImpl)
 */
public interface NursingRecordService {
    /** SL2-61: 간호기록 조회 */
    List<NursingRecordDto> getNursingRecords(String surgeryId);

    /** SL2-58/59/60: 간호기록 작성(물품카운트/적출검체 포함해 한 건으로 등록) */
    NursingRecordDto createNursingRecord(NursingRecordDto request);
}
