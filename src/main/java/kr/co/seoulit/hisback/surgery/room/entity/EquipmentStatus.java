package kr.co.seoulit.hisback.surgery.room.entity;

/**
 * 수술장비 상태
 */
public enum EquipmentStatus {

    AVAILABLE("사용가능"),
    IN_USE("사용중"),
    OUT("출고"),
    MAINTENANCE("정비중");

    private final String label;

    EquipmentStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
