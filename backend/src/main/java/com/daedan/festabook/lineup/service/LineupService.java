package com.daedan.festabook.lineup.service;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.infrastructure.FestivalJpaRepository;
import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.lineup.domain.Lineup;
import com.daedan.festabook.lineup.dto.LineupRequest;
import com.daedan.festabook.lineup.dto.LineupResponse;
import com.daedan.festabook.lineup.dto.LineupResponses;
import com.daedan.festabook.lineup.infrastructure.LineupJpaRepository;
import java.util.Collections;
import java.util.List;
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

    public LineupResponses getAllLineupByFestivalId(Long festivalId) {
        List<Lineup> lineups = lineupJpaRepository.findAllByFestivalId(festivalId);
        Collections.sort(lineups);
        return LineupResponses.from(lineups);
    }

    private Festival getFestivalById(Long festivalId) {
        return festivalJpaRepository.findById(festivalId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 축제입니다.", HttpStatus.NOT_FOUND));
    }
}
