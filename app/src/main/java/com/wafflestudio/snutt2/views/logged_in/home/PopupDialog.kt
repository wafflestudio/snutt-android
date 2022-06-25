package com.wafflestudio.snutt2.views.logged_in.home

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.databinding.DialogPopupBinding
import com.wafflestudio.snutt2.lib.data.SNUTTStringUtils.getPopupCloseMessage

class PopupDialog(
    context: Context,
    hideFewDays: () -> Unit,
    days: Int?,
    url: String
) : Dialog(context, R.style.popup_dialog) {

    // 왜 constructor 에서 바로 가져다 쓰지 못하는가?
    private val clickFewDaysButton = hideFewDays
    private val day = days

    lateinit var binding: DialogPopupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DialogPopupBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)

        binding.popupHideFewDaysButton.text = getPopupCloseMessage(day)

        binding.popupCloseButton.setOnClickListener {
            dismiss()
        }

        binding.popupHideFewDaysButton.setOnClickListener {
            clickFewDaysButton.invoke()
            dismiss()
        }

        // TODO : 이미지 가져오기
    }
}
