package com.daedan.festabook.festival.service;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.Lineup;
import com.daedan.festabook.festival.dto.LineupRequest;
import com.daedan.festabook.festival.dto.LineupResponse;
import com.daedan.festabook.festival.infrastructure.FestivalJpaRepository;
import com.daedan.festabook.festival.infrastructure.LineupJpaRepository;
import com.daedan.festabook.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LineupService {

    private final LineupJpaRepository lineupJpaRepository;
    private final FestivalJpaRepository festivalJpaRepository;

    public LineupResponse addLineup(Long festivalId, LineupRequest request) {
        Festival festival = getFestivalById(festivalId);

        Lineup lineup = request.toEntity(festival);
        Lineup savedLineup = lineupJpaRepository.save(lineup);

        return LineupResponse.from(savedLineup);
    }

    private Festival getFestivalById(Long festivalId) {
        return festivalJpaRepository.findById(festivalId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 축제입니다.", HttpStatus.NOT_FOUND));
    }
}
