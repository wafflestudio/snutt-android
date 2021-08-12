package com.wafflestudio.snutt2.views.logged_in.home.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.wafflestudio.snutt2.data.UserRepository
import com.wafflestudio.snutt2.databinding.FragmentReportBinding
import com.wafflestudio.snutt2.handler.ApiOnError
import com.wafflestudio.snutt2.lib.base.BaseFragment
import com.wafflestudio.snutt2.lib.rx.throttledClicks
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ReportFragment : BaseFragment() {

    private lateinit var binding: FragmentReportBinding

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var apiOnError: ApiOnError

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.throttledClicks()
            .bindUi(this) {
                findNavController().popBackStack()
            }

        binding.buttonSend.throttledClicks()
            .bindUi(this) {
                if (binding.detailEditText.text.toString().isEmpty()) {
                    Toast.makeText(requireContext(), "내용을 입력해 주세요", Toast.LENGTH_SHORT).show()
                } else {
                    hideSoftKeyboard()
                    val email = binding.emailEditText.text.toString()
                    val detail = binding.detailEditText.text.toString()
                    userRepository.postFeedback(email, detail)
                        .bindUi(
                            this,
                            onSuccess = {
                                Toast.makeText(requireContext(), "전송하였습니다", Toast.LENGTH_SHORT)
                                    .show()
                                findNavController().popBackStack()
                            },
                            onError = {
                                apiOnError(it)
                            }
                        )
                }
            }
    }

    private fun hideSoftKeyboard() {
        (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
            requireView().windowToken,
            0
        )
    }
}
