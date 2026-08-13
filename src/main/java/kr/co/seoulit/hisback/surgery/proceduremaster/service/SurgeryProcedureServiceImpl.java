package kr.co.seoulit.hisback.surgery.proceduremaster.service;

import java.util.List;
import java.util.stream.Collectors;
import kr.co.seoulit.hisback.surgery.proceduremaster.dto.SurgeryProcedureDto;
import kr.co.seoulit.hisback.surgery.proceduremaster.entity.SurgeryProcedure;
import kr.co.seoulit.hisback.surgery.proceduremaster.repository.SurgeryProcedureRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 수술항목 마스터 서비스 구현체 (SL2-70 목록조회 / SL2-71 등록)
 *
 * <p>수술항목은 Surgery Service 소유 업무마스터다(SL2-71 요구사항 명시). admin 공통코드가
 * 아니므로 우리가 직접 관리한다.</p>
 */
@Service
@Transactional(readOnly = true)
public class SurgeryProcedureServiceImpl implements SurgeryProcedureService {

    private final SurgeryProcedureRepository surgeryProcedureRepository;

    public SurgeryProcedureServiceImpl(SurgeryProcedureRepository surgeryProcedureRepository) {
        this.surgeryProcedureRepository = surgeryProcedureRepository;
    }

    /**
     * SL2-70: 수술항목 마스터 목록 조회
     *
     * <p><b>{@code findAll()} 을 그대로 반환할 수 없다.</b> 그것은
     * {@code List<SurgeryProcedure>}(엔티티)이고 이 메서드는 {@code List<SurgeryProcedureDto>}
     * 를 약속했다. 타입이 달라 컴파일이 안 될 뿐 아니라, 엔티티를 그대로 응답에 실으면
     * DB 구조가 API 로 새어 나가고 JPA 영속성 컨텍스트에 묶인 객체가 직렬화된다.
     * 한 겹 끊어야 테이블이 바뀌어도 API 계약이 흔들리지 않는다.</p>
     *
     * <p>사용 여부(activeYn)로 거르지 않고 전부 돌려준다 — 마스터 관리 화면은 미사용 항목도
     * 보여줘야 다시 살릴 수 있다. 수술 등록 화면처럼 '쓸 수 있는 것만' 필요한 곳이 생기면
     * 그때 별도 메서드를 둔다.</p>
     */
    @Override
    public List<SurgeryProcedureDto> getSurgeryProcedure() {
        return surgeryProcedureRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 엔티티 → DTO 변환
     *
     * <p>필드 순서는 DTO 선언 순서와 같아야 한다. {@code @AllArgsConstructor} 가 만든 생성자는
     * 이름이 아니라 <b>순서</b>로 값을 받기 때문에, 순서가 어긋나면 컴파일은 되는데 값이
     * 뒤바뀐다(같은 String 타입끼리는 컴파일러도 못 잡는다).</p>
     */
    private SurgeryProcedureDto toDto(SurgeryProcedure p) {
        return new SurgeryProcedureDto(
                p.getProcedureCd(),
                p.getProcedureName(),
                p.getActiveYn(),
                p.getCreatedAt(),
                p.getUpdatedAt());
    }
}
