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

/*APPKAFA - Gestionar los comportamientos sobre cada uno de las notas de la lista*/
class NotasAdapter(
    context: Context, val listener: OnNoteSelected
) : RecyclerView.Adapter<NotasAdapter.NoteViewHolder>() {

    /*Prueba Commit*/
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var notes = emptyList<Note>() // Cached copy of notes

    override fun getItemCount() = notes.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val itemView = inflater.inflate(R.layout.note_row, parent, false)
        return NoteViewHolder(itemView)

        this.listener = listener
    }

    /*APPKAFA - Asignar los datos a cada una de las filas del recyclerView*/
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
        holder.txtTituloNota.setOnClickListener(View.OnClickListener { view ->

            val intent = Intent(view.context, ConsultaNotaActivity::class.java)
            intent.putExtra(Constantes.ACCION, Constantes.ACCION_CONSULTAR)
            intent.putExtra(Constantes.ID_NOTA, current.mId)
            startActivity(view.context, intent, null)
        })
        holder.txtnotaView.setOnClickListener(View.OnClickListener { view ->

            val intent = Intent(view.context, ConsultaNotaActivity::class.java)
            intent.putExtra(Constantes.ACCION, Constantes.ACCION_CONSULTAR)
            intent.putExtra(Constantes.ID_NOTA, current.mId)
            startActivity(view.context, intent, null)
        })

        holder.menuNota.setOnClickListener { view ->
            val popupMenu: PopupMenu = PopupMenu(view.context, holder.menuNota)
            popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->

                when (item.itemId) {
                    R.id.popmModificar -> {
                        val intent = Intent(view.context, NotaActivity::class.java)
                        intent.putExtra(Constantes.ACCION, Constantes.ACCION_EDITAR)
                        intent.putExtra(Constantes.ID_NOTA, current.mId)
                        startActivity(view.context, intent, null)
                    }
                    R.id.popmRecordatorio -> {
                        val intent = Intent(view.context, NotaActivity::class.java)
                        intent.putExtra(Constantes.ACCION, Constantes.ACCION_EDITAR_RECORDATORIO)
                        intent.putExtra(Constantes.ID_NOTA, current.mId)
                        startActivity(view.context, intent, null)

                    }
                    R.id.popmCompartir -> {
                        val share = Intent.createChooser(Intent().apply {

                            var cuerpoNota = "<Nota generada desde AppKAFA>";

                            if (current!!.titulo != null) {
                                cuerpoNota += "\n*Titulo:* " + current!!.titulo
                            }

                            if (current!!.nota != null) {
                                cuerpoNota += "\n*Nota:* " + current!!.nota
                            }

                            if (current!!.fechaRecordatorio != null) {
                                cuerpoNota += "\n*Recordatorio:* " + current!!.fechaRecordatorio
                            }

                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, cuerpoNota)
                            putExtra(Intent.EXTRA_TITLE, current!!.titulo)
                            setType("text/plain");
                            setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        }, null)
                        startActivity(view.context, share, null)

                    }
                    R.id.popmEliminar -> {
                        val intent = Intent(view.context, NotaActivity::class.java)
                        intent.putExtra(Constantes.ACCION, Constantes.ACCION_ELIMINAR)
                        intent.putExtra(Constantes.ID_NOTA, current.mId)
                        startActivity(view.context, intent, null)
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

