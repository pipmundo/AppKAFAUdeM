package com.notas.appkafa


import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.notas.appkafa.database.MyDatabase
import com.notas.appkafa.database.NotesDao

/**
 * A simple [Fragment] subclass.
 */


/*APPKAFA - Visualizar las notas que son eliminadas (enviadas a la papelera)*/
class EliminadasFragment : Fragment(), OnNoteSelected {

    private val ID = "_id"
    private lateinit var adapter: NotasEliminadasAdapter
    private lateinit var notasDao: NotesDao
    private var contexto:Context?=null

    override fun onNoteSelected(id: Int) {
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        contexto=context
        val database = MyDatabase.getInstance(context!!)
        notasDao = database.notesDao()

        val rootView = inflater.inflate(R.layout.fragment_eliminadas, container, false)
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.recyclerview)
        adapter = NotasEliminadasAdapter(context!!, this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        showNotes()

        return rootView
    }

/*APPKAFA - Cuando el fragment vuelve estar activo luego de que una actividad que habia sido llamada fue finalizada*/
override fun onResume() {
    super.onResume()
    showNotes()
}

fun showNotes() {
    val notes = notasDao?.getNotesEliminated()
    if (notes != null && notes.size>0) {
        adapter.setNotes(notes)
    }

}


}
