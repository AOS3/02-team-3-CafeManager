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
import com.lion.cafemanager_1.databinding.FragmentCoffeeBinding
import com.lion.cafemanager_1.databinding.ItemCoffeeMenuBinding
import com.lion.cafemanager_1.repository.CafeRepository
import com.lion.cafemanager_1.util.FragmentName
import com.lion.cafemanager_1.viewmodel.CafeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CoffeeFragment : Fragment() {

    lateinit var fragmentCoffeeBinding: FragmentCoffeeBinding
    lateinit var mainActivity: MainActivity
    lateinit var coffeeMenuAdapter: CoffeeMenuAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentCoffeeBinding = FragmentCoffeeBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        // Toolbar 구성 메서드 호출
        settingToolbar()

        // 커피 메뉴 리스트 가져오기
        CoroutineScope(Dispatchers.Main).launch {
            val coffeeMenuList = fetchCoffeeMenuList()
            coffeeMenuAdapter = CoffeeMenuAdapter(coffeeMenuList)
            fragmentCoffeeBinding.recyclerViewCoffee.layoutManager = LinearLayoutManager(requireContext())
            fragmentCoffeeBinding.recyclerViewCoffee.adapter = coffeeMenuAdapter

            // 구분선
            val deco = MaterialDividerItemDecoration(mainActivity, MaterialDividerItemDecoration.VERTICAL)
            fragmentCoffeeBinding.recyclerViewCoffee.addItemDecoration(deco)
        }

        return fragmentCoffeeBinding.root
    }

    // 커피 메뉴 리스트를 가져오는 메서드
    private suspend fun fetchCoffeeMenuList(): List<CafeViewModel> {
        return withContext(Dispatchers.IO) {
            CafeRepository.selectCoffeeMenu(requireContext()) // 커피 메뉴를 가져오는 Repository 함수 호출
        }
    }

    // Toolbar 설정 메서드
    fun settingToolbar() {
        fragmentCoffeeBinding.apply {
            // 타이틀 설정
            toolbarCoffee.title = "커피 메뉴만 보기"
            // 네비게이션 아이콘 설정
            toolbarCoffee.setNavigationIcon(R.drawable.arrow_back_24px)
            toolbarCoffee.setNavigationOnClickListener {
                mainActivity.removeFragment(FragmentName.COFFEE_FRAGMENT)
            }
        }
    }

    // CoffeeMenuAdapter 클래스
    inner class CoffeeMenuAdapter(private val coffeeMenuList: List<CafeViewModel>) : RecyclerView.Adapter<CoffeeMenuAdapter.CoffeeMenuViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoffeeMenuViewHolder {
            val binding = ItemCoffeeMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CoffeeMenuViewHolder(binding)
        }

        override fun onBindViewHolder(holder: CoffeeMenuViewHolder, position: Int) {
            val coffeeMenu = coffeeMenuList[position]
            holder.bind(coffeeMenu)
        }

        override fun getItemCount(): Int {
            return coffeeMenuList.size
        }

        inner class CoffeeMenuViewHolder(private val binding: ItemCoffeeMenuBinding) : RecyclerView.ViewHolder(binding.root) {

            init {
                // 클릭 시 ShowFragment로 이동
                binding.root.setOnClickListener {
                    val coffeeMenu = coffeeMenuList[adapterPosition]
                    val dataBundle = Bundle().apply {
                        putInt("cafeIdx", coffeeMenu.cafeIdx) // 클릭한 메뉴의 cafeIdx 전달
                    }
                    mainActivity.replaceFragment(FragmentName.SHOW_FRAGMENT, true, true, dataBundle)
                }
            }

            fun bind(coffeeMenu: CafeViewModel) {
                binding.apply {
                    textViewRowCafeCoffee.text = coffeeMenu.cafeName // 메뉴 이름 표시
                }
            }
        }
    }
}
