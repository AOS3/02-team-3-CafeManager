package com.lion.cafemanager_1.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.lion.cafemanager_1.vo.CafeVO

@Database(entities = [CafeVO::class], version = 1, exportSchema = false)
abstract class CafeDatabase : RoomDatabase(){
    abstract fun cafeDAO() : CafeDAO

    companion object{
        var cafeDatabase:CafeDatabase? = null
        @Synchronized
        fun getInstance(context: Context) : CafeDatabase?{
            synchronized(CafeDatabase::class){
                cafeDatabase = Room.databaseBuilder(
                    context.applicationContext, CafeDatabase::class.java,
                    "Cafe.db"
                ).build()
            }
            return cafeDatabase
        }

        fun destroyInstance(){
            cafeDatabase = null
        }
    }
}
