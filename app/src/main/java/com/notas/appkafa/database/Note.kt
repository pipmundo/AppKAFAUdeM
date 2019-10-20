package com.notas.appkafa.database

import android.text.format.DateFormat
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.text.SimpleDateFormat


@Entity(tableName = "notas")
class Note {

   @PrimaryKey
   @ColumnInfo(name = "_id")
   var mId: Int? = null
   var titulo: String? = ""
   var recordatorio: Boolean? = false
   var nota: String? = ""
   var color:String?=""
   var eliminada:Boolean?=false
   var fechaCreacion:String?=""
   var fechaModificacion:String?=""
   var fechaRecordatorio:String?=""
   var rutaImagen:String?=""


}
