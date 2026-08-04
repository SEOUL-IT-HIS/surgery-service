package kr.co.seoulit.hisback.surgery.anesthesia.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 활력징후 추가 요청 DTO (SL2-206 필수 항목 검증)
 *
 * <p><b>AnesthesiaRecordDto 를 재사용하지 않는 이유</b> — 활력징후는 이 API 에서만 필수다.
 * 마취기록 생성(POST /{surgeryId}/anesthesia-records)은 마취방법·ASA등급만 받고 활력징후는
 * 비어 있는 것이 정상이라, 공용 DTO 에 {@code @NotBlank} 를 달면 생성이 깨진다.</p>
 *
 * <p>기존 {@code Map<String, String>} 을 대체한다. Map 에는 {@code @Valid} 를 걸 수 없어
 * 빈 값이나 키 누락이 그대로 서비스까지 내려갔다. JSON 형태는 동일하므로 프론트 계약은
 * 바뀌지 않는다 — {@code { "vitalSignsLog": "BP 120/80" }}</p>
 */
public record AppendVitalSignsRequest(@NotBlank String vitalSignsLog) {
}
