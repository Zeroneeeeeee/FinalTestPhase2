package com.example.finaltestphase2.common

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize


data class Student(
    val studentId:Int = 0,
    val firstName:String ,
    val lastName:String,
    var dateOfBirth:String,
    val city:String,
    val phone:String,
    val subjects: List<Subjects>
):Parcelable {
    constructor(parcel: Parcel) : this(
        studentId = parcel.readInt(),
        firstName = parcel.readString() ?: "",
        lastName = parcel.readString() ?: "",
        dateOfBirth = parcel.readString() ?: "",
        city = parcel.readString() ?: "",
        phone = parcel.readString() ?: "",
        subjects = parcel.createTypedArrayList(Subjects) ?: emptyList()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(studentId)
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeString(dateOfBirth)
        parcel.writeString(city)
        parcel.writeString(phone)
        parcel.writeTypedList(subjects)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Student> {
        override fun createFromParcel(parcel: Parcel): Student {
            return Student(parcel)
        }

        override fun newArray(size: Int): Array<Student?> {
            return arrayOfNulls(size)
        }
    }
}