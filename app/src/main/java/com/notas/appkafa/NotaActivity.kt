package com.notas.appkafa

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.notas.appkafa.database.MyDatabase
import com.notas.appkafa.database.Note
import com.notas.appkafa.database.NotesDao
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.notas.appkafa.tools.Constantes
import kotlinx.android.synthetic.main.activity_nota.*
import android.view.*
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import kotlinx.android.synthetic.main.calendario.view.*
import java.util.*
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat

/*APPKAFA - Activity encargada de Crear, modificar, eliminar notas de acuerdo a las acciones recibidas por otras actividades*/
class NotaActivity : AppCompatActivity() {


    private lateinit var notesDao: NotesDao
    var note: Note? = null

    private var txtTituloNota: EditText? = null
    private var txtCuerpoNota: EditText? = null
    private var btnRecordatorio: ImageView? = null
    private var btnImagen: ImageView? = null
    private var imgImagen: ImageView? = null
    private var btnximagen: ImageView? = null


    private var colorSeleccionado: String? = null
    private var editarNota: Boolean = false
    private var idNota: Int? = null
    private var editarRecordatorio: Boolean = false
    private var configUsuario: MutableMap<Any, Any> = mutableMapOf()
    private var rutaImagen: String? = null
    private var imgNota: ImageView? = null

    private val REQUEST_IMAGE_CAPTURE = 1
    private val PERMISSION_REQUEST_CODE: Int = 101
    private var mCurrentPhotoPath: String? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nota)

        setSupportActionBar(toolbar)
        setTitle("Nueva Nota")
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)

        txtTituloNota = findViewById(R.id.tituloNota)
        txtCuerpoNota = findViewById(R.id.cuerpoNota)
        btnRecordatorio = findViewById(R.id.btnRecordatorio)
        btnImagen = findViewById(R.id.btnImage)
        imgImagen = findViewById(R.id.imgImagen)
        btnximagen = findViewById(R.id.btnximagen)
        imgNota = findViewById(R.id.imgNota)

        val database = MyDatabase.getInstance(this)
        notesDao = database.notesDao()

/*APPKAFA - Obtener las sharedPreference que han sido creadas para el caso de esta actividad*/
        obtenerPreferenciasUsuario()

