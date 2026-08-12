package kr.co.seoulit.hisback.surgery.monitoring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 수술실별 진행 상태·공실 여부 DTO (SL2-287)
 *
 * <p>수술실 한 칸의 "지금 어떤가"를 담는다. {@link SurgeryStatusDto} 가 하루 전체를
 * 숫자로 요약한다면, 이쪽은 방 단위로 쪼갠 것이다. 대시보드에서 방마다 카드를
 * 그리는 데 쓴다.</p>
 *
 * <p><b>조회 전용이다.</b> 수술실 정보를 고치는 것은 room 패키지 소관이고,
 * 여기는 읽어서 합쳐 보여주기만 한다.</p>
 *
 * <h3>왜 수술실 이름은 담고 집도의 이름은 안 담는가</h3>
 * <p>수술실은 수술 서비스가 소유한 데이터라 그대로 실어도 된다. 집도의·환자는
 * 병원관리·환자 서비스 소유라 식별자만 넘기고 이름은 화면이 그쪽에서 조회한다
 * (§14.1 스냅샷 금지 / §21.9).</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperatingRoomStatusDto {

    /** 수술실 코드 (SURGERY_ROOM.room_code) */
    private String roomCode;

    /** 수술실 이름 — 우리 소유 데이터라 그대로 싣는다 */
    private String roomName;

    /** 수술실 자체 상태 (OR_STATUS_CD: 01사용가능 02사용중 03점검중 04폐쇄) */
    private String statusCd;

    /** 턴오버 상태 (OR_TURNOVER_CD) */
    private String turnoverCd;

    /**
     * 지금 이 방에서 수술이 진행중인가.
     *
     * <p>수술실의 {@code statusCd} 가 아니라 <b>수술 데이터</b>를 보고 판단한다.
     * 둘이 어긋날 수 있기 때문이다 — 수술이 끝났는데 방 상태를 '사용중'으로
     * 되돌려 놓지 않은 경우가 실제로 생긴다. 진행중 수술의 존재가 더 믿을 만하다.</p>
     */
    private boolean inUse;

    /**
     * 지금 비어서 바로 쓸 수 있는가.
     *
     * <p>{@code !inUse} 와 다르다 — 점검중(03)·폐쇄(04)인 방은 수술이 없어도
     * 쓸 수 없다. "수술이 없다"와 "쓸 수 있다"를 한 필드로 뭉치면 화면이
     * 점검중인 방을 배정 후보로 내놓게 된다.</p>
     */
    private boolean available;

    /**
     * 진행중인 수술의 식별자. 없으면 null.
     *
     * <p>진행중 수술이 둘 이상이면 <b>가장 먼저 시작한 것</b>을 넣는다. 정상이라면
     * 한 방에 한 건이지만, 종료 처리를 빠뜨리면 겹칠 수 있어 규칙을 정해 둔다.</p>
     */
    private String currentSurgeryId;

    /** 진행중인 수술명. 수술 서비스 소유라 그대로 싣는다. 없으면 null. */
    private String currentSurgeryName;

    /**
     * 그 날 이 방에 잡힌 수술 건수 (취소 제외).
     *
     * <p>취소를 빼는 이유 — 이 숫자는 "오늘 이 방이 얼마나 바쁜가"를 보는 값이라,
     * 실제로 쓰이지 않을 취소 건까지 세면 판단이 흐려진다. 날짜 전체 통계에서
     * 취소를 포함하는 {@link SurgeryStatusDto#getTotalCount()} 와 기준이 다르다.</p>
     */
    private long scheduledCount;
}
