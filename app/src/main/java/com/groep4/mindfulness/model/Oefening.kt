package com.groep4.mindfulness.model

import android.os.Parcel
import android.os.Parcelable
import android.util.Log

class Oefening : Parcelable {
    var id: String
    var sessieId: String
    var naam: String
    var beschrijving: String
    var groepen: String
    var url: String


    // File
    var fileMimeType: String


    constructor(id: String, naam: String, beschrijving: String, sId: String, mimeType: String, groepen: String, url: String){
        this.id = id
        this.naam = naam
        this.beschrijving = beschrijving
        this.sessieId = sId
        this.fileMimeType = mimeType
        this.groepen = groepen
        this.url = url
    }

    private constructor(parcel: Parcel) {
        this.id = parcel.readString()
        this.sessieId = parcel.readString()
        this.naam = parcel.readString().orEmpty()
        this.beschrijving = parcel.readString().orEmpty()
        this.fileMimeType = parcel.readString().orEmpty()
        this.groepen = parcel.readString().orEmpty()
        this.url = parcel.readString().orEmpty()
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(id)
        dest?.writeString(naam)
        dest?.writeString(beschrijving)
        dest?.writeString(sessieId)
        dest?.writeString(fileMimeType)
        dest?.writeString(groepen)
        dest?.writeString(url)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Oefening> {
        override fun createFromParcel(parcel: Parcel): Oefening {
            return Oefening(parcel)
        }

        override fun newArray(size: Int): Array<Oefening?> {
            return arrayOfNulls(size)
        }
    }
}