package com.daedan.festabook.domain.repository

import com.daedan.festabook.domain.model.LostItem

interface LostItemRepository {
    fun getAllLostItems(): List<LostItem>
}
