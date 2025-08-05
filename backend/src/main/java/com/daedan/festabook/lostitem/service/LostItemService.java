package com.daedan.festabook.lostitem.service;

import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.lostitem.controller.LostItemRequest;
import com.daedan.festabook.lostitem.controller.LostItemResponse;
import com.daedan.festabook.lostitem.controller.LostItemResponses;
import com.daedan.festabook.lostitem.domain.LostItem;
import com.daedan.festabook.lostitem.infrastructure.LostItemJpaRepository;
import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.infrastructure.OrganizationJpaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LostItemService {

    private final LostItemJpaRepository lostItemJpaRepository;
    private final OrganizationJpaRepository organizationJpaRepository;

    public LostItemResponse createLostItem(Long organizationId, LostItemRequest request) {
        Organization organization = getOrganizationById(organizationId);

        LostItem newLostItem = request.toLostItem(organization);
        lostItemJpaRepository.save(newLostItem);

        return LostItemResponse.from(newLostItem);
    }

    public LostItemResponses getAllLostItemByOrganizationId(Long organizationId) {
        List<LostItem> lostItems = lostItemJpaRepository.findAllByOrganizationId(organizationId);
        return LostItemResponses.from(lostItems);
    }

    private Organization getOrganizationById(Long organizationId) {
        return organizationJpaRepository.findById(organizationId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 조직입니다.", HttpStatus.BAD_REQUEST));
    }
}