/*APPKAFA - Validar la accion recibida mediante intent en caso de existir, si no se recibe una accion es porque es una creación de una nueva nota*/
        try {
            if (getIntent() != null && intent != null && intent.extras!!.containsKey(Constantes.ACCION) && intent.getStringExtra(
                    Constantes.ACCION
                ) != null
            ) {

                val accion: String? = intent.getStringExtra(Constantes.ACCION);
                when (accion) {

                    Constantes.ACCION_EDITAR -> {
                        editarNota = true
                        idNota = intent.getIntExtra(Constantes.ID_NOTA, -1)
                        setTitle("Modificar Nota")
                    }
                    Constantes.ACCION_ELIMINAR -> {
                        idNota = intent.getIntExtra(Constantes.ID_NOTA, -1)
                        eliminarNota(idNota!!)
                    }
                    Constantes.ACCION_RECUPERAR -> {
                        idNota = intent.getIntExtra(Constantes.ID_NOTA, -1)
                        recuperarNota(idNota!!)
                    }
                    Constantes.ACCION_ELIMINAR_DEFINITIVO -> {
                        idNota = intent.getIntExtra(Constantes.ID_NOTA, -1)
                        eliminarDefinitivoNota(idNota!!)
                    }
                    Constantes.ACCION_EDITAR_RECORDATORIO -> {
                        editarNota = true
                        idNota = intent.getIntExtra(Constantes.ID_NOTA, -1)
                        setTitle("Modificar Nota")
                        editarRecordatorio = true
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

/*APPKAFA - Si se ha recibido un id de nota, realizar la consulta y carga en la actividad*/
            if (idNota != -1) {

                note = notesDao.getNote(idNota!!)
                if (note != null) {

                    txtTituloNota!!.setText(note!!.titulo)
                    txtCuerpoNota!!.setText(note!!.nota)
                    txtRecordatorio.setText(note!!.fechaRecordatorio)

                    if (note!!.color != null) {
                        txtCuerpoNota?.setBackgroundColor(note!!.color!!.toInt())
                    }
                    colorSeleccionado = note!!.color


                    var btnx: ImageView = findViewById(R.id.btnx)
                    if (note!!.fechaRecordatorio != null && note!!.fechaRecordatorio != "-") {
                        btnx.visibility = View.VISIBLE
                    } else {
                        btnx.visibility = View.INVISIBLE
                    }
                    rutaImagen = note!!.rutaImagen

                    if (rutaImagen != null) {
                        btnximagen!!.visibility = View.VISIBLE
                        cargarImagen()
                    } else {
                        btnximagen!!.visibility = View.INVISIBLE
                    }
                }
            }
        } catch (ex: Exception) {/*APPKAFA - Excepcion controlada para continuar el flujo en caso de presentarse un error*/
            idNota = -1
            editarNota = false
        }

        /**********************************************************************************************/

/*APPKAFA - Listener para el caso del botón (ImageView) del recordatorio*/
        btnRecordatorio!!.setOnClickListener {

            /*APPKAFA - Inyectar un xml que cuenta con la vista para elegir fecha y hora*/
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.calendario, null)

/*APPKAFA - Crear un AlertDialog en el cual se mostrará el xml inyectado anteriormente*/
            val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
                .setTitle("Recordatorio")

            val mAlertDialog = mBuilder.show()

            var fecha = txtRecordatorio!!.text

            if (fecha != "" && fecha != "-") {
                var seleccion = fecha.toString().split(" ")
                mDialogView.efecha!!.setText(seleccion[0])

                if (seleccion.count() > 1)
                    mDialogView.ehora!!.setText(seleccion[1])
            }
            mDialogView.efecha!!.clearFocus()

/*APPKAFA - Crear listener para cuando sea seleccionado el EditText de la fecha*/
            mDialogView.efecha!!.setOnClickListener(View.OnClickListener { view ->

                val cal = Calendar.getInstance()
                val dia = cal.get(Calendar.DAY_OF_MONTH)
                val mes = cal.get(Calendar.MONTH)
                val anio = cal.get(Calendar.YEAR)

                val datePickerDialog = DatePickerDialog(
                    this,
                    DatePickerDialog.OnDateSetListener { view, anio, mes, dia ->
                        mDialogView.efecha!!.setText(
                            dia.toString() + "/" + (mes + 1) + "/" + anio
                        )
                    }, cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                )
                datePickerDialog.show()

            })

/*APPKAFA - Crear listener para cuando sea seleccionado el EditText de la hora*/
            mDialogView.ehora!!.setOnClickListener(View.OnClickListener { view ->

                val c = Calendar.getInstance()
                var hora = c.get(Calendar.HOUR_OF_DAY)
                var minutos = c.get(Calendar.MINUTE)

                val timePickerDialog = TimePickerDialog(
                    this,
                    TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                        mDialogView.ehora!!.setText(
                            "$hourOfDay:$minute"
                        )
                    }, hora!!, minutos!!, false
                )
                timePickerDialog.show()


            })
/*APPKAFA - Crear listener para cuando sea seleccionado el ImageView de la fecha*/
            mDialogView.btnf!!.setOnClickListener(View.OnClickListener { view ->

                val cal = Calendar.getInstance()
                val dia = cal.get(Calendar.DAY_OF_MONTH)
                val mes = cal.get(Calendar.MONTH)
                val anio = cal.get(Calendar.YEAR)

                val datePickerDialog = DatePickerDialog(
                    this,
                    DatePickerDialog.OnDateSetListener { view, anio, mes, dia ->
                        mDialogView.efecha!!.setText(
                            dia.toString() + "/" + (mes + 1) + "/" + anio
                        )
                    }, cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                )
                datePickerDialog.show()

            })

/*APPKAFA - Crear listener para cuando sea seleccionado el ImageView de la hora*/
            mDialogView.btne!!.setOnClickListener(View.OnClickListener { view ->

                val c = Calendar.getInstance()
                var hora = c.get(Calendar.HOUR_OF_DAY)
                var minutos = c.get(Calendar.MINUTE)

                val timePickerDialog = TimePickerDialog(
                    this,
                    TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                        mDialogView.ehora!!.setText(
                            "$hourOfDay:$minute"
                        )
                    }, hora!!, minutos!!, false
                )
                timePickerDialog.show()
            })


/*APPKAFA - Listener del botón aceptar  en el alertDialog*/
            mDialogView.btnAceptar.setOnClickListener {
                //dismiss dialog
                mAlertDialog.dismiss()
//get text from EditTexts of custom layout
                val fecha = mDialogView.efecha.text.toString()
                val hora = mDialogView.ehora.text.toString()

//set the input text in TextView
                txtRecordatorio!!.setText(fecha + " " + hora)

                if (txtRecordatorio!!.text.trim() != "") {
                    btnx.visibility = View.VISIBLE
                } else {
                    btnx.visibility = View.INVISIBLE
                }
                if (editarRecordatorio) {
                    guardar()
                }
            }
/*APPKAFA - Regresar a las notas cuando no se desea agregar un recordatorio*/
            mDialogView.btnCancelar.setOnClickListener {
                //dismiss dialog
                mAlertDialog.dismiss()
                if (editarRecordatorio) {
                    finish()
                }
            }
        }
