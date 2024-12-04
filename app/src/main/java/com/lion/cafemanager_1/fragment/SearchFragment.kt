package com.lion.cafemanager_1.fragment

import android.inputmethodservice.Keyboard.Row
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.lion.cafemanager_1.MainActivity
import com.lion.cafemanager_1.R
import com.lion.cafemanager_1.databinding.FragmentSearchBinding
import com.lion.cafemanager_1.databinding.RowMainBinding
import com.lion.cafemanager_1.fragment.ShowAllFragment.RecyclerViewMainAdapter
import com.lion.cafemanager_1.repository.CafeRepository
import com.lion.cafemanager_1.util.FragmentName
import com.lion.cafemanager_1.viewmodel.CafeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {
    lateinit var fragmentSearchBinding: FragmentSearchBinding
    lateinit var mainActivity: MainActivity

    var cafeList = mutableListOf<CafeViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentSearchBinding = FragmentSearchBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        // Toolbar 구성 메서드 호출
        settingToolbar()

        // 입력 요소 설정 메서드 호출
        settingTextField()

        // RecyclerView 구성 메서드 호출
        settingRecyclerViewSearch()

        return fragmentSearchBinding.root
    }

    // Toolbar를 구성하는 메서드
    fun settingToolbar(){
        fragmentSearchBinding.apply {
            // 타이틀
            toolbarSearch.title = "카페 메뉴 검색"
            // 네비게이션
            toolbarSearch.setNavigationIcon(R.drawable.arrow_back_24px)
            toolbarSearch.setNavigationOnClickListener {
                mainActivity.removeFragment(FragmentName.SEARCH_FRAGMENT)
            }
        }
    }

    // recyclerView를 구성하는 메서드
    fun settingRecyclerViewSearch(){
        fragmentSearchBinding.apply {
            recyclerViewSearchResult.adapter = RecyclerViewSearchAdapter()
            recyclerViewSearchResult.layoutManager = LinearLayoutManager(mainActivity)
            val deco = MaterialDividerItemDecoration(mainActivity, MaterialDividerItemDecoration.VERTICAL)
            recyclerViewSearchResult.addItemDecoration(deco)
        }
    }

    // RecyclerView 어댑터
    inner class RecyclerViewSearchAdapter : RecyclerView.Adapter<RecyclerViewSearchAdapter.ViewHolderSearch>() {
        // ViewHolder
        inner class ViewHolderSearch(val rowMainBinding: RowMainBinding) : RecyclerView.ViewHolder(rowMainBinding.root), OnClickListener{
            override fun onClick(v: View?) {
                // 카페 메뉴 정보 보는 화면으로 이동
                val dataBundle = Bundle()
                dataBundle.putInt("cafeIdx", cafeList[adapterPosition].cafeIdx)

                mainActivity.replaceFragment(FragmentName.SHOW_FRAGMENT, true, true, dataBundle)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderSearch {
            val rowMainBinding = RowMainBinding.inflate(layoutInflater, parent, false)
            val viewHolderSearch = ViewHolderSearch(rowMainBinding)
            rowMainBinding.root.setOnClickListener(viewHolderSearch)
            return viewHolderSearch
        }

        override fun getItemCount(): Int {
            return cafeList.size
        }

        override fun onBindViewHolder(holder: ViewHolderSearch, position: Int) {
            holder.rowMainBinding.textViewRowMainCafeName.text = cafeList[position].cafeName
        }

    }

    // 입력 요소 설정
    fun settingTextField(){
        fragmentSearchBinding.apply {
            // 검색창에 포커스를 준다.
            mainActivity.showSoftInput(textInputLayoutSearch.editText!!)
            // 키보드의 엔터를 누르면 동작하는 리스너
            // textInputLayoutSearch.editText?.setOnEditorActionListener { v, actionId, event ->
            buttonSearch.setOnClickListener {
                // 검색 데이터를 가져와 보여준다.
                CoroutineScope(Dispatchers.Main).launch {
                    val work1 = async(Dispatchers.IO){
                        val keyword = textInputLayoutSearch.editText?.text.toString()
                        CafeRepository.selectCafeDataAllByCafeName(mainActivity, keyword)
                    }
                    cafeList = work1.await()
                    recyclerViewSearchResult.adapter?.notifyDataSetChanged()
                }
                mainActivity.hideSoftInput()
                true
            }
        }
    }
}