package kr.co.seoulit.hisback.surgery.surgeryorder.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * 수술 오더 엔티티 (SL2-36 진료 요청 / SL2-44 응급 요청)
 *
 * <p>진료·응급실이 "이 환자를 이렇게 집도해 달라"고 보낸 <b>요청</b>이다.
 * 수술 그 자체가 아니다.</p>
 *
 * <h3>왜 SURGERY 와 따로 두는가</h3>
 * <p>§21.7 의 판단 기준 중 <b>1번(부모 없이 자식이 존재 가능한가)</b>과 <b>4번(취소·재접수가
 * 가능한가)</b>에 걸린다 — 오더는 수술 없이 존재할 수 있고, 반려 후 재요청이 실제로 일어난다. </p>
 *
 * <h3>수락하면 어떻게 되는가</h3>
 * <p>수술실을 배정해 SURGERY 를 만들고, 그 식별자를 {@link #surgeryId} 에 적어 둔다.
 * 오더 한 건에 수술 한 건이라 1:1 이고, 반려된 오더는 이 값이 비어 있다.</p>
 */
@Entity
@Table(name = "SURGERY_ORDER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurgeryOrder {

    // PK 는 내부 식별자라 서버가 UUID 로 채번한다(§14.2 `_id` → VARCHAR2(36))
    @Id
    @Column(name = "ORDER_ID", length = 36, nullable = false)
    private String orderId;

    /** 환자 식별자. 환자 서비스 소유라 식별자만 보유한다(§21.9) */
    @Column(name = "PATIENT_ID", length = 36, nullable = false)
    private String patientId;

    /**
     * 내원 식별자.
     *
     * <p><b>청구 연동(SL2-72)에 필요한 값이다</b> — billing 이 요구하는 항목이
     * {@code (patientId, visitId, surgeryCode, quantity)} 인데, 예전 구조에는 이 값을
     * 담을 자리가 없어 "어느 내원 건으로 청구할지" 알 수 없었다.</p>
     *
     * <p>진료·응급이 보내주지 않으면 비어 있다. 필수로 두지 않은 이유 — 응급은 접수보다
     * 수술 요청이 먼저 올라오는 경우가 있어, 값을 강제하면 요청 자체를 못 넣는다.</p>
     */
    @Column(name = "VISIT_ID", length = 36)
    private String visitId;

    /** 요청한 집도의. 병원관리 서비스 소유라 식별자만 보유한다 */
    @Column(name = "SURGEON_ID", length = 36, nullable = false)
    private String surgeonId;

    /**
     * 희망 수술일 (§14.2 `_dt` → DATE).
     *
     * <p>확정일이 아니다. 수술실 사정에 맞춰 수락 단계에서 조정될 수 있고, 조정된 값은
     * SURGERY 쪽에 적힌다. 이쪽은 "진료가 원래 원했던 날"로 남는다.</p>
     */
    @Column(name = "REQUESTED_DT", nullable = false)
    private LocalDate requestedDt;

    /**
     * 응급 여부 (§14.2 `_yn` → CHAR(1), 'Y'/'N').
     *
     * <p>요청 <b>경로</b>가 정한다. 일반 요청이 스스로 'Y' 를 실어 배정 우선순위를
     * 가로채지 못하게, 클라이언트가 보낸 값은 쓰지 않는다.</p>
     */
    @Column(name = "EMERGENCY_YN", length = 1, nullable = false)
    private String emergencyYn;

    /** 오더 상태 (OrderStatus: 00접수 / 01수락 / 02반려) */
    @Column(name = "ORDER_STATUS_CD", length = 36, nullable = false)
    private String orderStatusCd;

    /**
     * 반려 사유 코드. 반려(02)일 때만 채워진다.
     *
     * <p>자유 문구가 아니라 코드로 받는다 — 문구로 받으면 "환자 거부"와 "환자거부"가
     * 다른 사유로 집계된다.</p>
     */
    @Column(name = "REJECT_REASON_CD", length = 36)
    private String rejectReasonCd;

    /** 요청 수술 종류 코드 */
    @Column(name = "SURGERY_TYPE_CD", length = 36)
    private String surgeryTypeCd;

    /** 요청 수술명. 진료가 적어 보내는 값이라 우리 원본이다 */
    @Column(name = "SURGERY_NAME", length = 100)
    private String surgeryName;

    /**
     * 요청자(직원) 식별자.
     *
     * <p>수술 서비스에 로그인 세션이 없어 서버가 알아낼 수 없다. 진료·응급이 보내면
     * 저장하고, 안 보내면 비어 있다. 상태변경 이력의 {@code changed_by} 와 같은 처지다.</p>
     */
    @Column(name = "ORDERED_BY", length = 36)
    private String orderedBy;

    /**
     * 수락 시 만들어진 수술의 식별자. 반려·접수 상태면 비어 있다.
     *
     * <p>물리 FK 를 걸지 않는다 — 수술이 지워질 일이 없고, 오더가 수술보다 오래 남아야
     * 하는 경우(반려 이력 추적)도 있어서다. 다른 테이블과 같은 방침이다.</p>
     */
    @Column(name = "SURGERY_ID", length = 36)
    private String surgeryId;

    @CreationTimestamp
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt;
}
