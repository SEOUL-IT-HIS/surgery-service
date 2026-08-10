package kr.co.seoulit.hisback.surgery.anesthesia.service;

import java.util.List;
import kr.co.seoulit.hisback.surgery.anesthesia.dto.AnesthesiaRecordDto;

/**
 * 마취기록 서비스 인터페이스 (구현체는 AnesthesiaRecordServiceImpl)
 */
public interface AnesthesiaRecordService {
    List<AnesthesiaRecordDto> getAnesthesiaRecords(String surgeryId);

    /**
     * SL2-247: 마취기록 단건 조회
     *
     * <p>목록과 따로 두는 이유 — 상세 화면은 수술이 아니라 <b>기록 하나</b>를 지목해서 연다.
     * 목록에서 걸러 쓰면 그 수술의 기록을 전부 내려받아야 하고, 다른 수술의 기록을
     * 링크로 바로 열 수도 없다.</p>
     */
    AnesthesiaRecordDto getAnesthesiaRecord(String anesthesiaId);

    /** SL2-18/21: 마취기록 등록 (마취방법/ASA등급) */
    AnesthesiaRecordDto createAnesthesiaRecord(AnesthesiaRecordDto request);

    /** SL2-18: 활력징후는 CLOB 로그에 이어붙이는 방식이라 PATCH로 처리한다. */
    AnesthesiaRecordDto appendVitalSigns(String anesthesiaId, String vitalSignsEntry);
}
