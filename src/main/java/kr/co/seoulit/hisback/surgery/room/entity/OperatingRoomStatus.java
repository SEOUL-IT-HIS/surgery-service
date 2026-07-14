package kr.co.seoulit.hisback.surgery.room.entity;

/**
 * 수술실 상태코드 (RoomStatusCode)
 * <p>제거(SL2-8)는 물리 삭제 대신 {@link #CLOSED}로의 상태 전이로 처리한다.
 * (개발표준가이드 §14: 스냅샷/이력 보존을 위해 물리 DELETE를 지양)</p>
 */
public enum OperatingRoomStatus {

    AVAILABLE("사용가능"),
    IN_USE("사용중"),
    MAINTENANCE("점검중"),
    CLOSED("폐쇄");

    private final String label;

    OperatingRoomStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /** 제거(비활성화) 상태 여부 */
    public boolean isClosed() {
        return this == CLOSED;
    }
}
