package com.wafflestudio.snutt2

import android.content.Context
import android.view.LayoutInflater
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.wafflestudio.snutt2.databinding.DialogItemPickerBinding
import com.wafflestudio.snutt2.databinding.DialogTextInputBinding
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import io.reactivex.rxjava3.core.Maybe
import javax.inject.Inject

@ActivityScoped
class DialogController @Inject constructor(@ActivityContext private val context: Context) {

    fun <T> showSelectorDialog(
        @StringRes title: Int,
        items: List<T>,
        defaultValue: Int? = null,
        textMapper: (T) -> String
    ): Maybe<T> {
        return Maybe.create { emitter ->

            val binding = DialogItemPickerBinding.inflate(LayoutInflater.from(context))
            binding.selector.displayedValues = items.map(textMapper).toTypedArray()
            binding.selector.minValue = 0
            binding.selector.maxValue = items.size - 1
            binding.selector.wrapSelectorWheel = false
            defaultValue?.let { binding.selector.value = it }

            AlertDialog.Builder(context).apply {
                setView(binding.root)
                setTitle(title)
                setPositiveButton(context.getString(R.string.common_ok)) { _, _ ->
                    emitter.onSuccess(items[binding.selector.value])
                }
                setNegativeButton(context.getString(R.string.common_cancel), null)
            }
                .setOnCancelListener { emitter.onComplete() }
                .setOnDismissListener { emitter.onComplete() }
                .create()
                .show()
        }
    }


    fun showTextDialog(
        @StringRes title: Int,
        defaultValue: String? = null,
        @StringRes hint: Int? = null,
    ): Maybe<String> {
        return Maybe.create { emitter ->

            val binding = DialogTextInputBinding.inflate(LayoutInflater.from(context))
            defaultValue?.let { binding.input.setText(it) }
            hint?.let { binding.input.setHint(it) }

            AlertDialog.Builder(context).apply {
                setView(binding.root)
                setTitle(title)
                setPositiveButton(context.getString(R.string.common_ok)) { _, _ ->
                    emitter.onSuccess(binding.input.text.toString())
                }
                setNegativeButton(context.getString(R.string.common_cancel), null)
            }
                .setOnCancelListener { emitter.onComplete() }
                .setOnDismissListener { emitter.onComplete() }
                .create()
                .show()
        }
    }
}
