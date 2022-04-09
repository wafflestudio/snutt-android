package com.wafflestudio.snutt2.views.logged_in.home.settings

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.data.UserRepository
import com.wafflestudio.snutt2.databinding.FragmentUserSettingsBinding
import com.wafflestudio.snutt2.lib.network.ApiOnError
import com.wafflestudio.snutt2.lib.base.BaseFragment
import com.wafflestudio.snutt2.lib.network.dto.core.UserDto
import com.wafflestudio.snutt2.lib.rx.throttledClicks
import com.wafflestudio.snutt2.model.SettingsItem
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by makesource on 2017. 1. 24..
 */
@Suppress("DEPRECATION")
@AndroidEntryPoint
class UserSettingsFragment : BaseFragment() {

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var apiOnError: ApiOnError

    private lateinit var binding: FragmentUserSettingsBinding

    private var lists: MutableList<SettingsItem> = mutableListOf()
    private lateinit var adapter: UserSettingsAdapter
    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        registerFacebookCallback()

        adapter = UserSettingsAdapter(lists)
        adapter.setOnItemClickListener(
            object : UserSettingsAdapter.ClickListener {
                override fun onClick(v: View?, position: Int) {
                    when (lists[position].type) {
                        SettingsItem.Type.ChangePassword -> performChangePassword()
                        SettingsItem.Type.ChangeEmail -> performChangeEmail()
                        SettingsItem.Type.AddIdPassword -> performAddIdPassword()
                        SettingsItem.Type.LinkFacebook ->
                            LoginManager.getInstance()
                                .logInWithReadPermissions(
                                    this@UserSettingsFragment,
                                    callbackManager,
                                    emptyList()
                                )
                        SettingsItem.Type.DeleteFacebook -> performDeleteFacebook()
                        SettingsItem.Type.Leave -> performLeave()
                        else -> {
                        }
                    }
                }
            }
        )
        userRepository.user
            .firstElement()
            .bindUi(
                this,
                onSuccess = {
                    addSettingsList(it)
                    adapter.notifyDataSetChanged()
                },
                onError = {
                    apiOnError(it)
                }
            )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserSettingsBinding.inflate(inflater, container, false)

