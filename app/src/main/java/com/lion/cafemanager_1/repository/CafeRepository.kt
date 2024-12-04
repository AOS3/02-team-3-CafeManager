package com.lion.cafemanager_1.repository

import android.content.Context
import com.lion.cafemanager_1.dao.CafeDatabase
import com.lion.cafemanager_1.util.CafeSize
import com.lion.cafemanager_1.util.CafeTakeOut
import com.lion.cafemanager_1.util.CafeType
import com.lion.cafemanager_1.util.numberToCafeSize
import com.lion.cafemanager_1.util.numberToCafeTakeOut
import com.lion.cafemanager_1.util.numberToCafeType
import com.lion.cafemanager_1.viewmodel.CafeViewModel
import com.lion.cafemanager_1.vo.CafeVO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CafeRepository {

    companion object {

        // 카페 메뉴 정보를 저장하는 메서드
        fun insertCafeInfo(context: Context, cafeViewModel: CafeViewModel) {
            val cafeDatabase = CafeDatabase.getInstance(context)

            val cafeType = cafeViewModel.cafeType.number
            val cafeName = cafeViewModel.cafeName
            val cafePrice = cafeViewModel.cafePrice
            val cafeSize = cafeViewModel.cafeSize.number
            val cafeTakeOut = cafeViewModel.cafeTakeOut.number
            val cafeStock = cafeViewModel.cafeStock
            val cafeExplain = cafeViewModel.cafeExplain
            val cafeImage = cafeViewModel.cafeImage

            // Image URI를 포함하여 VO 생성
            val cafeVO = CafeVO(
                cafeType = cafeType,
                cafeName = cafeName,
                cafePrice = cafePrice,
                cafeSize = cafeSize,
                cafeTakeOut = cafeTakeOut,
                cafeStock = cafeStock,
                cafeExplain = cafeExplain,
                cafeImage = cafeImage
            )

            cafeDatabase?.cafeDAO()?.insertCafeData(cafeVO)
        }

        // 카페 메뉴 정보 전체를 가져오는 메서드
        fun selectCafeInfoAll(context: Context): MutableList<CafeViewModel> {
            val cafeDatabase = CafeDatabase.getInstance(context)
            val cafeVoList = cafeDatabase?.cafeDAO()?.selectCafeDataAll()
            val cafeViewModelList = mutableListOf<CafeViewModel>()

            cafeVoList?.forEach {
                val cafeType = when (it.cafeType) {
                    CafeType.CAFE_TYPE_COFFEE.number -> CafeType.CAFE_TYPE_COFFEE
                    CafeType.CAFE_TYPE_NONCOFFEE.number -> CafeType.CAFE_TYPE_NONCOFFEE
                    CafeType.CAFE_TYPE_BREAD.number -> CafeType.CAFE_TYPE_BREAD
                    else -> CafeType.CAFE_TYPE_CAKE
                }

                val cafeName = it.cafeName
                val cafePrice = it.cafePrice
                val cafeSize = when (it.cafeSize) {
                    CafeSize.CAFE_SIZE_LARGE.number -> CafeSize.CAFE_SIZE_LARGE
                    CafeSize.CAFE_SIZE_MEDIUM.number -> CafeSize.CAFE_SIZE_MEDIUM
                    CafeSize.CAFE_SIZE_SMALL.number -> CafeSize.CAFE_SIZE_SMALL
                    else -> CafeSize.CAFE_SIZE_NO
                }

                val cafeTakeOut = when(it.cafeTakeOut) {
                    CafeTakeOut.CAFE_TAKEOUT_OK.number -> CafeTakeOut.CAFE_TAKEOUT_OK
                    else -> CafeTakeOut.CAFE_TAKEOUT_NO
                }
                val cafeStock = it.cafeStock
                val cafeExplain = it.cafeExplain
                val cafeIdx = it.cafeIdx
                val cafeImage = it.cafeImage

                val cafeViewModel = CafeViewModel(cafeIdx, cafeType, cafeName, cafePrice, cafeSize,
                    cafeTakeOut, cafeStock, cafeExplain, cafeImage)
                cafeViewModelList.add(cafeViewModel)
            }

            return cafeViewModelList
        }

        // 카페 메뉴 하나의 정보를 가져오는 메서드
        fun selectCafeInfoByCafeIdx(context: Context, cafeIdx: Int): CafeViewModel {
            val cafeDatabase = CafeDatabase.getInstance(context)
            val cafeVO = cafeDatabase?.cafeDAO()?.selectCafeDataByCafeIdx(cafeIdx)

            val cafeType = when (cafeVO?.cafeType) {
                CafeType.CAFE_TYPE_COFFEE.number -> CafeType.CAFE_TYPE_COFFEE
                CafeType.CAFE_TYPE_NONCOFFEE.number -> CafeType.CAFE_TYPE_NONCOFFEE
                CafeType.CAFE_TYPE_BREAD.number -> CafeType.CAFE_TYPE_BREAD
                else -> CafeType.CAFE_TYPE_CAKE
            }
            val cafeName = cafeVO?.cafeName
            val cafePrice = cafeVO?.cafePrice
            val cafeSize = when (cafeVO?.cafeSize) {
                CafeSize.CAFE_SIZE_LARGE.number -> CafeSize.CAFE_SIZE_LARGE
                CafeSize.CAFE_SIZE_MEDIUM.number -> CafeSize.CAFE_SIZE_MEDIUM
                CafeSize.CAFE_SIZE_SMALL.number -> CafeSize.CAFE_SIZE_SMALL
                else -> CafeSize.CAFE_SIZE_NO
            }

            val cafeTakeOut = when (cafeVO?.cafeTakeOut) {
                CafeTakeOut.CAFE_TAKEOUT_OK.number -> CafeTakeOut.CAFE_TAKEOUT_OK
                else -> CafeTakeOut.CAFE_TAKEOUT_NO
            }
            val cafeStock = cafeVO?.cafeStock
            val cafeExplain = cafeVO?.cafeExplain
            val cafeImage = cafeVO?.cafeImage

            val cafeViewModel = CafeViewModel(cafeIdx, cafeType, cafeName!!, cafePrice!!, cafeSize,
                cafeTakeOut, cafeStock!!, cafeExplain!!, cafeImage!!)

            return cafeViewModel
        }

        // 카페 메뉴 정보 삭제하는 메서드
        fun deleteCafeInfoByCafeIdx(context: Context, cafeIdx: Int) {
            val cafeDatabase = CafeDatabase.getInstance(context)
            val cafeVO = CafeVO(cafeIdx = cafeIdx)
            cafeDatabase?.cafeDAO()?.deleteCafeData(cafeVO)
        }

        // 카페 메뉴 정보 수정하는 메서드
        fun updateCafeInfo(context: Context, cafeViewModel: CafeViewModel) {
            val cafeDatabase = CafeDatabase.getInstance(context)

            val cafeIdx = cafeViewModel.cafeIdx
            val cafeType = cafeViewModel.cafeType.number
            val cafeName = cafeViewModel.cafeName
            val cafePrice = cafeViewModel.cafePrice
            val cafeSize = cafeViewModel.cafeSize.number
            val cafeTakeOut = cafeViewModel.cafeTakeOut.number
            val cafeStock = cafeViewModel.cafeStock
            val cafeExplain = cafeViewModel.cafeExplain
            val cafeImage = cafeViewModel.cafeImage

            // VO 생성
            val cafeVO = CafeVO(
                cafeIdx = cafeIdx,
                cafeType = cafeType,
                cafeName = cafeName,
                cafePrice = cafePrice,
                cafeSize = cafeSize,
                cafeTakeOut = cafeTakeOut,
                cafeStock = cafeStock,
                cafeExplain = cafeExplain,
                cafeImage = cafeImage
            )

            // 수정
            cafeDatabase?.cafeDAO()?.updateCafeData(cafeVO)
        }

        // Repository에서 모든 카페 데이터를 삭제하는 메서드 추가
        fun deleteAllCafe(context: Context) {
            val cafeDatabase = CafeDatabase.getInstance(context)
            cafeDatabase?.cafeDAO()?.deleteAllCafeData()
        }

        // 카페 메뉴 이름으로 검색하여 학생 데이터 전체를 가져오는 메서드
        fun selectCafeDataAllByCafeName(context: Context, cafeName: String) : MutableList<CafeViewModel>{
            // 데이터를 가져온다.
            val cafeDatabase = CafeDatabase.getInstance(context)
            val cafeList = cafeDatabase?.cafeDAO()?.selectCafeDataAllByCafeName(cafeName)

             // 카페 메뉴 데이터를 담을 리스트
            val tempList = mutableListOf<CafeViewModel>()

            // 카페 메뉴 수만큼 반복
            cafeList?.forEach {
                val cafeViewModel = CafeViewModel(
                    it.cafeIdx, numberToCafeType(it.cafeType), it.cafeName, it.cafePrice, numberToCafeSize(it.cafeSize),
                    numberToCafeTakeOut(it.cafeTakeOut), it.cafeStock, it.cafeExplain, it.cafeImage)
                // 리스트에 담기
                tempList.add(cafeViewModel)
            }
            return tempList
        }

        // 빵 메뉴만 가져오기
        suspend fun selectBreadMenu(context: Context): MutableList<CafeViewModel> {
            return withContext(Dispatchers.IO) {
                val cafeDatabase = CafeDatabase.getInstance(context)
                val cafeVoList = cafeDatabase?.cafeDAO()?.selectCafeDataAll()
                val breadMenuList = mutableListOf<CafeViewModel>()

                cafeVoList?.forEach {
                    if (it.cafeType == CafeType.CAFE_TYPE_BREAD.number) {
                        val cafeViewModel = CafeViewModel(
                            cafeIdx = it.cafeIdx,
                            cafeType = CafeType.CAFE_TYPE_BREAD,
                            cafeName = it.cafeName,
                            cafePrice = it.cafePrice,
                            cafeSize = numberToCafeSize(it.cafeSize),
                            cafeTakeOut = numberToCafeTakeOut(it.cafeTakeOut),
                            cafeStock = it.cafeStock,
                            cafeExplain = it.cafeExplain,
                            cafeImage = it.cafeImage
                        )
                        breadMenuList.add(cafeViewModel)
                    }
                }
                breadMenuList
            }
        }

        suspend fun selectCakeMenu(context: Context): MutableList<CafeViewModel> {
            return withContext(Dispatchers.IO) {
                val cafeDatabase = CafeDatabase.getInstance(context)
                val cafeVoList = cafeDatabase?.cafeDAO()?.selectCafeDataAll()
                val cakeMenuList = mutableListOf<CafeViewModel>()

                cafeVoList?.forEach {
                    if (it.cafeType == CafeType.CAFE_TYPE_CAKE.number) {
                        val cafeViewModel = CafeViewModel(
                            cafeIdx = it.cafeIdx,
                            cafeType = CafeType.CAFE_TYPE_CAKE,
                            cafeName = it.cafeName,
                            cafePrice = it.cafePrice,
                            cafeSize = numberToCafeSize(it.cafeSize),
                            cafeTakeOut = numberToCafeTakeOut(it.cafeTakeOut),
                            cafeStock = it.cafeStock,
                            cafeExplain = it.cafeExplain,
                            cafeImage = it.cafeImage
                        )
                        cakeMenuList.add(cafeViewModel)
                    }
                }
                cakeMenuList
            }
        }

        suspend fun selectCoffeeMenu(context: Context): MutableList<CafeViewModel> {
            return withContext(Dispatchers.IO) {
                val cafeDatabase = CafeDatabase.getInstance(context)
                val cafeVoList = cafeDatabase?.cafeDAO()?.selectCafeDataAll()
                val coffeeMenuList = mutableListOf<CafeViewModel>()

                cafeVoList?.forEach {
                    if (it.cafeType == CafeType.CAFE_TYPE_COFFEE.number) {
                        val cafeViewModel = CafeViewModel(
                            cafeIdx = it.cafeIdx,
                            cafeType = CafeType.CAFE_TYPE_COFFEE,
                            cafeName = it.cafeName,
                            cafePrice = it.cafePrice,
                            cafeSize = numberToCafeSize(it.cafeSize),
                            cafeTakeOut = numberToCafeTakeOut(it.cafeTakeOut),
                            cafeStock = it.cafeStock,
                            cafeExplain = it.cafeExplain,
                            cafeImage = it.cafeImage
                        )
                        coffeeMenuList.add(cafeViewModel)
                    }
                }
                coffeeMenuList
            }
        }

        suspend fun selectNonCoffeeMenu(context: Context): MutableList<CafeViewModel> {
            return withContext(Dispatchers.IO) {
                val cafeDatabase = CafeDatabase.getInstance(context)
                val cafeVoList = cafeDatabase?.cafeDAO()?.selectCafeDataAll()
                val nonCoffeeMenuList = mutableListOf<CafeViewModel>()

                cafeVoList?.forEach {
                    if (it.cafeType == CafeType.CAFE_TYPE_NONCOFFEE.number) {
                        val cafeViewModel = CafeViewModel(
                            cafeIdx = it.cafeIdx,
                            cafeType = CafeType.CAFE_TYPE_NONCOFFEE,
                            cafeName = it.cafeName,
                            cafePrice = it.cafePrice,
                            cafeSize = numberToCafeSize(it.cafeSize),
                            cafeTakeOut = numberToCafeTakeOut(it.cafeTakeOut),
                            cafeStock = it.cafeStock,
                            cafeExplain = it.cafeExplain,
                            cafeImage = it.cafeImage
                        )
                        nonCoffeeMenuList.add(cafeViewModel)
                    }
                }
                nonCoffeeMenuList
            }
        }
    }
}
