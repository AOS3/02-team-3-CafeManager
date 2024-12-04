package com.lion.cafemanager_1

import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.transition.MaterialSharedAxis
import com.lion.cafemanager_1.databinding.ActivityMainBinding
import com.lion.cafemanager_1.fragment.BreadFragment
import com.lion.cafemanager_1.fragment.CakeBookingFragment
import com.lion.cafemanager_1.fragment.CakeFragment
import com.lion.cafemanager_1.fragment.CoffeeFragment
import com.lion.cafemanager_1.fragment.InputFragment
import com.lion.cafemanager_1.fragment.LoginFragment
import com.lion.cafemanager_1.fragment.MainFragment
import com.lion.cafemanager_1.fragment.ModifyFragment
import com.lion.cafemanager_1.fragment.NonCoffeeFragment
import com.lion.cafemanager_1.fragment.SearchFragment
import com.lion.cafemanager_1.fragment.SettingPasswordFragment
import com.lion.cafemanager_1.fragment.ShowAllFragment
import com.lion.cafemanager_1.fragment.ShowFragment
import com.lion.cafemanager_1.util.FragmentName
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    lateinit var activityMainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        // 앱의 최초 실행 시 비밀번호 등록 여부를 확인
        val managerPref = getSharedPreferences("manager", MODE_PRIVATE)
        val isPasswordSet = managerPref.contains("password")

        // 비밀번호가 설정되어 있지 않으면 SettingPasswordFragment로 이동
        if (isPasswordSet) {
            // 비밀번호가 설정되어 있으면 LoginFragment로 이동
            replaceFragment(FragmentName.LOGIN_FRAGMENT, false, false,null)
        } else {
            // 비밀번호가 설정되어 있지 않으면 SettingPasswordFragment로 이동
            replaceFragment(FragmentName.SETTING_PASSWORD_FRAGMENT, false, false, null)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // 프래그먼트를 교체하는 함수
    fun replaceFragment(fragmentName: FragmentName, isAddToBackStack: Boolean, animate:Boolean, dataBundle: Bundle?) {
        // 프래그먼트 객체
        val newFragment = when (fragmentName) {
            // 로그인 화면
            FragmentName.LOGIN_FRAGMENT -> LoginFragment()
            // 비밀번호 화면 설정 화면
            FragmentName.SETTING_PASSWORD_FRAGMENT -> SettingPasswordFragment()
            // 첫 화면
            FragmentName.MAIN_FRAGMENT -> MainFragment()
            // 입력 화면
            FragmentName.INPUT_FRAGMENT -> InputFragment()
            // 출력 화면
            FragmentName.SHOW_FRAGMENT -> ShowFragment()
            // 수정 화면
            FragmentName.MODIFY_FRAGMENT -> ModifyFragment()
            // 검색 화면
            FragmentName.SEARCH_FRAGMENT -> SearchFragment()
            // 목록 화면
            FragmentName.SHOW_ALL_FRAGMENT -> ShowAllFragment()
            // 커피 메뉴 화면
            FragmentName.COFFEE_FRAGMENT -> CoffeeFragment()
            // 논커피 메뉴 화면
            FragmentName.NON_COFFEE_FRAGMENT -> NonCoffeeFragment()
            // 빵 메뉴 화면
            FragmentName.BREAD_FRAGMENT -> BreadFragment()
            // 케이크 메뉴 화면
            FragmentName.CAKE_FRAGMENT -> CakeFragment()
            // 케이크 예약 화면
            FragmentName.CAKE_BOOKING_FRAGMENT -> CakeBookingFragment()
        }

        // bundle 객체가 null이 아니라면
        if (dataBundle != null) {
            newFragment.arguments = dataBundle
        }

        // 프래그먼트 교체
        supportFragmentManager.commit {
            newFragment.exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
            newFragment.reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
            newFragment.enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
            newFragment.returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)

            replace(R.id.containerFragmentMainView, newFragment)
            if (isAddToBackStack) {
                addToBackStack(fragmentName.str)
            }
        }
    }

    // 키보드 올리는 메서드
    fun showSoftInput(view: View) {
        val inputManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        view.requestFocus()

        thread {
            SystemClock.sleep(500)
            inputManager.showSoftInput(view, 0)
        }
    }

    // 키보드를 내리는 메서드
    fun hideSoftInput() {
        if (currentFocus != null) {
            val inputManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
            currentFocus?.clearFocus()
        }
    }

    // 리사이클러 뷰 스크롤에 따라 fab 설정하는 메서드
    fun fabHideAndShow(recyclerView: RecyclerView, fab: FloatingActionButton){
        // 리사이클러뷰가 스크롤 상태가 변경되면 동작하는 리스너
        recyclerView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if(oldScrollY == 0) {
                fab.show()
            } else {
                // 만약 제일 아래에 있는 상태라면..
                if (recyclerView.canScrollVertically(1) == false) {
                    // FloatingActionButton을 사라지게 된다.
                    fab.hide()
                } else {
                    // 만약 제일 아래에 있는 상태가 아니고 FloatingActionButton이 보이지 않는 상태라면
                    if (fab.isShown == false) {
                        // FloatingActionButton을 나타나게 한다.
                        fab.show()
                    }
                }
            }
        }
    }

    // 프래그먼트를 BackStack에서 제거하는 메서드
    fun removeFragment(fragmentName: FragmentName){
        supportFragmentManager.popBackStack(fragmentName.str, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }
}