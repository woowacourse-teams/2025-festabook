package com.daedan.festabook.presentation.news.lost

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import com.daedan.festabook.R
import com.daedan.festabook.presentation.common.getObject
import com.daedan.festabook.presentation.news.lost.component.LostItemModalDialog
import com.daedan.festabook.presentation.news.lost.model.LostUiModel
import timber.log.Timber

class LostItemModalDialogFragment : DialogFragment() {
    private val lostItem: LostUiModel.Item? by lazy {
        arguments?.getObject(KEY_LOST_ITEM)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext(), R.style.LostItemModalDialogTheme)
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View =
        ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                lostItem
                    ?.let {
                        LostItemModalDialog(lostItem = it)
                    } ?: run {
                    Timber.e("${::LostItemModalDialogFragment.name}: 선택한 분실물이 존재하지 않습니다.")
                    dismissAllowingStateLoss()
                }
            }
        }

    override fun onStart() {
        super.onStart()

        val width = resources.getDimensionPixelSize(R.dimen.lost_item_modal_dialog_width)
        val height = resources.getDimensionPixelSize(R.dimen.lost_item_modal_dialog_height)
        dialog?.window?.setLayout(width, height)
    }

    companion object {
        const val TAG_MODAL_DIALOG_LOST_ITEM_FRAGMENT = "lostItemModalDialogFragment"
        const val KEY_LOST_ITEM = "lostItem"

        fun newInstance(lostItem: LostUiModel.Item) =
            LostItemModalDialogFragment().apply {
                arguments =
                    Bundle().apply {
                        putParcelable(KEY_LOST_ITEM, lostItem)
                    }
            }
    }
}
