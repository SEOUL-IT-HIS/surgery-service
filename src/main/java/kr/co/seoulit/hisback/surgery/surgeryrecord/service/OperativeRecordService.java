package kr.co.seoulit.hisback.surgery.surgeryrecord.service;

import java.util.List;
import kr.co.seoulit.hisback.surgery.surgeryrecord.dto.OperativeRecordDto;

/**
 * 수술기록 서비스 인터페이스 (구현체는 OperativeRecordServiceImpl)
 */
public interface OperativeRecordService {
    /** SL2-57: 수술기록 조회 */
    List<OperativeRecordDto> getOperativeRecords(String surgeryId);

    /** SL2-55: 수술기록 작성 (op_status_cd 기본값 01작성중) */
    OperativeRecordDto createOperativeRecord(OperativeRecordDto request);

    /** SL2-56: 수술기록 수정 */
    OperativeRecordDto updateOperativeRecord(String recordId, OperativeRecordDto request);
}
