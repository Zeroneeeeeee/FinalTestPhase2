package com.example.finaltestphase2.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.finaltestphase2.common.Student
import com.example.finaltestphase2.common.Subjects

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "students.db"
        private const val DATABASE_VERSION = 1

        // Bảng student
        private const val TABLE_STUDENT = "student"
        private const val COLUMN_STUDENT_ID = "studentID"
        private const val COLUMN_FIRST_NAME = "firstName"
        private const val COLUMN_LAST_NAME = "lastName"
        private const val COLUMN_DATE_OF_BIRTH = "dateOfBirth"
        private const val COLUMN_CITY = "city"
        private const val COLUMN_PHONE = "phone"

        // Bảng subject
        private const val TABLE_SUBJECT = "subject"
        private const val COLUMN_SUBJECT_ID = "id"
        private const val COLUMN_SUBJECT_NAME = "name"
        private const val COLUMN_SCORE = "score"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createStudentTable = """
            CREATE TABLE $TABLE_STUDENT (
                $COLUMN_STUDENT_ID INTEGER PRIMARY KEY,
                $COLUMN_FIRST_NAME TEXT,
                $COLUMN_LAST_NAME TEXT,
                $COLUMN_DATE_OF_BIRTH TEXT,
                $COLUMN_CITY TEXT,
                $COLUMN_PHONE TEXT
            )
        """.trimIndent()

        val createSubjectTable = """
            CREATE TABLE $TABLE_SUBJECT (
                $COLUMN_SUBJECT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_STUDENT_ID INTEGER,
                $COLUMN_SUBJECT_NAME TEXT,
                $COLUMN_SCORE INTEGER,
                FOREIGN KEY ($COLUMN_STUDENT_ID) REFERENCES $TABLE_STUDENT($COLUMN_STUDENT_ID)
            )
        """.trimIndent()

        db.execSQL(createStudentTable)
        db.execSQL(createSubjectTable)
    }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SUBJECT")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_STUDENT")
        onCreate(db)
    }

    fun insertStudent(student: Student) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_STUDENT_ID, student.studentId)
            put(COLUMN_FIRST_NAME, student.firstName)
            put(COLUMN_LAST_NAME, student.lastName)
            put(COLUMN_DATE_OF_BIRTH, student.dateOfBirth)
            put(COLUMN_CITY, student.city)
            put(COLUMN_PHONE, student.phone)
        }
        db.insert(TABLE_STUDENT, null, values)

        for (subject in student.subjects) {
            insertSubject(subject)
        }

        db.close()
    }

    fun insertSubject(subject: Subjects) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_STUDENT_ID, subject.studentId)
            put(COLUMN_SUBJECT_NAME, subject.name)
            put(COLUMN_SCORE, subject.score)
        }
        db.insert(TABLE_SUBJECT, null, values)
        db.close()
    }

    fun getSubjectsByStudentID(studentID: Int): List<Subjects> {
        val subjects = mutableListOf<Subjects>()
        val db = readableDatabase
        val cursor =
            db.rawQuery("SELECT * FROM subject WHERE studentID = ?", arrayOf(studentID.toString()))

        if (cursor.moveToFirst()) {
            do {
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                val score = cursor.getInt(cursor.getColumnIndexOrThrow("score"))
                subjects.add(Subjects(studentID, name, score))
            } while (cursor.moveToNext())
        }

        cursor.close()
        //db.close()
        return subjects
    }

    fun get100Students(limit: Int = 100, offset: Int = 0): List<Student> {
        val students = mutableListOf<Student>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Student LIMIT ? OFFSET ?", arrayOf(limit.toString(), offset.toString()))

        if (cursor.moveToFirst()) {
            do {
                val studentID = cursor.getInt(cursor.getColumnIndexOrThrow("studentID"))
                val firstName = cursor.getString(cursor.getColumnIndexOrThrow("firstName"))
                val lastName = cursor.getString(cursor.getColumnIndexOrThrow("lastName"))
                val dateOfBirth = cursor.getString(cursor.getColumnIndexOrThrow("dateOfBirth"))
                val city = cursor.getString(cursor.getColumnIndexOrThrow("city"))
                val phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"))

                // Lấy danh sách môn học cho student này
                val subjects = getSubjectsByStudentID(studentID)

                students.add(
                    Student(
                        studentID,
                        firstName,
                        lastName,
                        dateOfBirth,
                        city,
                        phone,
                        subjects
                    )
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        //db.close()
        return students
    }

    fun getTop10StudentByNameAndScore(subjectName:String):List<Student>{
        val students = mutableListOf<Student>()
        val db = readableDatabase
        val query = """
        SELECT s.studentId, s.firstName, s.lastName, s.dateOfBirth, s.city, s.phone, sub.name, MAX(sub.score) as max_score
        FROM Student s
        JOIN Subject sub ON s.studentID = sub.studentID
        WHERE sub.name = ?
        GROUP BY s.studentID
        ORDER BY sub.score DESC,s.firstName ASC
        LIMIT 10;
    """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(subjectName))
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_ID))
            val subjectName = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            val maxScore = cursor.getInt(cursor.getColumnIndexOrThrow("max_score"))

            // Giả sử mỗi sinh viên chỉ có một môn học với điểm số cao nhất
            val subjects = listOf(Subjects(id, subjectName, maxScore))// Lấy danh sách môn học của học sinh
            val student = Student(
                studentId = cursor.getInt(0),
                firstName = cursor.getString(1),
                lastName = cursor.getString(2),
                dateOfBirth = cursor.getString(3),
                city = cursor.getString(4),
                phone = cursor.getString(5),
                subjects)
            students.add(student)
        }
        cursor.close()
        db.close()

        return students
    }

    fun getTop10StudentsByScoreA(city:String): List<Student> {
        val students = mutableListOf<Student>()
        val db = readableDatabase

        val query = """
        SELECT s.studentID, s.firstName, s.lastName, s.dateOfBirth, s.city, s.phone, 
               SUM(CASE WHEN sub.name = 'Math' THEN sub.score ELSE 0 END) +
               SUM(CASE WHEN sub.name = 'Physics' THEN sub.score ELSE 0 END) +
               SUM(CASE WHEN sub.name = 'Chemistry' THEN sub.score ELSE 0 END) AS totalScore
        FROM Student s
        JOIN Subject sub ON s.studentID = sub.studentID
        WHERE s.city = ?
        GROUP BY s.studentID
        ORDER BY totalScore DESC
        LIMIT 10;
    """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(city))
        while (cursor.moveToNext()) {
            val student = Student(
                studentId = cursor.getInt(0),
                firstName = cursor.getString(1),
                lastName = cursor.getString(2),
                dateOfBirth = cursor.getString(3),
                city = cursor.getString(4),
                phone = cursor.getString(5),
                subjects = getSubjectsByStudentID(cursor.getInt(0)) // Lấy danh sách môn học của học sinh
            )
            students.add(student)
        }
        cursor.close()
        db.close()

        return students
    }

    fun getTop10StudentsByScoreB(city:String): List<Student> {
        val students = mutableListOf<Student>()
        val db = readableDatabase

        val query = """
        SELECT s.studentID, s.firstName, s.lastName, s.dateOfBirth, s.city, s.phone, 
               SUM(CASE WHEN sub.name = 'English' THEN sub.score ELSE 0 END) +
               SUM(CASE WHEN sub.name = 'Biology' THEN sub.score ELSE 0 END) +
               SUM(CASE WHEN sub.name = 'Math' THEN sub.score ELSE 0 END) AS totalScore
        FROM Student s
        JOIN Subject sub ON s.studentID = sub.studentID
        WHERE s.city = ?
        GROUP BY s.studentID
        ORDER BY totalScore DESC
        LIMIT 10;
    """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(city))
        while (cursor.moveToNext()) {
            val student = Student(
                studentId = cursor.getInt(0),
                firstName = cursor.getString(1),
                lastName = cursor.getString(2),
                dateOfBirth = cursor.getString(3),
                city = cursor.getString(4),
                phone = cursor.getString(5),
                subjects = getSubjectsByStudentID(cursor.getInt(0)) // Lấy danh sách môn học của học sinh
            )
            students.add(student)
        }
        cursor.close()
        db.close()

        return students
    }



    fun isDatabaseEmpty(): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM Student", null)
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        //db.close()
        return count == 0 // Trả về true nếu chưa có dữ liệu
    }


}