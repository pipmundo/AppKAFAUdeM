package com.notas.appkafa


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.notas.appkafa.database.MyDatabase
import com.notas.appkafa.database.NotesDao

/**
 * A simple [Fragment] subclass.
 */

const val ID = "_id"

class NotesFragment : Fragment(), OnNoteSelected {

    override fun onNoteSelected(id: Int) {

    }

    private lateinit var adapter: NotasAdapter
    private lateinit var notasDao: NotesDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val database = MyDatabase.getInstance(context!!)
        notasDao = database.notesDao()


        val rootView = inflater.inflate(R.layout.fragment_notes, container, false)
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.recyclerview)
        adapter = NotasAdapter(context!!, this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        showNotes()
        return rootView
    }

    override fun onResume() {
        super.onResume()
        showNotes()
    }

    fun showNotes() {
        val notes = notasDao?.getNotes()
        if (notes != null) {
            adapter.setNotes(notes)
        }

    }


}
