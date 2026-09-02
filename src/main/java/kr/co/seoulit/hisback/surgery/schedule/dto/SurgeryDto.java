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
 * statusCd·emergencyYn 은 {@code createScheduledSurgery} 가 정한다(상태는 예약 01,
 * 응급 여부는 오더가 정한 값을 옮겨 받는다). 여기에 @NotBlank 를 달면 프론트가 굳이
 * 그 값을 보내야 하는 이상한 계약이 된다.</p>
 *
 * <p><b>이 DTO 로 수술을 새로 만드는 외부 경로는 없다</b> — 수술은 오더가
 * 수락(배정)될 때만 만들어진다. 요청 접수용 DTO 는 surgeryorder 패키지의
 * {@code CreateSurgeryOrderRequest} 다. 여기 남은 검증은 수정(PUT)에서 쓰인다.</p>
 *
 * <p><b>배정 필드(roomCode·anesthesiologistId·nurseId)에 제약이 없는 것은 여기가
 * 응답 겸용 DTO 이기 때문이지, 비워도 된다는 뜻이 아니다.</b> 배정은 오더 수락 시점에
 * 한 번에 확정되며, 그 필수 여부는 {@code AssignSurgeryOrderRequest} 가 선언한다.
 * 배정이 끝난 뒤에는 개별 배정 API 가 전부 400 으로 거절하므로 이 필드들은
 * 사실상 읽기 전용이다.</p>
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
    private String surgeryTypeCd;
    private String surgeryName;
    private String emergencyYn;

    /** 마취 시행 여부(Y/N). 배정할 때 정해지고, 이후 수술 화면은 읽기만 한다 */
    private String anesthesiaYn;

    private LocalDate actualStartDt;
    private LocalDate actualEndDt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