        val recyclerView = binding.accountRecyclerView
        // recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.throttledClicks()
            .bindUi(this) {
                findNavController().popBackStack()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun performChangePassword() {
        val layout =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_change_password, null)
        val alert = AlertDialog.Builder(requireContext())
        alert.setTitle("비밀번호 변경")
        alert.setView(layout)
        alert.setPositiveButton("변경") { _, _ ->
            // do nothing in here. because we override this button listener later
        }.setNegativeButton(
            "취소"
        ) { dialog, _ -> dialog.cancel() }
        val dialog = alert.create()
        dialog.show()
        // change default button handler after dialog show.
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val oldPassword =
                (layout.findViewById<View>(R.id.now_password) as EditText).text.toString()
            val newPassword =
                (layout.findViewById<View>(R.id.new_password) as EditText).text.toString()
            val newPasswordConfirm =
                (layout.findViewById<View>(R.id.new_password_confirm) as EditText).text.toString()
            if (newPassword != newPasswordConfirm) {
                Toast.makeText(context, "새 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
            } else {
                userRepository.putUserPassword(oldPassword, newPassword)
                    .bindUi(
                        this,
                        onSuccess = {
                            Toast.makeText(
                                context,
                                "비밀번호를 변경하였습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                            dialog.dismiss()
                        },
                        onError = {
                            apiOnError(it)
                        }
                    )
            }
        }
    }

    private fun performChangeEmail() {
        val inflater =
            requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout2 = inflater.inflate(R.layout.dialog_change_email, null)
        val alert2 = AlertDialog.Builder(requireContext())
        alert2.setTitle("이메일 변경")
        alert2.setView(layout2)
        alert2.setPositiveButton("변경") { _, _ ->
            // do nothing in here. because we override this button listener later
        }.setNegativeButton(
            "취소"
        ) { dialog, _ -> dialog.cancel() }
        val dialog2 = alert2.create()
        dialog2.show()
        dialog2.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val email = (layout2.findViewById<View>(R.id.email) as EditText).text.toString()
            if (email.isEmpty().not()) {
                userRepository.putUserInfo(email)
                    .bindUi(
                        this,
                        onSuccess = {
                            emailItem!!.detail = email
                            adapter.notifyDataSetChanged()
                        },
                        onError = {
                            apiOnError(it)
                        }
                    )
                dialog2.dismiss()
            } else {
                Toast.makeText(context, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun performAddIdPassword() {
        val layout = layoutInflater.inflate(R.layout.dialog_add_id, null)
        val alert = AlertDialog.Builder(requireContext())
        alert.setTitle("아이디 비빌번호 추가")
        alert.setView(layout)
        alert.setPositiveButton("추가") { _, _ ->
            // do nothing in here. because we override this button listener later
        }.setNegativeButton(
            "취소"
        ) { dialog, _ -> dialog.cancel() }
        val dialog = alert.create()
        dialog.show()
        // change default button handler after dialog show.
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val id = (layout.findViewById<View>(R.id.id) as EditText).text.toString()
            val password = (layout.findViewById<View>(R.id.password) as EditText).text.toString()
            val passwordConfirm =
                (layout.findViewById<View>(R.id.password_confirm) as EditText).text.toString()
            if (password != passwordConfirm) {
                Toast.makeText(context, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show()
            } else {
                userRepository.postUserPassword(id, password)
                    .bindUi(
                        this,
                        onSuccess = {
                            Toast.makeText(context, "아이디를 추가하였습니다", Toast.LENGTH_SHORT).show()
                            updateNewId(id)
                            dialog.dismiss()
                        },
                        onError = {
                            apiOnError(it)
                        }
                    )
            }
        }
    }

    private fun performDeleteFacebook() {
        val alert = AlertDialog.Builder(requireContext())
        alert.setTitle("페이스북 연동 끊기")
        alert.setMessage("페이스북 연동을 끊겠습니까?")
        alert.setPositiveButton("끊기") { _, _ ->
            userRepository.deleteUserFacebook()
                .bindUi(
                    this,
                    onSuccess = {
                        Toast.makeText(context, "페이스북 연동이 끊어졌습니다", Toast.LENGTH_SHORT).show()
                        LoginManager.getInstance().logOut()
                        updateDeleteFacebook()
                    },
                    onError = {
                        apiOnError(it)
                    }
                )
        }.setNegativeButton("취소") { dialog, _ -> dialog.cancel() }
        val dialog = alert.create()
        dialog.show()
    }

    private fun performLeave() {
        val alert = AlertDialog.Builder(requireContext())
        alert.setTitle("회원탈퇴")
        alert.setMessage("SNUTT 회원 탈퇴를 하겠습니까?")
        alert.setPositiveButton("회원탈퇴") { _, _ ->
            val progressDialog = ProgressDialog.show(context, "회원탈퇴", "잠시만 기다려 주세요", true, false)
            userRepository.deleteFirebaseToken()
                .flatMap {
                    userRepository.deleteUserAccount()
                }
                .bindUi(
                    this,
                    onSuccess = {
                        userRepository.performLogout()
                        progressDialog.dismiss()
                    },
                    onError = {
                        Toast.makeText(context, "회원탈퇴에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                        progressDialog.dismiss()
                    }
                )
        }.setNegativeButton("취소") { dialog, _ -> dialog.cancel() }
        val dialog = alert.create()
        dialog.show()
    }

    private fun updateNewId(id: String) {
        var position = -1
        for (i in lists.indices) {
            if (lists[i].type === SettingsItem.Type.AddIdPassword) position = i
        }
        if (position == -1) {
            Timber.e("Add id & password item not exists!!")
            return
        }
        lists.removeAt(position)
        adapter.notifyItemRemoved(position)
        lists.add(position, SettingsItem("비밀번호 변경", SettingsItem.Type.ChangePassword))
        adapter.notifyItemInserted(position)
        lists.add(position, SettingsItem("아이디", id, SettingsItem.Type.Id))
        adapter.notifyItemInserted(position)
    }

    private fun updateLinkFacebook() {
        var position = -1
        for (i in lists.indices) {
            if (lists[i].type === SettingsItem.Type.LinkFacebook) position = i
        }
        if (position == -1) {
            Timber.e("link facebook item not exists!!")
            return
        }
        lists.removeAt(position)
        adapter.notifyItemRemoved(position)
        lists.add(position, SettingsItem("페이스북 연동 취소", SettingsItem.Type.DeleteFacebook))
        adapter.notifyItemInserted(position)
        lists.add(position, SettingsItem("페이스북 이름", "", SettingsItem.Type.FacebookName))
        adapter.notifyItemInserted(position)
        val pos = position
        userRepository.getUserFacebook()
            .bindUi(
                this,
                onSuccess = {
                    facebookNameItem?.detail = it.name
                    adapter.notifyItemChanged(pos)
                },
                onError = {
                    apiOnError(it)
                }
            )
    }

    private fun updateDeleteFacebook() {
        var position = -1
        for (i in lists.indices) {
            if (lists[i].type === SettingsItem.Type.FacebookName) position = i
        }
        if (position == -1) {
            Timber.e("facebook name item not exists!!")
            return
        }
        lists.removeAt(position)
        adapter.notifyItemRemoved(position)
        lists.removeAt(position)
        adapter.notifyItemRemoved(position)
        lists.add(position, SettingsItem("페이스북 연동", SettingsItem.Type.LinkFacebook))
        adapter.notifyItemInserted(position)
    }

    private val facebookNameItem: SettingsItem?
        get() {
            for (item in lists) {
                if (item.type === SettingsItem.Type.FacebookName) return item
            }
            Timber.e("facebook name row does not exists!!")
            return null
        }
    private val emailItem: SettingsItem?
        get() {
            for (item in lists) {
                if (item.type === SettingsItem.Type.Email) return item
            }
            Timber.e("email row does not exists!!")
            return null
        }

    private fun registerFacebookCallback() {
        Timber.d("register facebook callback called.")
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    // App code
                    val id = result.accessToken.userId
                    val token = result.accessToken.token
                    Timber.i("User ID: %s", result.accessToken.userId)
                    Timber.i("Auth Token: %s", result.accessToken.token)
                    userRepository.postUserFacebook(id, token)
                        .bindUi(
                            this@UserSettingsFragment,
                            onSuccess = {
                                updateLinkFacebook()
                            },
                            onError = {
                                apiOnError(it)
                            }
                        )
                }

                override fun onCancel() {
                    // App code
                    Timber.w("Cancel")
                    Toast.makeText(context, "페이스북 연동중 오류가 발생하였습니다", Toast.LENGTH_SHORT).show()
                }

                override fun onError(error: FacebookException) {
                    // App code
                    Timber.e(error)
                    Toast.makeText(context, "페이스북 연동중 오류가 발생하였습니다", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun addSettingsList(user: UserDto) {
        lists.clear()
        if (user.localId.isNullOrEmpty()) {
            lists.add(SettingsItem(SettingsItem.Type.Header))
            lists.add(SettingsItem("아이디 비밀번호 추가", user.localId, SettingsItem.Type.AddIdPassword))
            lists.add(SettingsItem(SettingsItem.Type.Header))
            if (user.fbName.isNullOrEmpty()) { // 연동 x
                lists.add(SettingsItem("페이스북 연동", user.fbName, SettingsItem.Type.LinkFacebook))
            } else {
                lists.add(SettingsItem("페이스북 이름", user.fbName, SettingsItem.Type.FacebookName))
                lists.add(SettingsItem("페이스북 연동 취소", SettingsItem.Type.DeleteFacebook))
            }
            lists.add(SettingsItem(SettingsItem.Type.Header))
            lists.add(SettingsItem("이메일", user.email, SettingsItem.Type.Email))
            lists.add(SettingsItem("이메일 변경", SettingsItem.Type.ChangeEmail))
            lists.add(SettingsItem(SettingsItem.Type.Header))
            lists.add(SettingsItem("회원탈퇴", SettingsItem.Type.Leave))
            // lists.add(new SettingsItem(SettingsItem.Type.Header));
        } else {
            lists.add(SettingsItem(SettingsItem.Type.Header))
            lists.add(SettingsItem("아이디", user.localId, SettingsItem.Type.Id))
            lists.add(SettingsItem("비밀번호 변경", SettingsItem.Type.ChangePassword))
            lists.add(SettingsItem(SettingsItem.Type.Header))
            if (user.fbName.isNullOrEmpty()) { // 연동 x
                lists.add(SettingsItem("페이스북 연동", SettingsItem.Type.LinkFacebook))
            } else {
                lists.add(SettingsItem("페이스북 이름", user.fbName, SettingsItem.Type.FacebookName))
                lists.add(SettingsItem("페이스북 연동 취소", SettingsItem.Type.DeleteFacebook))
            }
            lists.add(SettingsItem(SettingsItem.Type.Header))
            lists.add(SettingsItem("이메일", user.email, SettingsItem.Type.Email))
            lists.add(SettingsItem("이메일 변경", SettingsItem.Type.ChangeEmail))
            lists.add(SettingsItem(SettingsItem.Type.Header))
            lists.add(SettingsItem("회원탈퇴", SettingsItem.Type.Leave))
            // lists.add(new SettingsItem(SettingsItem.Type.Header));
        }
    }
}
