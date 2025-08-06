package com.daedan.festabook.lostitem.service;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.infrastructure.FestivalJpaRepository;
import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.lostitem.domain.LostItem;
import com.daedan.festabook.lostitem.dto.LostItemRequest;
import com.daedan.festabook.lostitem.dto.LostItemResponse;
import com.daedan.festabook.lostitem.dto.LostItemResponses;
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

    @Transactional
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

    private Festival getFestivalById(Long festivalId) {
        return festivalJpaRepository.findById(festivalId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 조직입니다.", HttpStatus.BAD_REQUEST));
    }
}
