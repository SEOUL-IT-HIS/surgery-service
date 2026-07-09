package kr.co.seoulit.hisback.surgery.room.entity;

/**
 * 수술실 상태
 */
public enum RoomStatus {

    AVAILABLE("사용가능"),
    IN_USE("사용중"),
    CLEANING("청소중"),
    UNAVAILABLE("사용불가");

    private final String label;

    RoomStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
