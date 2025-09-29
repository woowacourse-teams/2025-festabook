package com.daedan.festabook.presentation.placeDetail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daedan.festabook.databinding.ItemPlaceImageBinding
import com.daedan.festabook.presentation.common.loadImage
import com.daedan.festabook.presentation.placeDetail.model.ImageUiModel
import io.getstream.photoview.dialog.PhotoViewDialog

class PlaceImageViewPagerViewHolder(
    private val binding: ItemPlaceImageBinding,
    private val images: List<ImageUiModel>
) : RecyclerView.ViewHolder(binding.root) {
    private val imageDialogBuilder = PhotoViewDialog.Builder(
        context = binding.root.context,
        images = images.map { it.url }
    ) { imageView, url ->
        imageView.loadImage(url)
    }

    init {
        binding.ivPlaceImage.setOnClickListener {
            imageDialogBuilder
                .withHiddenStatusBar(false)
                .withStartPosition(bindingAdapterPosition)
                .build()
                .show()
        }
    }

    fun bind(imageUiModel: ImageUiModel) {
        binding.image = imageUiModel
    }

    companion object {
        fun from(parent: ViewGroup, images: List<ImageUiModel>): PlaceImageViewPagerViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemPlaceImageBinding.inflate(layoutInflater, parent, false)
            return PlaceImageViewPagerViewHolder(binding, images)
        }
    }
}
