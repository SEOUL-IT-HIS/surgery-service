package kr.co.seoulit.hisback.surgery.nursingrecord.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import kr.co.seoulit.hisback.surgery.nursingrecord.dto.NursingRecordDto;
import kr.co.seoulit.hisback.surgery.nursingrecord.entity.NursingRecord;
import kr.co.seoulit.hisback.surgery.nursingrecord.repository.NursingRecordRepository;
import kr.co.seoulit.hisback.surgery.schedule.service.SurgeryGuard;
import org.springframework.stereotype.Service;

/**
 * 수술간호기록 서비스 구현체 (SL2-52)
 *
 * <p>세 가지를 한 행에 담는다 — 물품 수량 확인(SL2-59), 검체 정보(SL2-60), 기록 상태.
 * 간호사가 수술 한 건에서 한 번에 작성하는 서식이라 분리하지 않았다.</p>
 *
 * <p>검체 바코드는 여기서 <b>생성하지 않고 받아 적기만</b> 한다. 바코드 채번은 진단검사 소관이라
 * 수술이 대신 만들면 서비스 경계를 넘는다(§21.1). 저장하는 것도 식별자뿐이고 검체의
 * 상세 정보는 갖지 않는다(§21.9).</p>
 */
@Service
public class NursingRecordServiceImpl implements NursingRecordService {

    private final NursingRecordRepository nursingRecordRepository;

    /** SL2-223: 하위 목록 조회 전에 수술 존재를 확인한다 */
    private final SurgeryGuard surgeryGuard;

    public NursingRecordServiceImpl(
            NursingRecordRepository nursingRecordRepository, SurgeryGuard surgeryGuard) {
        this.nursingRecordRepository = nursingRecordRepository;
        this.surgeryGuard = surgeryGuard;
    }

    /** SL2-61: 특정 수술의 간호기록 목록 조회. */
    @Override
    public List<NursingRecordDto> getNursingRecords(String surgeryId) {
        // SL2-223: 없는 수술이면 빈 목록이 아니라 404 다(2026-08-12 결정)
        surgeryGuard.requireExists(surgeryId);
        return nursingRecordRepository.findBySurgeryId(surgeryId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /** SL2-58: 간호기록 작성. */
    @Override
    public NursingRecordDto createNursingRecord(NursingRecordDto request) {
        // SL2-327: 없는 수술에는 기록을 남길 수 없다.
        //   조회에는 8/12 부터 이 가드가 있었는데 작성에는 빠져 있었다(2026-08-27 발견).
        //   조회만 막으면 오타로 만든 행이 DB 에 남고, 정작 그 수술을 열어 보면
        //   404 라 화면에서는 영영 보이지 않는다 — 지우지도 못하는 고아 행이 된다.
        //   동의서·체크리스트·마취기록은 이미 작성에도 걸고 있어 그쪽에 맞춘다.
        surgeryGuard.requireExists(request.getSurgeryId());

        // PK는 내부 식별자라 서버가 UUID로 채번한다(§14.2 `_id` → VARCHAR2(36))
        String nursingRecordId =
                request.getNursingRecordId() != null
                        ? request.getNursingRecordId()
                        : UUID.randomUUID().toString();
        NursingRecord record =
                NursingRecord.builder()
                        .nursingRecordId(nursingRecordId)
                        .surgeryId(request.getSurgeryId())
                        .itemCountResultCd(request.getItemCountResultCd())
                        .specimenBarcode(request.getSpecimenBarcode())
                        .specimenTypeCd(request.getSpecimenTypeCd())
                        // 상태를 안 보내면 작성중(01)으로 시작한다 — 수술 중에는 미완성 상태로 쌓이고,
                        // 확정은 별도 행위이므로 기본값을 완료로 두지 않는다
                        .recordStatusCd(
                                request.getRecordStatusCd() != null ? request.getRecordStatusCd() : "01")
                        .build();
        return toDto(nursingRecordRepository.save(record));
    }

    /** 엔티티 → DTO 변환. 필드명은 프론트 types.ts 의 NursingRecord 와 1:1로 맞춘다. */
    private NursingRecordDto toDto(NursingRecord r) {
        return new NursingRecordDto(
                r.getNursingRecordId(),
                r.getSurgeryId(),
                r.getItemCountResultCd(),
                r.getSpecimenBarcode(),
                r.getSpecimenTypeCd(),
                r.getRecordStatusCd(),
                r.getCreatedAt(),
                r.getUpdatedAt());
    }
}
