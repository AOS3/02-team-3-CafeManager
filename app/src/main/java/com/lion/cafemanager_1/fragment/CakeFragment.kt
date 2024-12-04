package com.lion.cafemanager_1.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.lion.cafemanager_1.MainActivity
import com.lion.cafemanager_1.R
import com.lion.cafemanager_1.databinding.FragmentCakeBinding
import com.lion.cafemanager_1.databinding.ItemCakeMenuBinding
import com.lion.cafemanager_1.repository.CafeRepository
import com.lion.cafemanager_1.util.FragmentName
import com.lion.cafemanager_1.viewmodel.CafeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CakeFragment : Fragment() {

    lateinit var fragmentCakeBinding: FragmentCakeBinding
    lateinit var mainActivity: MainActivity
    lateinit var cakeMenuAdapter: CakeMenuAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        fragmentCakeBinding = FragmentCakeBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        // Toolbar 구성 메서드 호출
        settingToolbar()

        // 케이크 메뉴 리스트 가져오기
        CoroutineScope(Dispatchers.Main).launch {
            val cakeMenuList = fetchCakeMenuList()
            cakeMenuAdapter = CakeMenuAdapter(cakeMenuList)
            fragmentCakeBinding.recyclerViewCake.layoutManager = LinearLayoutManager(requireContext())
            fragmentCakeBinding.recyclerViewCake.adapter = cakeMenuAdapter

            // 구분선
            val deco = MaterialDividerItemDecoration(mainActivity, MaterialDividerItemDecoration.VERTICAL)
            fragmentCakeBinding.recyclerViewCake.addItemDecoration(deco)
        }

        return fragmentCakeBinding.root
    }

    // 케이크 메뉴 리스트를 가져오는 메서드
    private suspend fun fetchCakeMenuList(): List<CafeViewModel> {
        return withContext(Dispatchers.IO) {
            // 메뉴 데이터를 가져오는 함수 (CafeRepository에 정의되어 있다고 가정)
            CafeRepository.selectCakeMenu(requireContext())
        }
    }

    // Toolbar 설정 메서드
    fun settingToolbar() {
        fragmentCakeBinding.apply {
            // 타이틀 설정
            toolbarCake.title = "케이크 메뉴만 보기"
            // 네비게이션 아이콘 설정
            toolbarCake.setNavigationIcon(R.drawable.arrow_back_24px)
            toolbarCake.setNavigationOnClickListener {
                mainActivity.removeFragment(FragmentName.CAKE_FRAGMENT)
            }
        }
    }

    // CakeMenuAdapter 클래스
    inner class CakeMenuAdapter(private val cakeMenuList: List<CafeViewModel>) : RecyclerView.Adapter<CakeMenuAdapter.CakeMenuViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CakeMenuViewHolder {
            val binding = ItemCakeMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CakeMenuViewHolder(binding)
        }

        override fun onBindViewHolder(holder: CakeMenuViewHolder, position: Int) {
            val cakeMenu = cakeMenuList[position]
            holder.bind(cakeMenu)
        }

        override fun getItemCount(): Int {
            return cakeMenuList.size
        }

        inner class CakeMenuViewHolder(private val binding: ItemCakeMenuBinding) : RecyclerView.ViewHolder(binding.root) {

            init {
                // 클릭 시 ShowFragment로 이동
                binding.root.setOnClickListener {
                    val cakeMenu = cakeMenuList[adapterPosition]
                    val dataBundle = Bundle().apply {
                        putInt("cafeIdx", cakeMenu.cafeIdx) // 클릭한 메뉴의 cafeIdx 전달
                    }
                    mainActivity.replaceFragment(FragmentName.SHOW_FRAGMENT, true, true, dataBundle)
                }
            }

            fun bind(cakeMenu: CafeViewModel) {
                binding.apply {
                    textViewRowCafeCake.text = cakeMenu.cafeName // 메뉴 이름을 표시
                }
            }
        }
    }
}

