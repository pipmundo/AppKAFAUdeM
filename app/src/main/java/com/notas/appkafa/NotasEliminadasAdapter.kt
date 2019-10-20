package com.notas.appkafa

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.notas.appkafa.database.Note
import com.notas.appkafa.tools.Constantes
import android.R.attr.fragment
import androidx.core.content.ContextCompat

/*APPKAFA - Comportamiento de cada fila de la papelera*/
class NotasEliminadasAdapter(
    context: Context, val listener: OnNoteSelected
) : RecyclerView.Adapter<NotasEliminadasAdapter.NoteViewHolder>() {


    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var notes = emptyList<Note>()

    override fun getItemCount() = notes.size
    private var notaSeleccionada: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val itemView = inflater.inflate(R.layout.note_row, parent, false)
        return NoteViewHolder(itemView)

        this.listener = listener
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val current = notes[position]
        holder.txtTituloNota.text = current.titulo

        if (current.nota!!.length > 20) {
            current.nota = current.nota!!.substring(0, 19)
        }
        holder.txtnotaView.text = current.nota!!.toString()

        if (!current.recordatorio!!) {
            holder.reminderView.visibility = View.INVISIBLE
        } else {
            holder.reminderView.visibility = View.VISIBLE
        }
        if (current.fechaRecordatorio != null) {
            holder.txtRecordatorio.text = current.fechaRecordatorio!!.toString()
        } else {
            holder.txtRecordatorio.text = "-"
        }

        holder.menuNota.setOnClickListener { view ->
            val popupMenu: PopupMenu = PopupMenu(view.context, holder.menuNota)
            popupMenu.menuInflater.inflate(R.menu.popup_menu_papelera, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->

                notaSeleccionada = indiceNota(current.mId!!)
/*APPKAFA - Crear el respectivo Intent con la accion sobre la nota, para que dicho compoartamiento sea ejecutado por NotaActivity*/
                when (item.itemId) {
                    R.id.popmEliminar -> {
                        val intent = Intent(view.context, NotaActivity::class.java)
                        intent.putExtra(Constantes.ACCION, Constantes.ACCION_ELIMINAR_DEFINITIVO)
                        intent.putExtra(Constantes.ID_NOTA, current.mId!!)
                        startActivity(view.context, intent, null)
                        holder.miFila.visibility = View.INVISIBLE
                    }
                    R.id.popmRecuperar -> {
                        val intent = Intent(view.context, NotaActivity::class.java)
                        intent.putExtra(Constantes.ACCION, Constantes.ACCION_RECUPERAR)
                        intent.putExtra(Constantes.ID_NOTA, current.mId!!)
                        startActivity(view.context, intent, null)
                        holder.miFila.visibility = View.INVISIBLE
                    }
                }
                true
            })
            popupMenu.show()
        }
        if (current.color != null) {
            holder.miFila.setBackgroundColor(current.color!!.toInt())
        }
    }


    fun indiceNota(idNota: Int): Int {
        var indice: Int = 0
        (notes).forEach({ note ->
            if (note!!.mId == idNota) {
                notaSeleccionada = indice
            } else {

            }
            indice++
        })

        return notaSeleccionada!!
    }


    fun setNotes(notes: List<Note>) {
        this.notes = notes
        notifyDataSetChanged()
    }


    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtTituloNota: TextView = itemView.findViewById(R.id.txtTituloNota)
        val reminderView: ImageView = itemView.findViewById(R.id.reminder)
        val txtnotaView: TextView = itemView.findViewById(R.id.txtNota)
        var miFila: RelativeLayout = itemView.findViewById(R.id.miFila)
        var menuNota: TextView = itemView.findViewById(R.id.menuNota)
        var txtRecordatorio: TextView = itemView.findViewById(R.id.txtRecordatorio)


    }

}

