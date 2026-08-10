package kr.co.seoulit.hisback.surgery.estimatebillinglink.service;

import kr.co.seoulit.hisback.surgery.estimatebillinglink.repository.SurgeryEstimateLinkRepository;
import org.springframework.stereotype.Service;

/**
 * 수술 견적 연계 상태 서비스 구현체 (SL2-67 조회 / SL2-68 상태변경)
 *
 * <p><b>작성 중이다.</b> 인터페이스에 메서드가 정해지면 여기에 구현을 채운다.
 * 리포지토리 주입까지만 해두었다.</p>
 *
 * <p>구현 전 정해야 할 것 — 수납(Billing) 팀과의 API 계약, 재시도 시 중복 방지(멱등성),
 * 수납이 응답하지 않을 때의 실패 정책. 자세한 내용은 BillingServiceClient 주석 참고.</p>
 */
@Service
public class SurgeryEstimateLinkServiceImpl implements SurgeryEstimateLinkService {

    private final SurgeryEstimateLinkRepository surgeryEstimateLinkRepository;

    public SurgeryEstimateLinkServiceImpl(SurgeryEstimateLinkRepository repository) {
        this.surgeryEstimateLinkRepository = repository;
    }
}
