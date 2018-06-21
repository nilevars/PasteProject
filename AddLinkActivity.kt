package com.gtae.app.pasteproject

import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.gtae.app.pasteproject.R.id.link_address
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class AddLinkActivity : AppCompatActivity() {

    private val REGISTER_URL = "http://192.168.1.112/PasteProject/link/link_insert.php"
    val KEY_LINK = "link"
    val KEY_USER = "user_id"
    lateinit var e : EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_link)

        e =  findViewById(R.id.link_address)

        //Toolbar Settings
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        val upArrow = resources.getDrawable(R.drawable.abc_ic_clear_material)
        upArrow.setColorFilter(resources.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP)
        toolbar.navigationIcon = upArrow
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_answer, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.done) {
           // Toast.makeText(this@AddLinkActivity, "Post Clicked", Toast.LENGTH_LONG).show()
            var ok: Boolean? = true
            val e : EditText = findViewById(R.id.link_address)
            val l = e.text.toString()

            if (l.matches("".toRegex())) {
                ok = false
                Toast.makeText(this@AddLinkActivity, "Enter a link", Toast.LENGTH_LONG).show()
            }

            if (ok!!) {
               // Toast.makeText(this@AddLinkActivity, "Calling insert Data", Toast.LENGTH_LONG).show()
                insert_data()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    internal fun insert_data(): Boolean {
       // Toast.makeText(this@AddLinkActivity, "In Insert Data", Toast.LENGTH_LONG).show()
        val stringRequest = object : StringRequest(com.android.volley.Request.Method.POST, REGISTER_URL,
                Response.Listener {   response ->
                try {
                        val jsonObject = JSONObject(response)
                        val text = Integer.parseInt(jsonObject.getString("text"))
                        //val info = jsonObject.getString("info")
                        if (text == 1) {
                           // Log.i("Query",info);
                             e.setText("")
                             Toast.makeText(this@AddLinkActivity, "Your link has been posted", Toast.LENGTH_LONG).show()

                            }
                        else if (text == 0){
                          //  Log.i("Query",info);
                            Toast.makeText(this@AddLinkActivity, "Your link has not been posted ", Toast.LENGTH_LONG).show()
                            }
                 } catch (e: JSONException) {
                            e.printStackTrace()
                }
                },
               Response.ErrorListener {error->
                   Toast.makeText(this@AddLinkActivity,error.toString(),Toast.LENGTH_LONG).show();

                }) {
            override fun  getParams() : Map<String,String>{
                    val params = HashMap<String, String>()

                    val l = e.text.toString()
                    val user_id = FirebaseAuth.getInstance().currentUser?.uid
                    if(user_id!=null){
                        params["link"] = l
                        params["user_id"] = user_id
                    }

                    println("params:" + params.toString())
                    Log.e("Insert params", params.toString())
                    return params
                }
        }

        val requestQueue = Volley.newRequestQueue(this@AddLinkActivity)
        requestQueue.add(stringRequest)

        return true
    }

}

