package com.lion.cafemanager_1.fragment

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.android.material.snackbar.Snackbar
import com.lion.cafemanager_1.MainActivity
import com.lion.cafemanager_1.R
import com.lion.cafemanager_1.databinding.FragmentShowAllBinding
import com.lion.cafemanager_1.databinding.RowMainBinding
import com.lion.cafemanager_1.repository.CafeRepository
import com.lion.cafemanager_1.util.FragmentName
import com.lion.cafemanager_1.viewmodel.CafeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.math.truncate

class ShowAllFragment : Fragment() {

    var cafeList = mutableListOf<CafeViewModel>()
    lateinit var fragmentShowAllBinding: FragmentShowAllBinding
    lateinit var mainActivity: MainActivity

    var showCafeStock = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentShowAllBinding = FragmentShowAllBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        // Toolbar 구성
        settingToolbar()

        // FAB 구성
        settingFAB()

        // RecyclerView 갱신
        refreshRecyclerViewShowAll()

        // RecyclerView 설정
        settingRecyclerView()

        return fragmentShowAllBinding.root
    }

    // Toolbar 설정 메서드
    fun settingToolbar() {
        fragmentShowAllBinding.apply {
            toolbarShowAll.title = "카페 메뉴 목록"
            toolbarShowAll.setNavigationIcon(R.drawable.arrow_back_24px)
            toolbarShowAll.setNavigationOnClickListener {
                mainActivity.removeFragment(FragmentName.SHOW_ALL_FRAGMENT)
            }

            toolbarShowAll.inflateMenu(R.menu.toolbar_show_all_menu)
            toolbarShowAll.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.toolbar_main_menu_all_delete -> {
                        showDeleteConfirmationDialog()
                        true
                    }
                    R.id.cakeBookingShow -> {
                        mainActivity.replaceFragment(FragmentName.CAKE_BOOKING_FRAGMENT, true, true, null)
                        true
                    }
                    else -> false
                }
            }
        }
    }

    // RecyclerView 갱신
    fun refreshRecyclerViewShowAll() {
        CoroutineScope(Dispatchers.Main).launch {
            val work1 = async(Dispatchers.IO) {
                val allCafeList = CafeRepository.selectCafeInfoAll(mainActivity)

                // 재고 보이기/숨기기 필터링
                if (showCafeStock) {
                    cafeList = allCafeList
                } else {
                    cafeList = allCafeList.filter { it.cafeStock > 0 }.toMutableList()
                }
            }
            work1.await()

            // RecyclerView 갱신
            fragmentShowAllBinding.recyclerViewMain.adapter?.notifyDataSetChanged()
        }
    }


    // RecyclerView 설정
    fun settingRecyclerView() {
        fragmentShowAllBinding.recyclerViewMain.apply {
            adapter = RecyclerViewMainAdapter()
            layoutManager = LinearLayoutManager(mainActivity)
            addItemDecoration(MaterialDividerItemDecoration(mainActivity, MaterialDividerItemDecoration.VERTICAL))
        }
    }

    // RecyclerView 어댑터
    inner class RecyclerViewMainAdapter : RecyclerView.Adapter<RecyclerViewMainAdapter.ViewHolderMain>() {

        inner class ViewHolderMain(val rowMainBinding: RowMainBinding) : RecyclerView.ViewHolder(rowMainBinding.root) {
            init {
                rowMainBinding.root.setOnClickListener {
                    val dataBundle = Bundle()
                    dataBundle.putInt("cafeIdx", cafeList[adapterPosition].cafeIdx)
                    mainActivity.replaceFragment(FragmentName.SHOW_FRAGMENT, true, true, dataBundle)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderMain {
            val rowMainBinding = RowMainBinding.inflate(layoutInflater, parent, false)
            return ViewHolderMain(rowMainBinding)
        }

        override fun getItemCount() = cafeList.size

        override fun onBindViewHolder(holder: ViewHolderMain, position: Int) {
            holder.rowMainBinding.textViewRowMainCafeName.text = cafeList[position].cafeName
        }
    }

    // FAB 클릭 시 InputFragment로 이동
    fun settingFAB() {
        fragmentShowAllBinding.fabMenuAdd.setOnClickListener {
            mainActivity.replaceFragment(FragmentName.INPUT_FRAGMENT, true, true, null)
        }
    }

    // 삭제 확인 대화상자
    private fun showDeleteConfirmationDialog() {
        val materialAlertDialogBuilder = MaterialAlertDialogBuilder(mainActivity)
        materialAlertDialogBuilder.setTitle("모두 삭제")
        materialAlertDialogBuilder.setMessage("모든 메뉴 정보를 삭제하시겠습니까?")
        materialAlertDialogBuilder.setNegativeButton("취소", null)
        materialAlertDialogBuilder.setPositiveButton("삭제") { _, _ ->
            deleteAllCafe()
        }
        materialAlertDialogBuilder.show()
    }

    private fun deleteAllCafe() {
        if (cafeList.isEmpty()) {
            Toast.makeText(mainActivity, "삭제할 데이터가 없습니다.", Toast.LENGTH_SHORT).show()
        } else {
            CoroutineScope(Dispatchers.Main).launch {
                val work1 = async(Dispatchers.IO) {
                    CafeRepository.deleteAllCafe(mainActivity)
                }
                work1.await()
                refreshRecyclerViewShowAll()
            }
        }
    }
}
