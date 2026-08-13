package kr.co.seoulit.hisback.surgery.proceduremaster.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 수술항목 마스터 DTO
 *
 * <p>엔티티(SurgeryProcedure)와 필드명을 1:1로 맞춘다. 이름이 다르면 변환할 때 손으로
 * 맞춰야 하고 JSON 키도 어긋나 프론트에서 undefined 가 된다.</p>
 *
 * <p><b>Lombok 이 필요한 이유</b> — 필드만 선언하면 게터가 없어 두 가지가 깨진다.
 * 서비스에서 {@code getProcedureCd()} 를 못 부르고, Jackson 이 직렬화할 값을 찾지 못해
 * 응답이 빈 객체 {@code {}} 로 나간다. 다른 DTO 들도 모두 같은 세 개를 붙인다.</p>
 * <ul>
 *   <li>{@code @Data} — 게터·세터·toString·equals</li>
 *   <li>{@code @NoArgsConstructor} — Jackson 이 역직렬화할 때 쓴다</li>
 *   <li>{@code @AllArgsConstructor} — 엔티티→DTO 변환을 한 줄로 쓰기 위해</li>
 * </ul>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SurgeryProcedureDto {

    /** 수술항목 코드 — 사용자가 지정하는 마스터 코드다(서버 채번 아님) */
    private String procedureCd;

    private String procedureName;

    /** 사용 여부 'Y'/'N' — §14.2 `_yn` = CHAR(1) */
    private String activeYn;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
