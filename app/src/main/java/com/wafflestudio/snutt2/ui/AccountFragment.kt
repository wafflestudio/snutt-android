package com.wafflestudio.snutt2.ui

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.common.base.Strings
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.SNUTTBaseActivity
import com.wafflestudio.snutt2.SNUTTBaseFragment
import com.wafflestudio.snutt2.adapter.SettingsAdapter
import com.wafflestudio.snutt2.manager.UserManager.Companion.instance
import com.wafflestudio.snutt2.model.SettingsItem
import com.wafflestudio.snutt2.model.User
import java.util.*

/**
 * Created by makesource on 2017. 1. 24..
 */
class AccountFragment : SNUTTBaseFragment() {
    private var lists: MutableList<SettingsItem>? = null
    private var adapter: SettingsAdapter? = null
    private var inflater: LayoutInflater? = null
    private var callbackManager: CallbackManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerFacebookCallback()
        lists = ArrayList()
        adapter = SettingsAdapter(lists!!)
        adapter!!.setOnItemClickListener(
            object : SettingsAdapter.ClickListener {
                override fun onClick(v: View?, position: Int) {
                    Log.d(TAG, "$position-th item clicked!")
                    val type = lists!!.get(position).type
                    when (type) {
                        SettingsItem.Type.ChangePassword -> performChangePassword()
                        SettingsItem.Type.ChangeEmail -> performChangeEmail()
                        SettingsItem.Type.AddIdPassword -> performAddIdPassword()
                        SettingsItem.Type.LinkFacebook -> LoginManager.getInstance().logInWithReadPermissions(
                            this@AccountFragment,
                            null
                        )
                        SettingsItem.Type.DeleteFacebook -> performDeleteFacebook()
                        SettingsItem.Type.Leave -> performLeave()
                        else -> {
                        }
                    }
                }
            }
        )
        addSettingsList(instance!!.user)
        adapter!!.notifyDataSetChanged()
        instance!!.getUserInfo()
            .bindUi(this,
                onSuccess = {
                    addSettingsList(it)
                    adapter!!.notifyDataSetChanged()
                },
                onError = {
                    // do nothing
                })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_account, container, false)
        val recyclerView = rootView.findViewById<View>(R.id.account_recyclerView) as RecyclerView
        // recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        this.inflater = inflater
        return rootView
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager!!.onActivityResult(requestCode, resultCode, data)
    }

    private fun performChangePassword() {
        val layout = inflater!!.inflate(R.layout.dialog_change_password, null)
        val alert = AlertDialog.Builder(context!!)
        alert.setTitle("비밀번호 변경")
        alert.setView(layout)
        alert.setPositiveButton("변경") { dialog, whichButton ->
            // do nothing in here. because we override this button listener later
        }.setNegativeButton(
            "취소"
        ) { dialog, whichButton -> dialog.cancel() }
        val dialog = alert.create()
        dialog.show()
        // change default button handler after dialog show.
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val oldPassword = (layout.findViewById<View>(R.id.now_password) as EditText).text.toString()
            val newPassword = (layout.findViewById<View>(R.id.new_password) as EditText).text.toString()
            val newPasswordConfirm = (layout.findViewById<View>(R.id.new_password_confirm) as EditText).text.toString()
            if (newPassword != newPasswordConfirm) {
                Toast.makeText(app, "새 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
            } else {
                instance!!.putUserPassword(oldPassword, newPassword)
                    .bindUi(this,
                        onSuccess = {
                            Toast.makeText(
                                app,
                                "비밀번호를 변경하였습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                            dialog.dismiss()
                        },
                        onError = {
                            // do nothing
                        })
            }
        }
    }

    private fun performChangeEmail() {
        val inflater = activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout2 = inflater.inflate(R.layout.dialog_change_email, null)
        val alert2 = AlertDialog.Builder(context!!)
        alert2.setTitle("이메일 변경")
        alert2.setView(layout2)
        alert2.setPositiveButton("변경") { dialog, whichButton ->
            // do nothing in here. because we override this button listener later
        }.setNegativeButton(
            "취소"
        ) { dialog, whichButton -> dialog.cancel() }
        val dialog2 = alert2.create()
        dialog2.show()
        dialog2.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val email = (layout2.findViewById<View>(R.id.email) as EditText).text.toString()
            if (!Strings.isNullOrEmpty(email)) {
                instance!!.putUserInfo(email)
                    .bindUi(this,
                        onSuccess = {
                            emailItem!!.detail = email
                            adapter!!.notifyDataSetChanged()
                        },
                        onError = {
                            // do nothing
                        }
                    )
                dialog2.dismiss()
            } else {
                Toast.makeText(app, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun performAddIdPassword() {
        val layout = inflater!!.inflate(R.layout.dialog_add_id, null)
        val alert = AlertDialog.Builder(context!!)
        alert.setTitle("아이디 비빌번호 추가")
        alert.setView(layout)
        alert.setPositiveButton("추가") { dialog, whichButton ->
            // do nothing in here. because we override this button listener later
        }.setNegativeButton(
            "취소"
        ) { dialog, whichButton -> dialog.cancel() }
        val dialog = alert.create()
        dialog.show()
        // change default button handler after dialog show.
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val id = (layout.findViewById<View>(R.id.id) as EditText).text.toString()
            val password = (layout.findViewById<View>(R.id.password) as EditText).text.toString()
            val passwordConfirm = (layout.findViewById<View>(R.id.password_confirm) as EditText).text.toString()
            if (password != passwordConfirm) {
                Toast.makeText(app, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show()
            } else {
                instance!!.postUserPassword(id, password)
                    .bindUi(this,
                        onSuccess = {
                            Toast.makeText(app, "아이디를 추가하였습니다", Toast.LENGTH_SHORT).show()
                            updateNewId(id)
                            dialog.dismiss()
                        },
                        onError = {
                            // do nothing
                        }
                    )
            }
        }
    }

    private fun performDeleteFacebook() {
        val alert = AlertDialog.Builder(context!!)
        alert.setTitle("페이스북 연동 끊기")
        alert.setMessage("페이스북 연동을 끊겠습니까?")
        alert.setPositiveButton("끊기") { dialog, whichButton ->
            instance!!.deleteUserFacebook()
                .bindUi(this,
                    onSuccess = {
                        Toast.makeText(app, "페이스북 연동이 끊어졌습니다", Toast.LENGTH_SHORT).show()
                        LoginManager.getInstance().logOut()
                        updateDeleteFacebook()
                    }, onError = {
                    // do nothing
                })
        }.setNegativeButton("취소") { dialog, whichButton -> dialog.cancel() }
        val dialog = alert.create()
        dialog.show()
    }

    private fun performLeave() {
        val alert = AlertDialog.Builder(context!!)
        alert.setTitle("회원탈퇴")
        alert.setMessage("SNUTT 회원 탈퇴를 하겠습니까?")
        alert.setPositiveButton("회원탈퇴") { dialog, whichButton ->
            val progressDialog = ProgressDialog.show(context, "회원탈퇴", "잠시만 기다려 주세요", true, false)
            instance!!.deleteFirebaseToken()
                .flatMap {
                    instance!!.deleteUserAccount()
                }
                .bindUi(this,
                    onSuccess = {
                        instance!!.performLogout()
                        sNUTTBaseActivity!!.startIntro()
                        sNUTTBaseActivity!!.finishAll()
                        progressDialog.dismiss()
                    },
                    onError = {
                        Toast.makeText(app, "회원탈퇴에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                        progressDialog.dismiss()
                    }
                )
        }.setNegativeButton("취소") { dialog, whichButton -> dialog.cancel() }
        val dialog = alert.create()
        dialog.show()
    }

    private fun updateNewId(id: String) {
        var position = -1
        for (i in lists!!.indices) {
            if (lists!![i].type === SettingsItem.Type.AddIdPassword) position = i
        }
        if (position == -1) {
            Log.e(TAG, "Add id & password item not exists!!")
            return
        }
        lists!!.removeAt(position)
        adapter!!.notifyItemRemoved(position)
        lists!!.add(position, SettingsItem("비밀번호 변경", SettingsItem.Type.ChangePassword))
        adapter!!.notifyItemInserted(position)
        lists!!.add(position, SettingsItem("아이디", id, SettingsItem.Type.Id))
        adapter!!.notifyItemInserted(position)
    }

    private fun updateLinkFacebook() {
        var position = -1
        for (i in lists!!.indices) {
            if (lists!![i].type === SettingsItem.Type.LinkFacebook) position = i
        }
        if (position == -1) {
            Log.e(TAG, "link facebook item not exists!!")
            return
        }
        lists!!.removeAt(position)
        adapter!!.notifyItemRemoved(position)
        lists!!.add(position, SettingsItem("페이스북 연동 취소", SettingsItem.Type.DeleteFacebook))
        adapter!!.notifyItemInserted(position)
        lists!!.add(position, SettingsItem("페이스북 이름", "", SettingsItem.Type.FacebookName))
        adapter!!.notifyItemInserted(position)
        val pos = position
        instance!!.getUserFacebook()
            .bindUi(this,
                onSuccess = {
                    facebookNameItem!!.detail = it.name
                    adapter!!.notifyItemChanged(pos)
                },
                onError = {
                    // do nothing
                })
    }

    private fun updateDeleteFacebook() {
        var position = -1
        for (i in lists!!.indices) {
            if (lists!![i].type === SettingsItem.Type.FacebookName) position = i
        }
        if (position == -1) {
            Log.e(TAG, "facebook name item not exists!!")
            return
        }
        lists!!.removeAt(position)
        adapter!!.notifyItemRemoved(position)
        lists!!.removeAt(position)
        adapter!!.notifyItemRemoved(position)
        lists!!.add(position, SettingsItem("페이스북 연동", SettingsItem.Type.LinkFacebook))
        adapter!!.notifyItemInserted(position)
    }

    private val facebookNameItem: SettingsItem?
        private get() {
            for (item in lists!!) {
                if (item.type === SettingsItem.Type.FacebookName) return item
            }
            Log.e(TAG, "facebook name row does not exists!!")
            return null
        }
    private val emailItem: SettingsItem?
        private get() {
            for (item in lists!!) {
                if (item.type === SettingsItem.Type.Email) return item
            }
            Log.e(TAG, "email row does not exists!!")
            return null
        }

    private fun registerFacebookCallback() {
        Log.d(TAG, "register facebook callback called.")
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    // App code
                    val id = loginResult.accessToken.userId
                    val token = loginResult.accessToken.token
                    Log.i(TAG, "User ID: " + loginResult.accessToken.userId)
                    Log.i(TAG, "Auth Token: " + loginResult.accessToken.token)
                    instance!!.postUserFacebook(id, token)
                        .bindUi(this@AccountFragment,
                            onSuccess = {
                                updateLinkFacebook()
                            },
                            onError = {
                                // do nothing
                            })
                }

                override fun onCancel() {
                    // App code
                    Log.w(TAG, "Cancel")
                    Toast.makeText(app, "페이스북 연동중 오류가 발생하였습니다", Toast.LENGTH_SHORT).show()
                }

                override fun onError(exception: FacebookException) {
                    // App code
                    Log.e(TAG, "Error", exception)
                    Toast.makeText(app, "페이스북 연동중 오류가 발생하였습니다", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun addSettingsList(user: User) {
        lists!!.clear()
        if (Strings.isNullOrEmpty(user.local_id)) {
            lists!!.add(SettingsItem(SettingsItem.Type.Header))
            lists!!.add(SettingsItem("아이디 비밀번호 추가", user.local_id, SettingsItem.Type.AddIdPassword))
            lists!!.add(SettingsItem(SettingsItem.Type.Header))
            if (Strings.isNullOrEmpty(user.fb_name)) { // 연동 x
                lists!!.add(SettingsItem("페이스북 연동", user.fb_name, SettingsItem.Type.LinkFacebook))
            } else {
                lists!!.add(SettingsItem("페이스북 이름", user.fb_name, SettingsItem.Type.FacebookName))
                lists!!.add(SettingsItem("페이스북 연동 취소", SettingsItem.Type.DeleteFacebook))
            }
            lists!!.add(SettingsItem(SettingsItem.Type.Header))
            lists!!.add(SettingsItem("이메일", user.email, SettingsItem.Type.Email))
            lists!!.add(SettingsItem("이메일 변경", SettingsItem.Type.ChangeEmail))
            lists!!.add(SettingsItem(SettingsItem.Type.Header))
            lists!!.add(SettingsItem("회원탈퇴", SettingsItem.Type.Leave))
            // lists.add(new SettingsItem(SettingsItem.Type.Header));
        } else {
            lists!!.add(SettingsItem(SettingsItem.Type.Header))
            lists!!.add(SettingsItem("아이디", user.local_id, SettingsItem.Type.Id))
            lists!!.add(SettingsItem("비밀번호 변경", SettingsItem.Type.ChangePassword))
            lists!!.add(SettingsItem(SettingsItem.Type.Header))
            if (Strings.isNullOrEmpty(user.fb_name)) { // 연동 x
                lists!!.add(SettingsItem("페이스북 연동", SettingsItem.Type.LinkFacebook))
            } else {
                lists!!.add(SettingsItem("페이스북 이름", user.fb_name, SettingsItem.Type.FacebookName))
                lists!!.add(SettingsItem("페이스북 연동 취소", SettingsItem.Type.DeleteFacebook))
            }
            lists!!.add(SettingsItem(SettingsItem.Type.Header))
            lists!!.add(SettingsItem("이메일", user.email, SettingsItem.Type.Email))
            lists!!.add(SettingsItem("이메일 변경", SettingsItem.Type.ChangeEmail))
            lists!!.add(SettingsItem(SettingsItem.Type.Header))
            lists!!.add(SettingsItem("회원탈퇴", SettingsItem.Type.Leave))
            // lists.add(new SettingsItem(SettingsItem.Type.Header));
        }
    }

    private val sNUTTBaseActivity: SNUTTBaseActivity?
        private get() = activity as SNUTTBaseActivity?

    companion object {
        private const val TAG = "ACCOUNT_FRAGMENT"
    }
}
