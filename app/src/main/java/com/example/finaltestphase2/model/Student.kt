package com.example.finaltestphase2.model

import java.util.Date

class Student(
    val studentId:Int = 0,
    val firstName:String ,
    val lastName:String,
    var dateOfBirth:String,
    val city:String,
    val phone:String,
    val subjects: Array<Subjects>
) {
}