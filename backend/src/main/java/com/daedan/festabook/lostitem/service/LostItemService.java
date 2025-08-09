package com.daedan.festabook.lostitem.service;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.infrastructure.FestivalJpaRepository;
import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.lostitem.controller.LostItemStatusRequest;
import com.daedan.festabook.lostitem.controller.LostItemStatusUpdateResponse;
import com.daedan.festabook.lostitem.domain.LostItem;
import com.daedan.festabook.lostitem.dto.LostItemRequest;
import com.daedan.festabook.lostitem.dto.LostItemResponse;
import com.daedan.festabook.lostitem.dto.LostItemResponses;
import com.daedan.festabook.lostitem.dto.LostItemUpdateResponse;
import com.daedan.festabook.lostitem.infrastructure.LostItemJpaRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LostItemService {

    private final LostItemJpaRepository lostItemJpaRepository;
    private final FestivalJpaRepository festivalJpaRepository;

    public LostItemResponse createLostItem(Long festivalId, LostItemRequest request) {
        Festival festival = getFestivalById(festivalId);

        LostItem lostItem = request.toLostItem(festival);
        lostItemJpaRepository.save(lostItem);

        return LostItemResponse.from(lostItem);
    }

    public LostItemResponses getAllLostItemByFestivalId(Long festivalId) {
        List<LostItem> lostItems = lostItemJpaRepository.findAllByFestivalId(festivalId);
        return LostItemResponses.from(lostItems);
    }

    @Transactional
    public LostItemUpdateResponse updateLostItem(Long lostItemId, LostItemRequest request) {
        LostItem lostItem = getLostItemById(lostItemId);
        lostItem.updateLostItem(request.imageUrl(), request.storageLocation());
        return LostItemUpdateResponse.from(lostItem);
    }

    @Transactional
    public LostItemStatusUpdateResponse updateLostItemStatus(Long lostItemId, LostItemStatusRequest request) {
        LostItem lostItem = getLostItemById(lostItemId);
        lostItem.updateLostItem(request.status());
        return LostItemStatusUpdateResponse.from(lostItem);
    }

    public void deleteLostItemByLostItemId(Long lostItemId) {
        lostItemJpaRepository.deleteById(lostItemId);
    }

    private LostItem getLostItemById(Long lostItemId) {
        return lostItemJpaRepository.findById(lostItemId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 분실물입니다.", HttpStatus.BAD_REQUEST));
    }

    private Festival getFestivalById(Long festivalId) {
        return festivalJpaRepository.findById(festivalId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 축제입니다.", HttpStatus.BAD_REQUEST));
    }
}
