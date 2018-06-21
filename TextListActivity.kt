package com.gtae.app.pasteproject

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.webkit.URLUtil
import android.widget.RelativeLayout
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.*
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONException
import org.json.JSONObject
import java.net.MalformedURLException
import java.net.URL
import java.util.HashMap

class TextListActivity : AppCompatActivity() {

    lateinit var textAdapter: TextAdapter
    private val textDataList = java.util.ArrayList<TextData>()
    internal var s = 0
    internal var n = 5
    internal var total: Int = 0
    var text=""
    lateinit var snack: Snackbar
    lateinit var recyclerView: RecyclerView

    private var mRequestQueue: RequestQueue? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_list)

        recyclerView = findViewById(R.id.recycler_view)
        textAdapter = TextAdapter(this@TextListActivity, textDataList)
        recyclerView.adapter = textAdapter
        recyclerView.layoutManager = LinearLayoutManager(this@TextListActivity, LinearLayoutManager.VERTICAL, false)

        mRequestQueue = getRequestQueue()

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
           // val intent = Intent(this, AddtextActivity::class.java)
           // startActivity(intent)
        }


        clip_data()
        dataFetch()


    }
    fun clip_data()
    {
        val clipBoard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = clipBoard!!.getPrimaryClip()
        val item = clipData.getItemAt(0)
        text = item.getText().toString()
        show_snack(text)

        Log.i("Tag 1", text);
        clipBoard!!.addPrimaryClipChangedListener(ClipboardManager.OnPrimaryClipChangedListener {

            val clipData = clipBoard!!.getPrimaryClip()
            val item = clipData.getItemAt(0)
            text = item.getText().toString()
            show_snack(text)
            Log.i("Tag 1", text);
        })

    }
    fun show_snack(text:String){
        if (!text.equals("")) {
            if (!URLUtil.isValidUrl(text)) {
                var r : RelativeLayout = findViewById(R.id.main_layout)
                snack = Snackbar.make(r, "You copied " + text, Snackbar.LENGTH_INDEFINITE)
                snack.setAction("Add", View.OnClickListener {
                    insert_data()
                })


                snack.show()

            }
        }

    }

    fun getRequestQueue(): RequestQueue {
        if (mRequestQueue == null) {
            val cache = DiskBasedCache(this@TextListActivity.getCacheDir(), 10 * 1024 * 1024)
            val network = BasicNetwork(HurlStack())
            mRequestQueue = RequestQueue(cache, network)
            mRequestQueue!!.start()
        }
        return mRequestQueue!!
    }

    fun insert_data(): Boolean {
        var data=text
        val REGISTER_URL = "http://gtae.info/PasteProject/text/text_insert.php"

        val stringRequest = object : StringRequest(com.android.volley.Request.Method.POST, REGISTER_URL,
                Response.Listener { response ->
                    try {
                        val jsonObject = JSONObject(response)
                        val success= Integer.parseInt(jsonObject.getString("success"))
                        if (success == 1) {
                            val text = jsonObject.getString("text")
                            var insertIndex = 0;
                            val textData = TextData("1",text)
                            textDataList.add(insertIndex, textData);
                            textAdapter.notifyItemInserted(insertIndex);

                            snack.dismiss()
                            recyclerView.smoothScrollToPosition(0);

                        } else if (success == 0) {

                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    Log.i("Status", "Error")

                }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()

                val user_id = FirebaseAuth.getInstance().currentUser?.uid
                if (user_id != null) {
                    params["text"] = data
                    params["user_id"] = user_id
                }

                println("params:" + params.toString())
                Log.e("Insert params", params.toString())
                return params
            }
        }

        val requestQueue = Volley.newRequestQueue(applicationContext)
        //val requestQueue = getRequestQueue()
        requestQueue.add(stringRequest)

        return true
    }
    internal fun dataFetch() {

        val user_id = FirebaseAuth.getInstance().currentUser?.uid
        var JsonURL: String = ""
        if (user_id != null) {
            JsonURL = "http://gtae.info/PasteProject/text/get_text.php?user_id="+ user_id
            Log.e("url", JsonURL)
            val req = JsonObjectRequest(Request.Method.GET,
                    JsonURL, null, Response.Listener { jo ->
                Log.i("Response", jo.toString())
                try {
                    val success = jo.getInt("success")
                    Log.i("success:", Integer.toString(success))
                    if (success == 1) {
                        val data = jo.getJSONArray("text")
                        Log.i("json array", data.toString())
                        //totalItemCount = users.length()
                        Log.i("length", "" + data.length())
                        if (data.length() < n) {
                            n = data.length()
                        }
                        for (i in s until data.length()) {
                            Log.i("i", "" + i)
                            val o = data.getJSONObject(i)
                            Log.i("obj", o.toString())

                            val text = o.getString("text")
                            val id = o.getString("id")
                            val textData = TextData(id, text)

                            if (!containsId(id)) {

                                textDataList.add(textData)
                                Log.i("h", "helo")

                            }
                        }
                        s = n
                        n += 5
                        textAdapter.notifyDataSetChanged()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener { })
            mRequestQueue!!.add(req)
        }


    }

    fun containsId(id: String): Boolean {
        for (f in textDataList) {
            if (f.id.equals(id)) {
                return true
            }
        }
        return false
    }

}
