package com.daedan.festabook.data.repository

import com.daedan.festabook.domain.model.LostItem
import com.daedan.festabook.domain.repository.LostItemRepository

class LostItemRepositoryImpl : LostItemRepository {
    override fun getAllLostItems(): List<LostItem> =
        listOf(
            LostItem(imageId = 1L, imageUrl = "https://picsum.photos/200/300"),
            LostItem(imageId = 2L, imageUrl = "https://picsum.photos/200/300"),
            LostItem(imageId = 3L, imageUrl = "https://picsum.photos/200/300"),
            LostItem(imageId = 4L, imageUrl = "https://picsum.photos/200/300"),
            LostItem(imageId = 5L, imageUrl = "https://picsum.photos/200/300"),
            LostItem(imageId = 6L, imageUrl = "https://picsum.photos/200/300"),
            LostItem(imageId = 7L, imageUrl = "https://picsum.photos/200/300"),
            LostItem(imageId = 8L, imageUrl = "https://picsum.photos/200/300"),
            LostItem(imageId = 9L, imageUrl = "https://picsum.photos/200/300"),
            LostItem(imageId = 10L, imageUrl = "https://picsum.photos/200/300"),
        )
}
