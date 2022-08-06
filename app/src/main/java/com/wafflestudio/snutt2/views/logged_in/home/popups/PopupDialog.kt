package com.wafflestudio.snutt2.views.logged_in.home.popups

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.databinding.DialogPopupBinding

class PopupDialog(
    context: Context,
    private val onClickHideFewDays: () -> Unit,
    private val url: String
) : Dialog(context, R.style.popup_dialog) {

    lateinit var binding: DialogPopupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DialogPopupBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )

        binding.popupCloseButton.setOnClickListener {
            dismiss()
        }

        binding.popupHideFewDaysButton.setOnClickListener {
            onClickHideFewDays.invoke()
            dismiss()
        }

        val defaultImage = R.drawable.img_reviews_coming_soon

        // ApplicationContext 에 묶이지만 1회성 요청
        Glide.with(context.applicationContext)
            .load(url)
            .placeholder(R.color.white)
            .error(defaultImage)
            .fallback(defaultImage)
            .into(binding.popupImage)
    }
}
