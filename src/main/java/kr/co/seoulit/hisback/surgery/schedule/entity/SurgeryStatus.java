package kr.co.seoulit.hisback.surgery.schedule.entity;

/**
 * 수술 진행상태
 * <p>등록 시 {@link #SCHEDULED}이며, 모니터링(SL2-39)에서
 * 대기 → 마취중 → 수술중 → 회복중 → 완료 순으로 전이한다.</p>
 */
public enum SurgeryStatus {

    SCHEDULED("수술예약됨"),
    PREP("수술준비중"),
    WAITING("대기"),
    ANESTHESIA("마취중"),
    IN_PROGRESS("수술중"),
    RECOVERY("회복중"),
    COMPLETED("완료"),
    CANCELLED("취소됨");

    private final String label;

    SurgeryStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /** 종료 상태(완료/취소) 여부 */
    public boolean isTerminal() {
        return this == COMPLETED || this == CANCELLED;
    }
}
