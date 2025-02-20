package com.example.finaltestphase2

import android.content.Context
import com.example.finaltestphase2.DateConverter.stringToDate
import com.example.finaltestphase2.database.DatabaseHelper
import com.example.finaltestphase2.model.Student
import com.example.finaltestphase2.model.Subjects
import org.json.JSONArray
import java.io.IOException

object JsonUtils {
    fun loadJsonFromAsset(context: Context, fileName: String): String? {
        return try {
            val inputStream = context.assets.open(fileName)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, Charsets.UTF_8)
        } catch (ex: IOException) {
            ex.printStackTrace()
            null
        }
    }

    fun parseJsonAndInsertToDB(context: Context) {
        val dbHelper = DatabaseHelper(context)

        // Kiểm tra nếu database chưa có dữ liệu thì mới insert
        if (dbHelper.isDatabaseEmpty()) {
            val jsonString = loadJsonFromAsset(context, "fresher.json")

            if (jsonString != null) {
                val jsonArray = JSONArray(jsonString)
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val studentID = jsonObject.getInt("studentID")
                    val firstName = jsonObject.getString("firstName")
                    val lastName = jsonObject.getString("lastName")
                    val dateOfBirth = jsonObject.getString("dateOfBirth")
                    val city = jsonObject.getString("city")
                    val phone = jsonObject.getString("phone")

                    val subjectsJsonArray = jsonObject.getJSONArray("subjects")
                    val subjectsList = mutableListOf<Subjects>()

                    for (j in 0 until subjectsJsonArray.length()) {
                        val subjectJson = subjectsJsonArray.getJSONObject(j)
                        val subjectName = subjectJson.getString("name")
                        val subjectScore = subjectJson.getInt("score")

                        subjectsList.add(Subjects(studentID, subjectName, subjectScore))
                    }

                    val student = Student(studentID, firstName, lastName, dateOfBirth, city, phone, subjectsList.toTypedArray())
                    dbHelper.insertStudent(student)
                }
            }
        }
    }
}
