package com.groep4.mindfulness.activities

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.groep4.mindfulness.R
import com.groep4.mindfulness.fragments.*
import com.groep4.mindfulness.interfaces.CallbackInterface
import com.groep4.mindfulness.model.Gebruiker
import com.groep4.mindfulness.model.Oefening
import com.groep4.mindfulness.model.Sessie
import com.groep4.mindfulness.utils.ExtendedDataHolder
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), CallbackInterface {

    private val BACK_STACK_ROOT_TAG = "root_fragment"
    lateinit var mAuth: FirebaseAuth
    lateinit var firestore: FirebaseFirestore

    var gebruiker : Gebruiker = Gebruiker()
    var sessies: ArrayList<Sessie> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        Logger.addLogAdapter(AndroidLogAdapter())

        // Set gebruiker
        if(this.gebruiker == null) {
            this.gebruiker = getAangemeldeGebruiker()
        }

        // Sessies
        sessies = getSessiesFromDB()
        val extras = ExtendedDataHolder.getInstance()
        extras.putExtra("sessielist", sessies)


        //Set no new fragment if there already is one
        if (savedInstanceState == null) {
            setFragment(FragmentMain(), false)
        }
    }

    /**
     * Om fragment te tonen
     */
    override fun setFragment(fragment: Fragment, addToBackstack: Boolean) {
        if (addToBackstack)
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_holder_main, fragment, "pageContent")
                    .addToBackStack(BACK_STACK_ROOT_TAG)
                    .commit()
        else
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_holder_main, fragment, "pageContent")
                    .commit()
    }

    /**
     * ActionMenu aanmaken
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    /**
     * Sessies ophalen
     */
    fun getSessiesFromDB(): ArrayList<Sessie> {
        val tempSessies: ArrayList<Sessie> = ArrayList()
        val sessieCollection = firestore.collection("Sessies")
        sessieCollection.get().addOnSuccessListener { sessies ->
            sessies.forEach{
                tempSessies.add(Sessie(
                        Integer.parseInt(it.get("id").toString()),
                        it.get("naam") as String,
                        it.get("beschrijving") as String,
                        getOefeningen(it.get("id").toString()),
                        it.get("sessieCode") as String
                ))
            }
        }
        return tempSessies
    }

    /**
     * Aangemelde gebruiker ophalen
     */
    fun getAangemeldeGebruiker() : Gebruiker{
        val gebruiker : Gebruiker = Gebruiker()
        val id = mAuth.currentUser!!.uid
        val gebruikerDoc = firestore.collection("Gebruikers").document(id)

        gebruikerDoc.get().addOnSuccessListener {
            Log.d("TAGSKE", it.toString())
        }
        return gebruiker
    }

    /**
     * Oefeningen van sessie ophalen
     */
    fun getOefeningen(sessieId: String): ArrayList<Oefening>{
        val oefeningen: ArrayList<Oefening> = ArrayList()
        val oefeningenCollection = firestore.collection("Oefeningen")

        oefeningenCollection.get().addOnSuccessListener { collection ->
            collection.forEach{
                if(it.data.getValue("sId").toString() == sessieId.toString()){
                    oefeningen.add(Oefening(
                            it.data.getValue("id").toString(),
                            it.data.getValue("naam") as String,
                            it.data.getValue("beschrijving") as String,
                            it.data.getValue("sId").toString(),
                            it.data.getValue("mimeType") as String,
                            it.data.getValue("groepen") as String,
                            it.data.getValue("url") as String
                    ))
                }
            }
        }
        return oefeningen
    }

    /**
     * Terugkeerknop
     */
    override fun onBackPressed() {
        super.onBackPressed()
    }

    /**
     * gegevens van de gebruiker bewerken
     */
    fun veranderGegevensGebruiker(gebruikersnaam : String, regio : String, telnr : String) {
        gebruiker!!.name = gebruikersnaam
        gebruiker!!.regio = regio
        gebruiker!!.telnr = telnr
    }

    /**
     * gegevens van de gebruiker opslaan
     */
    fun gegevensGebruikerOpslaan(){
        val id = mAuth.currentUser!!.uid
        val userDoc = firestore.collection("Gebruikers").document(id)

        val user = hashMapOf(
                "naam" to gebruiker.name,
                "email" to gebruiker.email,
                "telNr" to gebruiker.telnr,
                "regio" to gebruiker.regio,
                "groepnr" to gebruiker.groepsnr,
                "sessieId" to gebruiker.sessieId
        )

        userDoc.set(user)
        getAangemeldeGebruiker()
    }

    /**
     * Opent menu om naar FragmentProfiel te gaan of uit te loggen
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            R.id.action_logout -> {

                val builder = AlertDialog.Builder(this@MainActivity)
                builder.setMessage("Wil je uitloggen ?")

                builder.setPositiveButton("Ja"){dialog, which ->
                    FirebaseAuth.getInstance().signOut()
                    Toast.makeText(this,"Account uitgelogd", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, ActivityLogin::class.java)
                    this.startActivity(intent)
                    finish()
                }

                builder.setNegativeButton("Nee"){dialog,which ->
                }

                val dialog: AlertDialog = builder.create()
                dialog.show()


                return true
            }

            R.id.action_profiel -> {
                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_holder_main, FragmentProfiel(), "pageContent")
                        .addToBackStack(BACK_STACK_ROOT_TAG)
                        .commit()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Slaat de feedback op
     */
    fun postFeedback(id: String, beschrijving: String, score: String): String {
        val feedbackCollection = firestore.collection("Feedback")
        val feedback = hashMapOf(
                "id" to id,
                "beschrijving" to beschrijving,
                "score" to score
        )
        var response = ""
        feedbackCollection.add(feedback)
                .addOnSuccessListener {
                    response = "success"
                }
                .addOnFailureListener{
                    response = "Fail"
                }
        return response
    }


    /**
     * Voegt de unlockte sessie toe aan de gebruiker
     */
    fun sessieUnlocked() {
        gebruiker!!.sessieId += 1
        gegevensGebruikerOpslaan()
    }
}