/*APPKAFA - LIstener para validar is se cuenta con los permisos de acceso a cámara, en caso de ser posible realizar la respectiva captura de la foto*/
        btnImagen!!.setOnClickListener {
            if (checkPersmission()) capturarFoto() else requestPermission()
        }

/*APPKAFA - Si la acción es para programar un recordatorio, disparar el evento click ara elegir fecha y hora*/
        if (editarRecordatorio) {
            btnRecordatorio!!.callOnClick();
        }

    }

    /*APPKAFA - Si es necesario solicitar permisos de acceso cámara y almacenamiento al usuario*/
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {

                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                ) {

                    capturarFoto()

                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }

            else -> {

            }
        }
    }

    private fun capturarFoto() {

        val intent: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val file: File = createFile()

        val uri: Uri = FileProvider.getUriForFile(
            this,
            "com.notas.appkafa.provider",
            file
        )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        try {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        }catch (ex:Exception){
            Toast.makeText(baseContext,"El dispositivo debe contar con cámara",Toast.LENGTH_LONG).show()
        }

    }

    /*APPKAFA - Si se ha reabierto la actividad, recargar nuevamente las sharedpreference*/
    override fun onResume() {
        super.onResume()
        obtenerPreferenciasUsuario()
    }

    override fun onRestart() {
        super.onRestart()
        obtenerPreferenciasUsuario()
    }

    /*APPKAFA - Cargar las preferencias del usuario*/
    fun obtenerPreferenciasUsuario() {

        val prefs = getSharedPreferences(Constantes.SHARED_PREFERENCE_APPKAFA, Context.MODE_PRIVATE)

        configUsuario[Constantes.SHARED_PREFERENCE_CONF_GUARDADO] =
            convertirBSN(prefs.getBoolean(Constantes.SHARED_PREFERENCE_CONF_GUARDADO, true))
        configUsuario[Constantes.SHARED_PREFERENCE_CONF_ELIMINAR] =
            convertirBSN(prefs.getBoolean(Constantes.SHARED_PREFERENCE_CONF_ELIMINAR, false))
        configUsuario[Constantes.SHARED_PREFERENCE_CONF_ELIMINAR_PERMANENTE] = convertirBSN(
            prefs.getBoolean(
                Constantes.SHARED_PREFERENCE_CONF_ELIMINAR_PERMANENTE,
                false
            )
        )

    }

    /*APPKAFA - Click en elimianr recordatorio de la nota*/
    fun onX(view: View) {
        txtRecordatorio.text = "-"
    }

    /*APPKAFA - Click en eliminar la imagen de la nota*/
    fun onXImagen(view: View) {
        deleteFileInterno()
        view.visibility = View.INVISIBLE
    }

    /*APPKAFA - LUego de capturar la foto y si es exitoso captuar rutaImagen y habiliatr su eliminacion*/
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            rutaImagen = mCurrentPhotoPath
            btnximagen!!.visibility = View.VISIBLE
            cargarImagen()
        } else {
            rutaImagen = null
            btnximagen!!.visibility = View.INVISIBLE
        }
    }

    /*APPKAFA - Validar si existen permisos para acceder a la camara*/
    private fun checkPersmission(): Boolean {
        return (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED)
    }

    /*APPKAFA - Validar si existen permisos para acceder al almacenamiento en el dispisitivo*/
    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            ), PERMISSION_REQUEST_CODE
        )
    }

    /*APPKAFA - Guardar en el directorio la imagen cuando es capturada*/
    @Throws(IOException::class)
    private fun createFile(): File {
// Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            mCurrentPhotoPath = absolutePath
        }
    }

    /*APPKAFA - Eliminar la imagen del directorio*/
    @Throws(IOException::class)
    private fun deleteFileInterno() {
// Create an image file name
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        if (rutaImagen != null) {

            var rutaFull = rutaImagen!!.split("/")

            var file: File = File(storageDir, rutaFull[rutaFull.size - 1]);

            if (file.exists()) {
                file.delete()
                rutaImagen = null
                cargarImagen()
            }
        }

    }

    /*APPKAFA - Cargar imagen en el ImageView si existe*/
    private fun cargarImagen() {
        if (rutaImagen != null) {

            var bitmap: Bitmap = BitmapFactory.decodeFile(rutaImagen)
            imgNota!!.setImageBitmap(bitmap)
        }
    }

    /*APPKAFA - Mover una nota a la papelera*/
    fun eliminarNota(idNota: Int) {


        val builder = AlertDialog.Builder(this)

        var mensaje: String = Constantes.MENSAJE_ELIMINAR

/*APPKAFA - Validar si es necesario una confirmacion antes de ejecutar la accion*/
        if (configUsuario[Constantes.SHARED_PREFERENCE_CONF_ELIMINAR] == "S") {
            builder.setTitle("Nota")
            builder.setMessage(mensaje)
//builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))
            builder.setNegativeButton(android.R.string.no) { dialog, which ->
                Toast.makeText(
                    applicationContext,
                    "Nota no eliminada", Toast.LENGTH_SHORT
                ).show()

                finish()
            }
            builder.setPositiveButton(android.R.string.yes) { dialog, which ->

                note = notesDao.getNote(idNota!!)
                note!!.eliminada = true
                notesDao.updateNote(note!!)
                Toast.makeText(
                    applicationContext,
                    "Nota eliminada", Toast.LENGTH_SHORT
                ).show()
                finish()
            }
            builder.show()
        } else {
            note = notesDao.getNote(idNota!!)
            note!!.eliminada = true
            notesDao.updateNote(note!!)
            Toast.makeText(
                applicationContext,
                "Nota eliminada", Toast.LENGTH_SHORT
            ).show()
            finish()
        }
    }

    /*APPKAFA - Restaurar nota desde la papelera*/
    fun recuperarNota(idNota: Int) {
        note = notesDao.getNote(idNota!!)
        note!!.eliminada = false
        notesDao.updateNote(note!!)
        Toast.makeText(
            applicationContext,
            "Nota restaurada", Toast.LENGTH_SHORT
        ).show()
        finish()
    }

    /*APPKAFA - Eliminar definitivamente la nota de la papelera*/
    fun eliminarDefinitivoNota(idNota: Int) {

        val builder = AlertDialog.Builder(this)

        var mensaje: String = Constantes.MENSAJE_ELIMINAR_PERMANENTE

/*APPKAFA - Validar si es necsario conformacion al eliminar una nota*/
        if (configUsuario[Constantes.SHARED_PREFERENCE_CONF_ELIMINAR_PERMANENTE] == "S") {
            builder.setTitle("Nota")
            builder.setMessage(mensaje)

            builder.setNegativeButton(android.R.string.no) { dialog, which ->
                Toast.makeText(
                    applicationContext,
                    "Nota no eliminada", Toast.LENGTH_SHORT
                ).show()
                finish()
            }
            builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                note = notesDao.getNote(idNota!!)
                notesDao.deleteNote(note!!)
                deleteFileInterno()
                Toast.makeText(
                    applicationContext,
                    "Nota eliminada permanentemente", Toast.LENGTH_SHORT
                ).show()
                finish()
            }
            builder.show()
        } else {
            note = notesDao.getNote(idNota!!)
            notesDao.deleteNote(note!!)
            deleteFileInterno()
            Toast.makeText(
                applicationContext,
                "Nota eliminada permanentemente", Toast.LENGTH_SHORT
            ).show()
            finish()
        }
    }

    /*APPKAFA - Valor booleano a S/N*/
    fun convertirBSN(valor: Boolean): String {
        return when (valor) {
            true -> "S"
            else -> "N"
        }
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

    /*APPKAFA - Persistir nota en base de datos*/
    fun guardar(view: View? = null) {

        if (note == null) {
            note = Note()
        }

        note!!.titulo = txtTituloNota!!.text.toString()
        note!!.nota = txtCuerpoNota!!.text.toString()
        note!!.color = colorSeleccionado
        note!!.eliminada = false
        note!!.recordatorio =
            if (txtRecordatorio.text.trim() != "" && txtRecordatorio.text.trim() != "-") {
                true
            } else {
                false
            }
        note!!.fechaRecordatorio = txtRecordatorio.text.trim().toString()
        note!!.rutaImagen = rutaImagen

        if (note!!.nota != null && note!!.nota!!.trim() != "") {
            notesDao.insertNote(note!!)

            val builder = AlertDialog.Builder(this)

            var mensaje: String = Constantes.MENSAJE_GUARDAR

            if (editarNota) {
                mensaje = Constantes.MENSAJE_ACTUALIZAR
            }
/*APPKAFA - Validar si es necesario mostrar mensaje al guardar, no mostrar cuando solo se programa la fecha del recordatorio elegido desde la actividad principal de la notas*/
            if (configUsuario[Constantes.SHARED_PREFERENCE_CONF_GUARDADO] == "S" && editarRecordatorio == false) {
                builder.setTitle("Nota")
                builder.setMessage(mensaje)


                builder.setPositiveButton(android.R.string.yes) { dialog, which ->

                    cancelar(view)
                }
                builder.show()
            } else {
                Toast.makeText(
                    applicationContext,
                    "Nota guardada", Toast.LENGTH_SHORT
                ).show()
                cancelar(view)
            }


        } else {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Nota")
            builder.setMessage("No ingresó una nota válida")
            builder.setPositiveButton(android.R.string.yes) { dialog, which -> }
            builder.show()

        }

    }

    /*APPKAFA - Regesar a la actividad principal cuando es cancelar*/
    fun cancelar(view: View? = null) {
        val intent = Intent(view!!.context, PricipalActivity::class.java)
        ContextCompat.startActivity(view.context, intent, null)
    }

    /*APPKAFA - AL elegir los botones de los colores cambiar el background de la nota*/
    fun onColorNota(view: View) {
        val colorValue = (view as AppCompatButton).textColors.defaultColor
        txtCuerpoNota?.setBackgroundColor(colorValue)
        colorSeleccionado = colorValue.toString();

    }
}