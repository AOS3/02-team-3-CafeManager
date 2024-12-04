package com.lion.cafemanager_1.viewmodel

import com.lion.cafemanager_1.util.CafeSize
import com.lion.cafemanager_1.util.CafeTakeOut
import com.lion.cafemanager_1.util.CafeType

data class CafeViewModel(
    var cafeIdx: Int,
    var cafeType: CafeType,
    var cafeName: String,
    var cafePrice: Int,
    var cafeSize: CafeSize,
    var cafeTakeOut: CafeTakeOut,
    var cafeStock: Int,
    var cafeExplain: String,
    var cafeImage: String
)