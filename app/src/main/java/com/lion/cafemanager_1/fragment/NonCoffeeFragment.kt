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
import com.lion.cafemanager_1.databinding.FragmentNonCoffeeBinding
import com.lion.cafemanager_1.databinding.ItemNoncoffeeMenuBinding
import com.lion.cafemanager_1.repository.CafeRepository
import com.lion.cafemanager_1.util.FragmentName
import com.lion.cafemanager_1.viewmodel.CafeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NonCoffeeFragment : Fragment() {

    lateinit var fragmentNonCoffeeBinding: FragmentNonCoffeeBinding
    lateinit var mainActivity: MainActivity
    lateinit var nonCoffeeMenuAdapter: NonCoffeeMenuAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentNonCoffeeBinding = FragmentNonCoffeeBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        // Toolbar 구성 메서드 호출
        settingToolbar()

        // 논커피 메뉴 리스트 가져오기
        CoroutineScope(Dispatchers.Main).launch {
            val nonCoffeeMenuList = fetchNonCoffeeMenuList()
            nonCoffeeMenuAdapter = NonCoffeeMenuAdapter(nonCoffeeMenuList)
            fragmentNonCoffeeBinding.recyclerViewNonCoffee.layoutManager = LinearLayoutManager(requireContext())
            fragmentNonCoffeeBinding.recyclerViewNonCoffee.adapter = nonCoffeeMenuAdapter

            // 구분선
            val deco = MaterialDividerItemDecoration(mainActivity, MaterialDividerItemDecoration.VERTICAL)
            fragmentNonCoffeeBinding.recyclerViewNonCoffee.addItemDecoration(deco)
        }

        return fragmentNonCoffeeBinding.root
    }

    // 논커피 메뉴 리스트를 가져오는 메서드
    private suspend fun fetchNonCoffeeMenuList(): List<CafeViewModel> {
        return withContext(Dispatchers.IO) {
            CafeRepository.selectNonCoffeeMenu(requireContext()) // 논커피 메뉴를 가져오는 Repository 함수 호출
        }
    }

    // Toolbar 설정 메서드
    fun settingToolbar() {
        fragmentNonCoffeeBinding.apply {
            // 타이틀 설정
            toolbarNonCoffee.title = "논커피 메뉴만 보기"
            // 네비게이션 아이콘 설정
            toolbarNonCoffee.setNavigationIcon(R.drawable.arrow_back_24px)
            toolbarNonCoffee.setNavigationOnClickListener {
                mainActivity.removeFragment(FragmentName.NON_COFFEE_FRAGMENT)
            }
        }
    }

    // NonCoffeeMenuAdapter 클래스
    inner class NonCoffeeMenuAdapter(private val nonCoffeeMenuList: List<CafeViewModel>) : RecyclerView.Adapter<NonCoffeeMenuAdapter.NonCoffeeMenuViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NonCoffeeMenuViewHolder {
            val binding = ItemNoncoffeeMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return NonCoffeeMenuViewHolder(binding)
        }

        override fun onBindViewHolder(holder: NonCoffeeMenuViewHolder, position: Int) {
            val nonCoffeeMenu = nonCoffeeMenuList[position]
            holder.bind(nonCoffeeMenu)
        }

        override fun getItemCount(): Int {
            return nonCoffeeMenuList.size
        }

        inner class NonCoffeeMenuViewHolder(private val binding: ItemNoncoffeeMenuBinding) : RecyclerView.ViewHolder(binding.root) {

            init {
                // 클릭 시 ShowFragment로 이동
                binding.root.setOnClickListener {
                    val nonCoffeeMenu = nonCoffeeMenuList[adapterPosition]
                    val dataBundle = Bundle().apply {
                        putInt("cafeIdx", nonCoffeeMenu.cafeIdx) // 클릭한 메뉴의 cafeIdx 전달
                    }
                    mainActivity.replaceFragment(FragmentName.SHOW_FRAGMENT, true, true, dataBundle)
                }
            }

            fun bind(nonCoffeeMenu: CafeViewModel) {
                binding.apply {
                    textViewRowCafeNonCoffee.text = nonCoffeeMenu.cafeName // 메뉴 이름 표시
                }
            }
        }
    }
}
