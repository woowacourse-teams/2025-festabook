package com.daedan.festabook.presentation.placeMap

import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import kotlinx.coroutines.suspendCancellableCoroutine

suspend fun MapFragment.getMap() =
    suspendCancellableCoroutine<NaverMap> { cont ->
        getMapAsync { map ->
            cont.resumeWith(
                Result.success(map),
            )
        }
    }
