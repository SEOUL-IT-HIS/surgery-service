package kr.co.seoulit.hisback.surgery.schedule.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 수술 스케줄 요청/응답 DTO (가이드 §11.3)
 *
 * <p>필수 항목 검증(SL2-184/190/198)은 여기서 선언하고 컨트롤러가 {@code @Valid} 로 발동시킨다.
 * 실패는 GlobalExceptionHandler 가 SUR038 로 변환한다(§11.5, §15.1).</p>
 *
 * <p><b>서버가 채우는 필드에는 제약을 걸지 않는다</b> — surgeryId 는 UUID 채번,
 * statusCd·emergencyYn 은 registerSchedule 이 기본값을 넣는다. 여기에 @NotBlank 를 달면
 * 프론트가 굳이 그 값을 보내야 하는 이상한 계약이 된다.</p>
 *
 * <p>배정 필드(roomCode·anesthesiologistId·nurseId)도 비워 둔다. 등록 시점에는 미정이고
 * 배정 API(/room, /anesthesiologist, /nurse)에서 채우기 때문이다.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SurgeryDto {

    private String surgeryId;

    @NotBlank
    private String patientId;

    @NotBlank
    private String surgeonId;

    private String anesthesiologistId;
    private String nurseId;
    private String roomCode;

    @NotNull
    private LocalDate surgeryDt;

    private String statusCd;
    private String progressCd;
    private String cancelReasonCd;
    private String surgTypeCd;
    private String surgeryName;
    private String emergencyYn;
    private LocalDate actualStartDt;
    private LocalDate actualEndDt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
