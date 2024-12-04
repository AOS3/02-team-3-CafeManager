package com.lion.cafemanager_1.fragment

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.core.content.edit
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lion.cafemanager_1.MainActivity
import com.lion.cafemanager_1.R
import com.lion.cafemanager_1.databinding.FragmentSettingPasswordBinding
import com.lion.cafemanager_1.util.FragmentName

class SettingPasswordFragment: Fragment() {

    lateinit var fragmentSettingPasswordBinding: FragmentSettingPasswordBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        fragmentSettingPasswordBinding = FragmentSettingPasswordBinding.inflate(layoutInflater, container, false)
        mainActivity = activity as MainActivity

        settingToolbar()
        settingButtonSettingPasswordSubmit()
        settingInitView()

        return fragmentSettingPasswordBinding.root
    }

    // 툴바 설정 메서드
    fun settingToolbar() {
        fragmentSettingPasswordBinding.apply {
            toolbarSettingPassword.title = "관리자 비밀번호 등록 (4자리 숫자)"
        }
    }

    // 기타 초기 설정
    fun settingInitView() {
        fragmentSettingPasswordBinding.apply {
            // 숫자 키보드 띄우기
            textFieldSettingPassword1.editText?.inputType = android.text.InputType.TYPE_CLASS_NUMBER
            textFieldSettingPassword2.editText?.inputType = android.text.InputType.TYPE_CLASS_NUMBER
            mainActivity.showSoftInput(textFieldSettingPassword1.editText!!)
        }
    }

    // 등록 완료 버튼 설정
    fun settingButtonSettingPasswordSubmit() {
        fragmentSettingPasswordBinding.apply {
            buttonSettingPasswordSubmit.setOnClickListener {
                processingInputManagerPassword()
            }
        }
    }

    // 비밀번호 입력 완료 처리 메서드
    fun processingInputManagerPassword() {
        fragmentSettingPasswordBinding.apply {
            val pw1 = textFieldSettingPassword1.editText?.text!!.toString()
            val pw2 = textFieldSettingPassword2.editText?.text!!.toString()

            var inputFlag = true

            // 첫 번째 비밀번호에 입력한 것이 없다면
            if (pw1.isEmpty()) {
                textFieldSettingPassword1.error = "비밀번호를 입력해주세요"
                inputFlag = false
            }

            // 두 번째 비밀번호에 입력한 것이 없다면
            if (pw2.isEmpty()) {
                textFieldSettingPassword2.error = "비밀번호를 입력해주세요"
                inputFlag = false
            }

            // 두 비밀번호가 서로 다르면
            if (pw1.isNotEmpty() && pw2.isNotEmpty() && pw1 != pw2) {
                textFieldSettingPassword1.error = "입력하신 비밀번호가 서로 다릅니다"
                textFieldSettingPassword2.error = "입력하신 비밀번호가 서로 다릅니다"

                textFieldSettingPassword1.editText?.setText("")
                textFieldSettingPassword2.editText?.setText("")
                textFieldSettingPassword1.editText?.requestFocus()
                inputFlag = false
            }

            // 비밀번호가 4자리 숫자만 입력됐는지 확인
            if (inputFlag) {
                if (pw1.length != 4 || !pw1.all { it.isDigit() }) {
                    textFieldSettingPassword1.error = "비밀번호는 4자리 숫자여야 합니다"
                    textFieldSettingPassword1.editText?.setText("")
                    textFieldSettingPassword1.editText?.requestFocus()
                    inputFlag = false
                }
            }

            // 입력이 제대로 되었다면
            if (inputFlag) {
                val managerPef = mainActivity.getSharedPreferences("manager", MODE_PRIVATE)
                managerPef.edit {
                    putString("password", pw1)
                }

                val builder1 = MaterialAlertDialogBuilder(mainActivity)
                builder1.setTitle("등록 완료")
                builder1.setMessage("""
                    관리자 비밀번호가 등록되었습니다.
                    로그인해주세요
                """.trimIndent())
                builder1.setPositiveButton("확인") { dialogInterface: DialogInterface, i: Int ->
                    mainActivity.replaceFragment(FragmentName.LOGIN_FRAGMENT, false, true, null)
                }

                builder1.show()
            }
        }
    }
}