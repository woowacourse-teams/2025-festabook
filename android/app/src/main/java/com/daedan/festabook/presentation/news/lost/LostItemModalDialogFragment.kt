package com.daedan.festabook.presentation.news.lost

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentLostItemModalDialogBinding
import com.daedan.festabook.presentation.common.getObject
import com.daedan.festabook.presentation.common.loadImage
import com.daedan.festabook.presentation.news.lost.model.LostItemUiModel
import timber.log.Timber

class LostItemModalDialogFragment : DialogFragment() {
    private val lostItem: LostItemUiModel? by lazy {
        arguments?.getObject(KEY_LOST_ITEM)
    }

    private lateinit var binding: FragmentLostItemModalDialogBinding

    @Suppress("ktlint:standard:backing-property-naming")
    private var _binding: FragmentLostItemModalDialogBinding? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext(), R.style.LostItemModalDialogTheme)
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_lost_item_modal_dialog,
                container,
                false,
            )
        binding = _binding!!

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        lostItem?.let {
            binding.ivModalLostItemImage.loadImage(it.imageUrl)
            binding.tvModalLostItemStorageLocation.text =
                binding.tvModalLostItemStorageLocation.context.getString(
                    R.string.modal_lost_item,
                    it.storageLocation,
                )
        } ?: run {
            Timber.e("${::LostItemModalDialogFragment.name}: 선택한 분실물이 존재하지 않습니다.")
            dismissAllowingStateLoss()
        }
    }

    override fun onStart() {
        super.onStart()

        val width = resources.getDimensionPixelSize(R.dimen.lost_item_modal_dialog_width)
        val height = resources.getDimensionPixelSize(R.dimen.lost_item_modal_dialog_height)
        dialog?.window?.setLayout(width, height)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG_MODAL_DIALOG_LOST_ITEM_FRAGMENT = "lostItemModalDialogFragment"
        const val KEY_LOST_ITEM = "lostItem"

        fun newInstance(lostItem: LostItemUiModel) =
            LostItemModalDialogFragment().apply {
                arguments =
                    Bundle().apply {
                        putParcelable(KEY_LOST_ITEM, lostItem)
                    }
            }
    }
}
