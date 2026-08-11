package kr.co.seoulit.hisback.surgery.room.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 수술실 응답 DTO (가이드 §11.3: ApiResponse.data 로 감싸 응답)
 * <p>프론트 OperatingRoom 타입(hisfrontend/src/features/surgery/types.ts)과 필드명을
 * 그대로 맞췄다(camelCase).</p>
 */
// ─────────────────────────────────────────────────────────────────────────────
// 이 파일은 DTO 계층의 '대표 예제'다. 어노테이션마다 설명을 달아두었으니
// 다른 DTO 는 같은 모양으로 읽으면 된다.
//
// DTO 를 따로 두는 이유 — 엔티티를 그대로 응답에 쓰면 DB 구조가 API 계약에 그대로
// 새어 나간다. 컬럼을 하나 바꾸면 프론트가 깨진다. 한 겹 끊어두면 테이블이 바뀌어도
// 변환 메서드(toDto)만 고치면 된다.
// ─────────────────────────────────────────────────────────────────────────────

// @Data — Lombok 묶음 어노테이션. 아래를 한 번에 만들어준다.
//   @Getter / @Setter / @ToString / @EqualsAndHashCode / @RequiredArgsConstructor
//   요청 본문을 받을 때 Jackson 이 setter 로 값을 채우므로 @Setter 가 꼭 필요하다.
//   (엔티티에는 @Data 를 쓰지 않는다 — @EqualsAndHashCode 가 연관 관계를 타고
//    무한 순회하거나 지연 로딩을 강제로 깨우는 문제가 있어서다. 그래서 엔티티는
//    @Getter/@Setter 를 따로 붙인다.)
@Data
// @NoArgsConstructor — Jackson 이 JSON → 객체 변환 시 빈 객체를 먼저 만든다. 없으면 실패한다.
@NoArgsConstructor
// @AllArgsConstructor — 모든 필드를 받는 생성자.
//   ServiceImpl 의 toDto() 가 new OperatingRoomDto(a, b, c, ...) 형태로 쓴다.
//   순서가 곧 필드 선언 순서이므로, 필드를 중간에 끼워 넣으면 값이 밀린다. 주의할 것.
@AllArgsConstructor
public class OperatingRoomDto {
    // 필드명은 프론트 types.ts 의 SurgeryRoom 과 한 글자도 다르면 안 된다.
    // 다르면 JSON 키가 안 맞아 화면에 undefined 가 뜨는데, TypeScript 는 실행 중
    // 검사를 하지 않아 조용히 넘어간다 — 빈칸을 보고서야 알게 된다.
    // DB 컬럼은 lower_snake(room_code), API 는 camelCase(roomCode) 다(§13).
    // roomCode 에 @NotBlank 를 걸지 않는 이유 — 수정(PUT /rooms/{roomCode})은 대상을
    // 경로에서 받고 본문의 roomCode 는 쓰지 않는다(updateOperatingRoom 은 roomName 만 반영).
    // 필수로 걸면 이름만 바꾸려는 요청이 400 으로 막힌다. ConsentDto 가 surgeryId 에
    // 제약을 걸지 않는 것과 같은 이유다.
    //
    // 그래서 등록 시 roomCode 누락은 이 DTO 가 아니라 서비스에서 걸러야 한다.
    private String roomCode;

    // @NotBlank — null 도, 빈 문자열도, 공백만 있는 것도 막는다(@NotNull 보다 강하다).
    // 등록·수정 모두 이름은 반드시 있어야 하므로 여기에만 건다.
    // 위반하면 MethodArgumentNotValidException 이 나고 GlobalExceptionHandler 가 400(SUR038)으로 바꾼다.
    @NotBlank
    private String roomName;

    // 상태·턴오버 코드는 등록 시 서버가 기본값을 넣고, 변경은 전용 PATCH 로만 한다.
    // 본문으로 받을 일이 없어 제약을 걸지 않는다.
    private String statusCd;
    private String turnoverCd;

    // 엔티티의 TIMESTAMP 가 그대로 넘어와 JSON 에서는 ISO 문자열이 된다(§14.2 `_at`).
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
