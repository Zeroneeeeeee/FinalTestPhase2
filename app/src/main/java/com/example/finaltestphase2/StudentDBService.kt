package com.example.finaltestphase2

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.finaltestphase2.database.DatabaseHelper
import com.example.finaltestphase2.common.Student


class StudentDBService: Service() {
    val database: DatabaseHelper by lazy {
        DatabaseHelper(applicationContext)
    }

    // Cung cấp giao diện AIDL cho Activity
    private val binder = object :IStudentDB.Stub(){

        override fun get100Students(limit:Int,offset:Int): MutableList<Student> {
            return database.get100Students().toMutableList()
        }

        override fun getTop10StudentByNameAndScore(subjectName: String): MutableList<Student> {
            return database.getTop10StudentByNameAndScore(subjectName).toMutableList()
        }

        override fun getTop10StudentsByScoreA(city: String): MutableList<Student> {
            return database.getTop10StudentsByScoreA(city).toMutableList()
        }

        override fun getTop10StudentsByScoreB(city: String): MutableList<Student> {
            return database.getTop10StudentsByScoreB(city).toMutableList()
        }

    }
    override fun onBind(p0: Intent?): IBinder {
        Log.d("Binder","Bind success")
        return binder
    }
}