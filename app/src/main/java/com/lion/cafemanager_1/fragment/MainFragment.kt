package com.lion.cafemanager_1.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lion.cafemanager_1.MainActivity
import com.lion.cafemanager_1.R
import com.lion.cafemanager_1.databinding.FragmentMainBinding
import com.lion.cafemanager_1.util.FragmentName

class MainFragment : Fragment() {
    lateinit var fragmentMainBinding: FragmentMainBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentMainBinding = FragmentMainBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        // Toolbar 구성 메서드 호출
        settingToolbar()

        // 카페 메뉴 리스트를 보는 버튼 메서드 호출
        settingButtonCafeMenuShowAll()

        // 카페 메뉴를 추가하는 버튼 메서드 호출
        settingButtonCafeMenuInput()

        // 카페 메뉴 검색하는 버튼 메서드 호출
        settingButtonCafeMenuSearch()

        // 커피 메뉴 버튼 메서드 호출
        settingButtonCafeMenuCoffee()

        // 논커피 메뉴 버튼 메서드 호출
        settingButtonCafeMenuNonCoffee()

        // 빵 메뉴 버튼 메서드 호출
        settingButtonCafeMenuBread()

        // 케이크 메뉴 버튼 메서드 호출
        settingButtonCafeMenuCake()

        return fragmentMainBinding.root
    }

    // Toolbar를 구성하는 메서드
    fun settingToolbar(){
        fragmentMainBinding.apply {
            // 타이틀
            toolbarMain.title = "카페 메뉴 관리 프로그램"
        }
    }

    // 카페 메뉴 리스트를 보는 버튼을 구성하는 메서드
    fun settingButtonCafeMenuShowAll(){
        fragmentMainBinding.apply {
            buttonCafeMenuShowAll.setOnClickListener {
                // ShowAllFragment 이동
                mainActivity.replaceFragment(FragmentName.SHOW_ALL_FRAGMENT, true, true, null)
            }
        }
    }

    // 카페 메뉴를 추가하는 버튼을 구성하는 메서드
    fun settingButtonCafeMenuInput(){
        fragmentMainBinding.apply {
            buttonCafeMenuInput.setOnClickListener {
                // InputFragment 이동
                mainActivity.replaceFragment(FragmentName.INPUT_FRAGMENT, true, true, null)
            }
        }
    }

    // 카페 메뉴를 검색하는 버튼을 구성하는 메서드
    fun settingButtonCafeMenuSearch(){
        fragmentMainBinding.apply {
            buttonCafeMenuSearch.setOnClickListener {
                // SearchFragment 이동
                mainActivity.replaceFragment(FragmentName.SEARCH_FRAGMENT, true, true, null)
            }
        }
    }

    // 커피 메뉴 버튼을 구성하는 메서드
    fun settingButtonCafeMenuCoffee(){
        fragmentMainBinding.apply {
            buttonCafeMenuCoffee.setOnClickListener {
                // CoffeeFragment 이동
                mainActivity.replaceFragment(FragmentName.COFFEE_FRAGMENT, true, true, null)
            }
        }
    }

    // 논커피 메뉴 버튼을 구성하는 메서드
    fun settingButtonCafeMenuNonCoffee(){
        fragmentMainBinding.apply {
            buttonCafeMenuNonCoffee.setOnClickListener {
                // NonCoffeeFragment 이동
                mainActivity.replaceFragment(FragmentName.NON_COFFEE_FRAGMENT, true, true, null)
            }
        }
    }

    // 빵 메뉴 버튼을 구성하는 메서드
    fun settingButtonCafeMenuBread(){
        fragmentMainBinding.apply {
            buttonCafeMenuBread.setOnClickListener {
                // BreadFragment 이동
                mainActivity.replaceFragment(FragmentName.BREAD_FRAGMENT, true, true, null)
            }
        }
    }

    // 케이크 메뉴 버튼을 구성하는 메서드
    fun settingButtonCafeMenuCake(){
        fragmentMainBinding.apply {
            buttonCafeMenuCake.setOnClickListener {
                // CakeFragment 이동
                mainActivity.replaceFragment(FragmentName.CAKE_FRAGMENT, true, true, null)
            }
        }
    }
}