package com.lion.cafemanager_1.vo

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lion.cafemanager_1.util.CafeSize
import com.lion.cafemanager_1.util.CafeTakeOut
import com.lion.cafemanager_1.util.CafeType

@Entity(tableName = "CafeTable")
data class CafeVO(
    @PrimaryKey(autoGenerate = true)
    var cafeIdx: Int = 0,
    var cafeType: Int = 0,
    var cafeName: String = "",
    var cafePrice: Int = 0,
    var cafeSize: Int = 0,
    var cafeTakeOut: Int = 0,
    var cafeStock: Int = 0,
    var cafeExplain: String = "",
    var cafeImage: String = ""
)
