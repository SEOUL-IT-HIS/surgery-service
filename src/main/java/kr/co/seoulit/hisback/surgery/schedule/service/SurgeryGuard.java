package kr.co.seoulit.hisback.surgery.schedule.service;

import kr.co.seoulit.hisback.surgery.common.exception.BusinessException;
import kr.co.seoulit.hisback.surgery.common.exception.ErrorCode;
import kr.co.seoulit.hisback.surgery.schedule.repository.SurgeryRepository;
import org.springframework.stereotype.Component;

/**
 * 수술 존재 여부 확인 (SL2-223)
 *
 * <p>동의서·마취기록·체크리스트·간호기록·수술기록지·예정항목은 모두 "어떤 수술에 딸린"
 * 데이터다. 이들을 {@code surgeryId} 로 조회할 때 <b>수술이 실제로 있는지</b> 먼저 확인한다.</p>
 *
 * <h3>왜 빈 목록이 아니라 404 인가</h3>
 * <p>없는 수술의 하위 목록을 "없음"으로 답하면, 식별자를 잘못 넣은 것과 정말 자료가 없는 것이
 * 구분되지 않는다. 화면은 "동의서가 없습니다"를 띄우고 사용자는 등록하러 가는데, 사실은
 * 존재하지 않는 수술을 보고 있는 상황이 된다. 오타는 조용히 넘어가는 것보다 즉시 드러나는
 * 편이 낫다. (2026-08-12 결정 — 이전에는 서비스마다 판단이 갈려 있었다)</p>
 *
 * <h3>비용</h3>
 * <p>조회 한 번이 두 번이 된다. {@code existsById} 는 PK 로 count 만 세는 가벼운 쿼리라
 * 목록 조회에 얹는 부담이 크지 않다. 엔티티를 통째로 읽지 않으려고 {@code findById} 가
 * 아니라 {@code existsById} 를 쓴다.</p>
 *
 * <h3>왜 schedule 패키지에 두는가</h3>
 * <p>수술 데이터의 소유자가 schedule 이라서다(§21.2). common 에 두면 common 이 schedule 을
 * 거꾸로 참조하게 되어 계층이 뒤집힌다.</p>
 */
@Component
public class SurgeryGuard {

    private final SurgeryRepository surgeryRepository;

    public SurgeryGuard(SurgeryRepository surgeryRepository) {
        this.surgeryRepository = surgeryRepository;
    }

    /**
     * 수술이 없으면 404 SUR035 를 던진다.
     *
     * <p>{@code surgeryId} 가 비어 있으면 조회할 것도 없으므로 400 으로 먼저 끊는다 —
     * 빈 문자열로 DB 를 조회해 봐야 어차피 없다.</p>
     */
    public void requireExists(String surgeryId) {
        if (surgeryId == null || surgeryId.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "surgeryId 누락");
        }
        if (!surgeryRepository.existsById(surgeryId)) {
            throw new BusinessException(ErrorCode.SURGERY_NOT_FOUND, surgeryId);
        }
    }
}
