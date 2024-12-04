package com.lion.cafemanager_1.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.lion.cafemanager_1.vo.CafeVO

@Dao
interface CafeDAO {

    // 카페 메뉴 정보 저장
    @Insert
    fun insertCafeData(cafeVO: CafeVO)

    // 카페 메뉴 정보를 가져오는 메서드
    @Query("""
        select * from CafeTable 
        order by cafeIdx desc""")
    fun selectCafeDataAll() : List<CafeVO>

    // 카페 메뉴 하나의 정보를 가져오는 메서드
    @Query("""
        select * from CafeTable
        where cafeIdx = :cafeIdx
    """)
    fun selectCafeDataByCafeIdx(cafeIdx:Int) : CafeVO

    // 카페 메뉴 하나의 정보를 삭제하는 메서드
    @Delete
    fun deleteCafeData(cafeVO: CafeVO)

//    @Query("delete from CafeTable where cafeIdx = :cafeIdx")
//    fun deletecafeData(cafeIdx:Int)

    // 카페 메뉴 하나의 정보를 수정하는 메서드
    @Update
    fun updateCafeData(cafeVO: CafeVO)

    // 메뉴 테이블의 모든 데이터를 삭제하는 메서드
    @Query("DELETE FROM CafeTable")
    fun deleteAllCafeData()

    // 카페 메뉴랑 같은 이름을 가져오는 메서드
    @Query("""
        select * from CafeTable
        where cafeName = :cafeName
        order by cafeIdx desc
    """)
    fun selectCafeDataAllByCafeName(cafeName:String):List<CafeVO>

    // 카페 메뉴의 타입으로 필터링하여 가져오는 메서드
    @Query("""
        select * from CafeTable 
        where cafeType = :cafeType
        order by cafeIdx desc
    """)
    fun selectCafeDataByType(cafeType: Int): List<CafeVO> // 'cafeType'에 해당하는 데이터만 반환
}
