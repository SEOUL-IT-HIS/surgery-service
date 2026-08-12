package kr.co.seoulit.hisback.surgery.anesthesia.service;

import kr.co.seoulit.hisback.surgery.anesthesia.dto.AnesthesiaRecordDto;
import kr.co.seoulit.hisback.surgery.common.response.PageResponse;
import org.springframework.data.domain.Pageable;

/**
 * 마취기록 서비스 인터페이스 (구현체는 AnesthesiaRecordServiceImpl)
 */
public interface AnesthesiaRecordService {

    /**
     * SL2-34/246: 특정 수술의 마취기록 목록 (페이지 단위)
     *
     * <p>전체 목록을 돌려주던 것을 페이지 단위로 바꿨다. 한 수술의 마취기록이 많지는
     * 않지만, 활력징후를 이어붙이며 기록이 늘어나는 구조라 상한을 두지 않으면 언젠가
     * 한 번에 다 내려받게 된다.</p>
     *
     * <p>정렬 기준을 안 주면 작성 시각 역순(최신 먼저)이다 — 화면에서 가장 먼저 보고 싶은
     * 것은 마지막 기록이다. 정렬을 아예 지정하지 않으면 DB가 돌려주는 순서에 맡기게 되어
     * 같은 요청인데 페이지마다 같은 행이 나오거나 빠질 수 있다.</p>
     */
    PageResponse<AnesthesiaRecordDto> getAnesthesiaRecords(String surgeryId, Pageable pageable);

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
