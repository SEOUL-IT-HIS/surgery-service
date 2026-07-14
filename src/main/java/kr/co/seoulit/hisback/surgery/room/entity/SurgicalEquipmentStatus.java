package kr.co.seoulit.hisback.surgery.room.entity;

/**
 * 수술장비 상태코드 (EquipmentStatusCode)
 * <p>제거(SL2-11)·출고반입(SL2-12)은 아직 착수 전 스토리라 이 enum은
 * 현재 신규 등록(SL2-10) 시 기본값 지정 용도로만 쓰인다.</p>
 */
public enum SurgicalEquipmentStatus {

    AVAILABLE("사용가능"),
    IN_USE("사용중"),
    MAINTENANCE("점검중"),
    BROKEN("고장");

    private final String label;

    SurgicalEquipmentStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
