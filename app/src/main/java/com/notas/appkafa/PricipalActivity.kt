package com.notas.appkafa

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_pricipal.*

class PricipalActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pricipal)

        setSupportActionBar(toolbar)

        val navController = findNavController(R.id.myNavHostFragment)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.notesFragment, R.id.perfilUsuarioFragment,R.id.papeleraFragment,R.id.preferenciasFragment), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val fab = findViewById<FloatingActionButton>(R.id.btnNuevaNota)
        fab.setOnClickListener { view -> createNote() }


    }

    override fun onSupportNavigateUp(): Boolean {
        val navController= findNavController(R.id.myNavHostFragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun createNote() {
        val intent = Intent(this, NotaActivity::class.java)
        startActivityForResult(intent, 1)
    }
}
