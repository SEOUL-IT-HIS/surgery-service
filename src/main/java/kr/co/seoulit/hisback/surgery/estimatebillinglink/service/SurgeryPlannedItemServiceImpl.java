package kr.co.seoulit.hisback.surgery.estimatebillinglink.service;

import kr.co.seoulit.hisback.surgery.estimatebillinglink.dto.SurgeryPlannedItemDto;
import kr.co.seoulit.hisback.surgery.estimatebillinglink.entity.SurgeryPlannedItem;
import kr.co.seoulit.hisback.surgery.estimatebillinglink.repository.SurgeryPlannedItemRepository;
import kr.co.seoulit.hisback.surgery.global.exception.BusinessException;
import kr.co.seoulit.hisback.surgery.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 수술 예정 자원목록 서비스 구현체 (SL2-65 등록 / SL2-66 조회)
 */
@Service
public class SurgeryPlannedItemServiceImpl implements SurgeryPlannedItemService {

    private final SurgeryPlannedItemRepository surgeryPlannedItemRepository;

    public SurgeryPlannedItemServiceImpl(SurgeryPlannedItemRepository repository) {
        this.surgeryPlannedItemRepository = repository;
    }

    @Override
    public List<SurgeryPlannedItemDto> getPlannedItems(String surgeryId) {
        return surgeryPlannedItemRepository.findBySurgeryId(surgeryId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public SurgeryPlannedItemDto createPlannedItem(SurgeryPlannedItemDto request) {
        // PK 는 내부 식별자라 서버가 UUID 로 채번한다(§14.2)
        String plannedItemId =
                request.getPlannedItemId() != null
                        ? request.getPlannedItemId()
                        : UUID.randomUUID().toString();

        SurgeryPlannedItem item =
                SurgeryPlannedItem.builder()
                        .plannedItemId(plannedItemId)
                        .surgeryId(request.getSurgeryId())
                        .itemTypeCd(request.getItemTypeCd())
                        .itemCode(request.getItemCode())
                        .quantity(request.getQuantity())
                        .build();
        return toDto(surgeryPlannedItemRepository.save(item));
    }

    @Override
    public void deletePlannedItem(String plannedItemId) {
        SurgeryPlannedItem item =
                surgeryPlannedItemRepository
                        .findById(plannedItemId)
                        .orElseThrow(
                                () -> new BusinessException(
                                        ErrorCode.PLANNED_ITEM_NOT_FOUND, plannedItemId));
        surgeryPlannedItemRepository.delete(item);
    }

    // 엔티티 → DTO. 인자 순서는 DTO 선언 순서와 같아야 한다(@AllArgsConstructor).
    private SurgeryPlannedItemDto toDto(SurgeryPlannedItem i) {
        return new SurgeryPlannedItemDto(
                i.getPlannedItemId(),
                i.getSurgeryId(),
                i.getItemTypeCd(),
                i.getItemCode(),
                i.getQuantity(),
                i.getCreatedAt(),
                i.getUpdatedAt());
    }

}
