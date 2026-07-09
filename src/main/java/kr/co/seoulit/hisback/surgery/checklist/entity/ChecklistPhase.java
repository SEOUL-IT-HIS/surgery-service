package kr.co.seoulit.hisback.surgery.checklist.entity;

/**
 * WHO 수술안전체크리스트 단계
 * <p>Sign In(마취 유도 전) → Time Out(피부 절개 전) → Sign Out(퇴실 전) 순으로 진행한다. (BR-011)</p>
 */
public enum ChecklistPhase {

    SIGN_IN("Sign In"),
    TIME_OUT("Time Out"),
    SIGN_OUT("Sign Out");

    private final String label;

    ChecklistPhase(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /** 직전 단계 (Sign In은 없음) */
    public ChecklistPhase previous() {
        return switch (this) {
            case SIGN_IN -> null;
            case TIME_OUT -> SIGN_IN;
            case SIGN_OUT -> TIME_OUT;
        };
    }
}
