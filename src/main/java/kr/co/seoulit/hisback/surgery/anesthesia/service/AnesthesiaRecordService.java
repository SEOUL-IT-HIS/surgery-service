package kr.co.seoulit.hisback.surgery.anesthesia.service;

import java.util.List;
import kr.co.seoulit.hisback.surgery.anesthesia.dto.AnesthesiaRecordDto;

/**
 * 마취기록 서비스 인터페이스 (구현체는 AnesthesiaRecordServiceImpl)
 */
public interface AnesthesiaRecordService {
    List<AnesthesiaRecordDto> getAnesthesiaRecords(String surgeryId);

    /** SL2-18/21: 마취기록 등록 (마취방법/ASA등급) */
    AnesthesiaRecordDto createAnesthesiaRecord(AnesthesiaRecordDto request);

    /** SL2-18: 활력징후는 CLOB 로그에 이어붙이는 방식이라 PATCH로 처리한다. */
    AnesthesiaRecordDto appendVitalSigns(String anesthesiaId, String vitalSignsEntry);
}
