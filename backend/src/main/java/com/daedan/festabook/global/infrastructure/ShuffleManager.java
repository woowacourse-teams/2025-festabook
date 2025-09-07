package com.daedan.festabook.global.infrastructure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ShuffleManager {

    public <T> List<T> getShuffledList(List<T> originalList) {
        if (originalList == null || originalList.isEmpty()) {
            return Collections.emptyList();
        }

        List<T> shuffledList = new ArrayList<>(originalList);
        Collections.shuffle(shuffledList);

        return shuffledList;
    }
}
