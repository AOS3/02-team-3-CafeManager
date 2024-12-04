package com.lion.cafemanager_1.fragment

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lion.cafemanager_1.MainActivity
import com.lion.cafemanager_1.R
import com.lion.cafemanager_1.databinding.FragmentShowBinding
import com.lion.cafemanager_1.repository.CafeRepository
import com.lion.cafemanager_1.util.FragmentName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ShowFragment : Fragment() {

    lateinit var fragmentShowBinding: FragmentShowBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentShowBinding = FragmentShowBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        // Toolbar 설정 메서드 호출
        settingToolbar()

        // 데이터 가져와 출력
        settingTextView()

        return fragmentShowBinding.root
    }

    // 툴바 설정 메서드
    fun settingToolbar(){
        fragmentShowBinding.apply {
            // 타이틀
            toolbarShow.title = "카페 메뉴 정보 보기"
            // 네비게이션
            toolbarShow.setNavigationIcon(R.drawable.arrow_back_24px)
            toolbarShow.setNavigationOnClickListener {
                mainActivity.removeFragment(FragmentName.SHOW_FRAGMENT)
            }
            // 메뉴
            toolbarShow.inflateMenu(R.menu.toolbar_show_menu)
            toolbarShow.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.toolbar_show_menu_edit -> {
                        // 메뉴 번호 담기
                        val dataBundle = Bundle()
                        dataBundle.putInt("cafeIdx", arguments?.getInt("cafeIdx")!!)
                        // ModifyFragment로 이동
                        mainActivity.replaceFragment(FragmentName.MODIFY_FRAGMENT, true, true, dataBundle)
                    }
                    R.id.toolbar_show_menu_delete -> {
                        // 삭제를 위한 다이얼로그
                        deleteStudentInfo()
                    }
                }
                true
            }
        }
    }

    // TextView에 값을 설정하는 메서드
    fun settingTextView(){
        // TextView 초기화
        fragmentShowBinding.textViewType.text = ""
        fragmentShowBinding.textViewName.text = ""
        fragmentShowBinding.textViewPrice.text = ""
        fragmentShowBinding.textViewSize.text = ""
        fragmentShowBinding.textViewTakeOut.text = ""
        fragmentShowBinding.textViewStock.text = ""
        fragmentShowBinding.textViewExplain.text = ""
        // 카페 메뉴 번호 추출
        val cafeIdx = arguments?.getInt("cafeIdx")
        // 카페 메뉴 데이터 추출
        CoroutineScope(Dispatchers.Main).launch {
            val work1 = async(Dispatchers.IO){
                CafeRepository.selectCafeInfoByCafeIdx(mainActivity, cafeIdx!!)
            }
            val cafeViewModel = work1.await()

            fragmentShowBinding.textViewType.text = "메뉴 종류: ${cafeViewModel.cafeType.str}"
            fragmentShowBinding.textViewName.text = "메뉴 이름: ${cafeViewModel.cafeName}"
            fragmentShowBinding.textViewPrice.text = "메뉴 가격: ${cafeViewModel.cafePrice.toString()}원"
            fragmentShowBinding.textViewSize.text = "메뉴 사이즈: ${cafeViewModel.cafeSize.str}"
            fragmentShowBinding.textViewTakeOut.text = "메뉴 포장 가능 유무: ${cafeViewModel.cafeTakeOut.str}"
            fragmentShowBinding.textViewStock.text = "메뉴 재고 현황: ${cafeViewModel.cafeStock.toString()}개"
            fragmentShowBinding.textViewExplain.text = "메뉴 특이사항: ${cafeViewModel.cafeExplain}"
            cafeViewModel.cafeImage.let { uriString ->
                if (uriString.isNullOrEmpty()){
                    fragmentShowBinding.imageViewPicture.setImageResource(R.drawable.image_search_24px)
                } else {
                    val uri = Uri.parse(uriString)
                    fragmentShowBinding.imageViewPicture.setImageURI(uri)
                }
            }
        }
    }

    // 삭제처리 메서드
    fun deleteStudentInfo(){
        // 다이얼로그를 띄워주다.
        val materialAlertDialogBuilder = MaterialAlertDialogBuilder(mainActivity)
        materialAlertDialogBuilder.setTitle("삭제")
        materialAlertDialogBuilder.setMessage("삭제할 경우 복원이 불가능합니다")
        materialAlertDialogBuilder.setNeutralButton("취소", null)
        materialAlertDialogBuilder.setPositiveButton("삭제"){ dialogInterface: DialogInterface, i: Int ->
            CoroutineScope(Dispatchers.Main).launch {
                val work1 = async(Dispatchers.IO){
                    val cafeIdx = arguments?.getInt("cafeIdx")
                    CafeRepository.deleteCafeInfoByCafeIdx(mainActivity, cafeIdx!!)
                }
                work1.join()
                mainActivity.removeFragment(FragmentName.SHOW_FRAGMENT)
            }
        }
        materialAlertDialogBuilder.show()
    }
}