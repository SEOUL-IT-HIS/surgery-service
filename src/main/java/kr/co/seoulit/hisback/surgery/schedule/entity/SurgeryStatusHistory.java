package kr.co.seoulit.hisback.surgery.schedule.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * 수술 상태변경 이력 엔티티 (SL2-282)
 *
 * <p>Surgery 행은 <b>현재 상태만</b> 들고 있어서, 언제 무엇에서 무엇으로 바뀌었는지는 남지 않는다.
 * 진료기록의 성격상 "예약이던 수술이 언제 취소됐는가" 같은 질문에 답할 수 있어야 하므로
 * 변경이 일어날 때마다 한 행씩 쌓는다(§21.6 이력 보존).</p>
 *
 * <p><b>왜 별도 테이블인가</b> — Surgery 에 컬럼을 더 붙이면 마지막 변경 하나밖에 못 남긴다.
 * 이력은 1:N 이라 행으로 쌓아야 한다.</p>
 *
 * <p><b>statusCd 와 progressCd 를 한 테이블에 모은 이유</b> — 둘 다 "수술이 어떻게 흘러갔는가"를
 * 보여주는 같은 성격의 기록이고, 화면에서도 시간순으로 함께 보여주는 편이 읽기 좋다.
 * 어느 쪽이 바뀐 것인지는 {@code status_type} 으로 구분한다(StatusChangeType).</p>
 *
 * <p><b>수정·삭제 API 를 두지 않는다.</b> 이력을 고칠 수 있으면 이력이 아니다.
 * 잘못 쌓인 행이 있어도 지우지 않고 그대로 둔다.</p>
 */
@Entity
@Table(name = "SURGERY_STATUS_HISTORY")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurgeryStatusHistory {

    // PK 는 내부 식별자라 서버가 UUID 로 채번한다(§14.2 `_id` → VARCHAR2(36))
    @Id
    @Column(name = "history_id", length = 36, nullable = false)
    private String historyId;

    // FK -> SURGERY.surgery_id. 물리 FK 는 걸지 않는다 — 수술이 지워질 일이 없고,
    // 이력이 본체보다 오래 남아야 하는 경우도 있어서다.
    @Column(name = "surgery_id", length = 36, nullable = false)
    private String surgeryId;

    /**
     * 어느 코드의 변경인가 — STATUS 또는 PROGRESS (StatusChangeType)
     *
     * <p>{@code _cd} 접미사를 쓰지 않은 이유 — 공통코드 그룹에서 내려오는 값이 아니라
     * 서버 내부 구분자다. {@code _cd} 를 붙이면 admin 에 코드그룹이 있는 줄 오해하게 된다(§14.2).</p>
     */
    @Column(name = "status_type", length = 20, nullable = false)
    private String statusType;

    /**
     * 바뀌기 전 값. <b>null 이 정상인 경우가 있다</b> — 수술을 처음 등록할 때는 이전 값이 없다.
     */
    @Column(name = "before_cd", length = 20)
    private String beforeCd;

    /** 바뀐 뒤 값. 이력의 핵심이라 비어 있을 수 없다. */
    @Column(name = "after_cd", length = 20, nullable = false)
    private String afterCd;

    /**
     * 사유 코드. 취소(04)처럼 이유가 있는 전이에만 채워진다.
     *
     * <p>SURGERY_CANCEL_CD 그룹이 아직 admin 에 없어 지금은 값이 들어올 일이 없다.
     * 등록되면 cancelSchedule 이 받은 사유를 여기에 함께 남긴다(SL2-227).</p>
     */
    @Column(name = "reason_cd", length = 20)
    private String reasonCd;

    /**
     * 변경한 사람의 직원 식별자.
     *
     * <p><b>프론트가 보낸 값을 그대로 받는다</b>(2026-08-11 결정). 수술 백엔드에는 로그인 세션이
     * 없어 서버가 스스로 알아낼 방법이 없기 때문이다. 클라이언트가 보낸 값이라 위조 가능성이
     * 있으므로, 감사(audit) 용도로 신뢰하려면 나중에 admin 세션에서 받아오는 방식으로 바꿔야 한다.</p>
     *
     * <p>직원 정보는 병원관리 서비스 소유라 식별자만 저장한다(§21.9). 이름이 필요하면
     * 화면이 그쪽 API 로 조회한다.</p>
     */
    @Column(name = "changed_by", length = 36)
    private String changedBy;

    /**
     * 변경 시각.
     *
     * <p>{@code @CreationTimestamp} 로 서버가 찍는다 — 클라이언트 시계를 믿을 수 없고,
     * 여러 단말에서 동시에 바꿔도 한 줄기로 정렬돼야 한다. updatable=false 라 나중에 고칠 수 없다.</p>
     */
    @CreationTimestamp
    @Column(name = "changed_at", nullable = false, updatable = false)
    private LocalDateTime changedAt;

    /**
     * 공통 감사 컬럼 (§14.1 "모든 테이블에 created_at, updated_at 공통 포함")
     *
     * <p><b>changed_at 과 값이 같아 보이는데 왜 또 두는가</b> — 처음에는 중복이라 보고 뺐지만,
     * 규칙에 이력 테이블 예외 조항이 없다. 우리끼리 예외를 만들면 다음 사람이 "어떤 테이블은
     * 있고 어떤 테이블은 없다"는 상태에서 판단 기준을 잃는다. 열두 개 테이블 중 이것만
     * 다르게 두지 않는다.</p>
     *
     * <p>둘의 <b>의미는 다르다</b>. {@code changed_at} 은 "수술 상태가 바뀐 시각"이라는 업무
     * 사실이고, {@code created_at} 은 "이 행이 저장된 시각"이라는 기록 시각이다. 지금은 같은
     * 트랜잭션에서 찍혀 값이 같지만, 나중에 지난 변경을 뒤늦게 적재하는 일이 생기면 갈라진다.
     * 화면과 API 가 쓰는 것은 changed_at 이다.</p>
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * <p>이력은 고치지 않으므로 이 값은 사실상 created_at 에서 변하지 않는다. 그래도 두는 이유는
     * 위와 같다 — 값이 변하는지가 아니라 규칙을 지키는지가 기준이다. 만약 이 값이 created_at 과
     * 달라진 행이 발견되면 그것 자체가 "이력이 수정됐다"는 신호가 되므로, 감사에 쓸모가 있다.</p>
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
