package com.example.finaltestphase2.common

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class Subjects(
    val studentId:Int,
    val name:String,
    val score:Int
):Parcelable {
    constructor(parcel: Parcel) : this(
        studentId = parcel.readInt(),
        name = parcel.readString()?: "",
        score = parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(studentId)
        parcel.writeString(name)
        parcel.writeInt(score)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Subjects> {
        override fun createFromParcel(parcel: Parcel): Subjects {
            return Subjects(parcel)
        }

        override fun newArray(size: Int): Array<Subjects?> {
            return arrayOfNulls(size)
        }
    }

}