package com.notas.appkafa


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import kotlinx.android.synthetic.main.fragment_preferencias.view.*
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.widget.CompoundButton
import android.widget.Toast
import com.notas.appkafa.tools.Constantes


/*APPKAFA - Creacion, modificación y consultar de las preferencias del usuario*/
class preferenciasFragment : Fragment() {


    lateinit var swGuardado:Switch
    lateinit var swEliminacion:Switch
    lateinit var swEliminacionPermanente:Switch
    private val sharedPref: SharedPreferences? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        var rootview= inflater.inflate(R.layout.fragment_preferencias, container, false)

        swGuardado=rootview.findViewById(R.id.swGuardado)
        swEliminacion=rootview.findViewById(R.id.swEliminacion)
        swEliminacionPermanente=rootview.findViewById(R.id.swEliminacionPermanente)

        swGuardado.setOnCheckedChangeListener { view, isChecked ->cambioEstado(view,isChecked)}
        swEliminacion.setOnCheckedChangeListener { view, isChecked ->cambioEstado(view,isChecked)}
        swEliminacionPermanente.setOnCheckedChangeListener { view, isChecked ->cambioEstado(view,isChecked)}

        precargarPreferencias(rootview)
        return rootview
    }

    private fun precargarPreferencias(view:View) {
        val prefs = view.context.getSharedPreferences(Constantes.SHARED_PREFERENCE_APPKAFA, MODE_PRIVATE)
        swGuardado.isChecked=prefs.getBoolean(Constantes.SHARED_PREFERENCE_CONF_GUARDADO, true)
        swEliminacion.isChecked=prefs.getBoolean(Constantes.SHARED_PREFERENCE_CONF_ELIMINAR, false)
        swEliminacionPermanente.isChecked=prefs.getBoolean(Constantes.SHARED_PREFERENCE_CONF_ELIMINAR_PERMANENTE,false)
    }

    /*APPKAFA - Almacenar las preferencias de usuario seleccionadas*/
    fun  cambioEstado(view:View,checked:Boolean){
        //https://expocodetech.com/como-usar-sharedpreferences-en-android/

        val prefs = view!!.context!!.getSharedPreferences(Constantes.SHARED_PREFERENCE_APPKAFA, MODE_PRIVATE)
        val editor = prefs.edit()

        when(view.id){
            R.id.swGuardado->editor.putBoolean(Constantes.SHARED_PREFERENCE_CONF_GUARDADO,checked)
            R.id.swEliminacion->editor.putBoolean(Constantes.SHARED_PREFERENCE_CONF_ELIMINAR,checked)
            R.id.swEliminacionPermanente->editor.putBoolean(Constantes.SHARED_PREFERENCE_CONF_ELIMINAR_PERMANENTE,checked)
        }

        editor.commit()
    }


}
