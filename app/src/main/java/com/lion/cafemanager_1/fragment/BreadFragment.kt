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
import com.lion.cafemanager_1.databinding.FragmentBreadBinding
import com.lion.cafemanager_1.databinding.ItemBreadMenuBinding
import com.lion.cafemanager_1.repository.CafeRepository
import com.lion.cafemanager_1.util.FragmentName
import com.lion.cafemanager_1.viewmodel.CafeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BreadFragment : Fragment() {

    lateinit var fragmentBreadBinding: FragmentBreadBinding
    lateinit var mainActivity: MainActivity
    lateinit var breadMenuAdapter: BreadMenuAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        fragmentBreadBinding = FragmentBreadBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        // Toolbar 구성 메서드 호출
        settingToolbar()

        // 빵 메뉴 리스트 가져오기
        CoroutineScope(Dispatchers.Main).launch {
            val breadMenuList = fetchBreadMenuList()
            breadMenuAdapter = BreadMenuAdapter(breadMenuList)
            fragmentBreadBinding.recyclerViewBread.layoutManager = LinearLayoutManager(requireContext())
            fragmentBreadBinding.recyclerViewBread.adapter = breadMenuAdapter

            // 구분선
            val deco = MaterialDividerItemDecoration(mainActivity, MaterialDividerItemDecoration.VERTICAL)
            fragmentBreadBinding.recyclerViewBread.addItemDecoration(deco)
        }

        return fragmentBreadBinding.root
    }

    // 빵 메뉴 리스트를 가져오는 메서드
    private suspend fun fetchBreadMenuList(): List<CafeViewModel> {
        return withContext(Dispatchers.IO) {
            CafeRepository.selectBreadMenu(requireContext())
        }
    }

    // Toolbar 설정 메서드
    fun settingToolbar() {
        fragmentBreadBinding.apply {
            // 타이틀 설정
            toolbarBread.title = "빵 메뉴만 보기"
            // 네비게이션 아이콘 설정
            toolbarBread.setNavigationIcon(R.drawable.arrow_back_24px)
            toolbarBread.setNavigationOnClickListener {
                mainActivity.removeFragment(FragmentName.BREAD_FRAGMENT)
            }
        }
    }

    // BreadMenuAdapter 클래스
    inner class BreadMenuAdapter(private val breadMenuList: List<CafeViewModel>) : RecyclerView.Adapter<BreadMenuAdapter.BreadMenuViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BreadMenuViewHolder {
            val binding = ItemBreadMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return BreadMenuViewHolder(binding)
        }

        override fun onBindViewHolder(holder: BreadMenuViewHolder, position: Int) {
            val breadMenu = breadMenuList[position]
            holder.bind(breadMenu)
        }

        override fun getItemCount(): Int {
            return breadMenuList.size
        }

        inner class BreadMenuViewHolder(private val binding: ItemBreadMenuBinding) : RecyclerView.ViewHolder(binding.root) {

            init {
                // 클릭 시 ShowFragment로 이동
                binding.root.setOnClickListener {
                    val breadMenu = breadMenuList[adapterPosition]
                    val dataBundle = Bundle().apply {
                        putInt("cafeIdx", breadMenu.cafeIdx) // 클릭한 메뉴의 cafeIdx 전달
                    }
                    mainActivity.replaceFragment(FragmentName.SHOW_FRAGMENT, true, true, dataBundle)
                }
            }

            fun bind(breadMenu: CafeViewModel) {
                binding.apply {
                    textViewRowCafeBread.text = breadMenu.cafeName
                }
            }
        }
    }
}

