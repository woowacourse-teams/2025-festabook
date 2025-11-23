package com.daedan.festabook.presentation.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.CachePolicy
import coil3.request.crossfade
import coil3.request.transformations
import coil3.transform.RoundedCornersTransformation
import com.daedan.festabook.R
import com.daedan.festabook.databinding.ItemHomePosterBinding
import com.daedan.festabook.logging.logger
import com.daedan.festabook.logging.model.PosterTouchLogData
import com.daedan.festabook.presentation.common.convertImageUrl
import com.daedan.festabook.presentation.common.loadImage
import io.getstream.photoview.dialog.PhotoViewDialog

class PosterItemViewHolder(
    private val binding: ItemHomePosterBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(url: String) {
        val imageDialog = PhotoViewDialog.Builder(
            context = binding.root.context,
            images = listOf(url),
        ) { imageView, url ->
            imageView.load(url.convertImageUrl()) {
                crossfade(true)
            }
        }
            .withHiddenStatusBar(false)
            .withTransitionFrom(binding.ivHomePoster)
            .build()

        binding.ivHomePoster.loadImage(url) {
            transformations(RoundedCornersTransformation(20f))
            memoryCachePolicy(CachePolicy.ENABLED)
        }
        binding.motionLayout.progress = 0f
        binding.motionLayout.transitionToState(R.id.collapsed)
        binding.root.setOnClickListener {
            binding.logger.log(
                PosterTouchLogData(
                    baseLogData = binding.logger.getBaseLogData(),
                    url = url,
                ),
            )
        }
        binding.ivHomePoster.setOnClickListener {
            imageDialog.show()
        }
    }

    fun transitionToExpanded() {
        binding.motionLayout.transitionToState(R.id.expanded)
    }

    fun transitionToCollapsed() {
        binding.motionLayout.transitionToState(R.id.collapsed)
    }

    companion object {
        fun from(parent: ViewGroup): PosterItemViewHolder {
            val binding =
                ItemHomePosterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return PosterItemViewHolder(binding)
        }
    }
}
