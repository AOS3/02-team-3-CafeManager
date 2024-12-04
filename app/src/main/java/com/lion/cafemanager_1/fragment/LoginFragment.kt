package com.lion.cafemanager_1.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import com.lion.cafemanager_1.MainActivity
import com.lion.cafemanager_1.R
import com.lion.cafemanager_1.databinding.FragmentLoginBinding
import com.lion.cafemanager_1.util.FragmentName

class LoginFragment : Fragment() {

    lateinit var fragmentLoginBinding: FragmentLoginBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentLoginBinding = FragmentLoginBinding.inflate(inflater, container, false)
        mainActivity = activity as MainActivity

        settingToolbarLogin()
        settingButtonLoginSubmit()
        settingInitView()

        return fragmentLoginBinding.root
    }

    // 툴바 설정 메서드
    fun settingToolbarLogin() {
        fragmentLoginBinding.toolbarLogin.title = "관리자 로그인"
    }

    // 기타 초기 설정
    fun settingInitView() {
        fragmentLoginBinding.apply {
            setupPasswordFields()
            mainActivity.showSoftInput(editTextDigit1)
        }
    }

    // 4자리 비밀번호 입력 필드 설정
    fun setupPasswordFields() {
        val editTexts = arrayOf(
            fragmentLoginBinding.editTextDigit1,
            fragmentLoginBinding.editTextDigit2,
            fragmentLoginBinding.editTextDigit3,
            fragmentLoginBinding.editTextDigit4
        )

        for (i in 0 until editTexts.size) {
            editTexts[i].addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (s != null && s.length == 1 && i < editTexts.size - 1) {
                        editTexts[i + 1].requestFocus() // 다음 칸으로 포커스 이동
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }

        // 마지막 칸에서 엔터키를 누르면 입력이 완료된 것으로 간주
        editTexts[3].setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && editTexts[3].text.length == 1) {
                processLogin()
            }
        }
    }

    // 로그인 버튼 설정 메서드
    fun settingButtonLoginSubmit() {
        fragmentLoginBinding.buttonLoginSubmit.setOnClickListener {
            processLogin()
        }
    }

    // 로그인 처리 메서드
    fun processLogin() {
        // 4자리 비밀번호 입력을 하나의 문자열로 합칩니다.
        val password = fragmentLoginBinding.run {
            editTextDigit1.text.toString() +
                    editTextDigit2.text.toString() +
                    editTextDigit3.text.toString() +
                    editTextDigit4.text.toString()
        }

        // 비밀번호 길이가 4가 아니거나, 숫자가 아닌 값이 포함되어 있으면 오류 처리
        if (password.length != 4 || !password.all { it.isDigit() }) {
            fragmentLoginBinding.apply {
                textViewPasswordError.text = "비밀번호는 4자리 숫자여야 합니다." // 오류 메시지 표시
                textViewPasswordError.visibility = View.VISIBLE
            }
            return
        }

        // 저장된 비밀번호 확인
        val managerPref = mainActivity.getSharedPreferences("manager", MODE_PRIVATE)
        val savedPassword = managerPref.getString("password", null)

        if (savedPassword != password) {
            fragmentLoginBinding.apply {
                textViewPasswordError.text = "비밀번호를 잘못 입력하였습니다."
                textViewPasswordError.visibility = View.VISIBLE
            }
            return
        }

        // 비밀번호가 맞다면 화면 이동
        mainActivity.replaceFragment(FragmentName.MAIN_FRAGMENT, false, true, null)
    }
}