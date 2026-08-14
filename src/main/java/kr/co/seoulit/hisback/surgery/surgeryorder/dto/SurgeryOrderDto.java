package kr.co.seoulit.hisback.surgery.surgeryorder.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 수술 오더 응답 DTO
 *
 * <p>엔티티와 필드명을 1:1로 맞춘다. 이름이 다르면 변환할 때 손으로 맞춰야 하고
 * JSON 키도 어긋나 프론트에서 undefined 가 된다.</p>
 *
 * <p><b>요청용 DTO 를 따로 둔 이유</b>({@link CreateSurgeryOrderRequest}) — 응답에는
 * 서버가 정하는 값(오더 식별자·상태·응급여부·연결된 수술)이 들어가는데, 요청에서
 * 그것들을 받으면 클라이언트가 상태를 조작할 수 있다. 한 DTO 를 양쪽에 쓰면
 * "이 필드는 보내도 무시된다"는 설명을 주석으로만 유지하게 된다.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SurgeryOrderDto {

    private String orderId;

    private String patientId;

    /** 내원 식별자. 청구 연동(SL2-72)에 쓰인다 */
    private String visitId;

    private String surgeonId;

    /** 희망 수술일 (yyyy-MM-dd). 확정일이 아니다 */
    private LocalDate requestedDt;

    /** 'Y'/'N' — 요청 경로가 정한다 */
    private String emergencyYn;

    /** OrderStatus: 00접수 / 01수락 / 02반려 */
    private String orderStatusCd;

    /** 반려일 때만 값이 있다 */
    private String rejectReasonCd;

    private String surgeryTypeCd;

    private String surgeryName;

    /** 요청자 식별자. 진료·응급이 보내지 않으면 null */
    private String orderedBy;

    /** 수락 시 만들어진 수술의 식별자. 접수·반려 상태면 null */
    private String surgeryId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
