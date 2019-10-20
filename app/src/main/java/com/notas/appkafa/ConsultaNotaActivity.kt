package com.notas.appkafa

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.notas.appkafa.database.MyDatabase
import com.notas.appkafa.database.Note
import com.notas.appkafa.database.NotesDao
import com.notas.appkafa.tools.Constantes
import kotlinx.android.synthetic.main.activity_consulta_nota.*
import kotlinx.android.synthetic.main.note_row.*
import java.io.File

class ConsultaNotaActivity : AppCompatActivity() {

    private lateinit var txtNota: TextView
    private lateinit var imgNota: ImageView
    private lateinit var txtTituloNota: TextView
    private lateinit var txtRecordatorio: TextView


    private lateinit var notesDao: NotesDao
    var note: Note? = null

    private var idNota: Int? = null

    override fun onResume() {
        super.onResume()
        try {
            consultarNota(idNota!!)
        } catch (ex: Exception) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consulta_nota)
        setSupportActionBar(toolbar)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)

        txtNota = findViewById(R.id.txtNota)
        imgNota = findViewById(R.id.imgNota)
        txtTituloNota = findViewById(R.id.tituloNota)
        txtRecordatorio = findViewById(R.id.txtRecordatorio)

        val database = MyDatabase.getInstance(this)
        notesDao = database.notesDao()

/*APPKAFA - Realizar la validacion si existe un Intent con datos a capturar para realizar la accion*/
        try {
            if (getIntent() != null && intent != null && intent.extras!!.containsKey(Constantes.ACCION) && intent.getStringExtra(
                    Constantes.ACCION
                ) != null
            ) {
/*APPKAFA - Verificar si es una operación soportada*/
                val accion: String? = intent.getStringExtra(Constantes.ACCION);
                when (accion) {

                    Constantes.ACCION_CONSULTAR -> {
                        idNota = intent.getIntExtra(Constantes.ID_NOTA, -1)
                    }
                    else -> {
                        Toast.makeText(
                            applicationContext,
                            "${accion}: No soportada!!!", Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            } else {
                idNota = -1
            }
        } catch (ex: Exception) {/*APPKAFA - Control de errores para no afectar el flujo de la aplicación en caso de no llevar un dato*/
            idNota = -1
        }
    }

    /*APPKAFA - Consultar el detalle de la nota que ha sido enviada en el intent de la peticion*/
    fun consultarNota(idNota: Int): String {

        var tituloNota: String? = "-"
        if (idNota != -1) {
            note = notesDao.getNote(idNota!!)

            if (note != null) {

                if (note!!.titulo != null) {
                    txtTituloNota.text = note!!.titulo
                }
                txtNota.setText(note!!.nota)
                txtRecordatorio.text = note!!.fechaRecordatorio

                if (note!!.color != null) {
                    txtNota?.setBackgroundColor(note!!.color!!.toInt())
                }
                tituloNota = note!!.titulo


                if (note!!.rutaImagen != null) {
/*APPKAFA - Transformar imagen fisica para ser mostrada en el ImageView de la activity de consulta*/
                    var bitmap: Bitmap = BitmapFactory.decodeFile(note!!.rutaImagen)
                    imgNota.setImageBitmap(bitmap)
                }

            }
        }
        return tituloNota!!
    }

    /*APPKAFA - AL presionar el botón cancelar de la vista*/
    fun cancelar(view: View) {
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home) {
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return true
    }

    /*APPKAFA - Al presionar botón de modificar enviar a la Actividad en cargada de Crear, Actualizar y eliminar una nota*/
    fun modificar(view: View) {

        val intent = Intent(view.context, NotaActivity::class.java)

        intent.putExtra(Constantes.ACCION, Constantes.ACCION_EDITAR)
        intent.putExtra(Constantes.ID_NOTA, idNota)
        ContextCompat.startActivity(view.context, intent, null)

    }
}
