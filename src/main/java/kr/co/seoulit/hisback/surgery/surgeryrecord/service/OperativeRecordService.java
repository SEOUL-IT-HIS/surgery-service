package kr.co.seoulit.hisback.surgery.surgeryrecord.service;

import java.util.List;
import kr.co.seoulit.hisback.surgery.surgeryrecord.dto.OperativeRecordDto;

/**
 * 수술기록 서비스 인터페이스 (구현체는 OperativeRecordServiceImpl)
 */
public interface OperativeRecordService {
    /** SL2-57: 수술기록 조회 */
    List<OperativeRecordDto> getOperativeRecords(String surgeryId);

    /**
     * SL2-57: 수술기록 단건 조회
     *
     * <p>수정 화면이 목록을 거치지 않고 기록 하나를 바로 열 수 있어야 해서 따로 둔다.
     * 수정(update)이 recordId 로 대상을 지목하는 것과 짝이 맞는다.</p>
     */
    OperativeRecordDto getOperativeRecord(String recordId);

    /** SL2-55: 수술기록 작성 (op_status_cd 기본값 01작성중) */
    OperativeRecordDto createOperativeRecord(OperativeRecordDto request);

    /** SL2-56: 수술기록 수정 */
    OperativeRecordDto updateOperativeRecord(String recordId, OperativeRecordDto request);
}
