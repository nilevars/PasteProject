package com.gtae.app.pasteproject

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.RelativeLayout
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*
import org.json.JSONException
import org.json.JSONObject
import java.net.MalformedURLException
import java.net.URL
import java.util.HashMap

public class LinkFragment : Fragment() {
    lateinit var linkAdapter: LinkAdapter
    private val linkDataList = java.util.ArrayList<LinkData>()
    internal var s = 0
    internal var n = 5
    internal var total: Int = 0
    var text=""
    lateinit var snack: Snackbar
    lateinit var recyclerView: RecyclerView

    private var mRequestQueue: RequestQueue? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.activity_link_list, container, false)
        Log.i("Status","In link fragment")
        recyclerView = view.findViewById(R.id.recycler_view)
        linkAdapter = LinkAdapter(activity!!.applicationContext, linkDataList)
        recyclerView.adapter = linkAdapter
        recyclerView.layoutManager = LinearLayoutManager(activity!!.applicationContext, LinearLayoutManager.VERTICAL, false)

        mRequestQueue = getRequestQueue()
        clip_data(view)
        dataFetch()
        return view;

    }
    fun clip_data(view : View)
    {
        val clipBoard = activity!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = clipBoard!!.getPrimaryClip()
        val item = clipData.getItemAt(0)
        text = item.getText().toString()
        show_snack(text,view)

        Log.i("Tag 1", text);
        clipBoard!!.addPrimaryClipChangedListener(ClipboardManager.OnPrimaryClipChangedListener {

            val clipData = clipBoard!!.getPrimaryClip()
            val item = clipData.getItemAt(0)
            text = item.getText().toString()
            show_snack(text,view)
            Log.i("Tag 1", text);
        })

    }
    fun show_snack(text:String,view:View){
        if (!text.equals("")) {
            if (URLUtil.isValidUrl(text)) {
                var r : RelativeLayout = view.findViewById(R.id.main_layout)
                snack = Snackbar.make(content, "You copied " + text, Snackbar.LENGTH_INDEFINITE)
                snack.setAction("Add", View.OnClickListener {
                    insert_data()
                })
                snack.show()

            }
        }

    }

    fun getRequestQueue(): RequestQueue {
        if (mRequestQueue == null) {
            val cache = DiskBasedCache(activity!!.applicationContext.getCacheDir(), 10 * 1024 * 1024)
            val network = BasicNetwork(HurlStack())
            mRequestQueue = RequestQueue(cache, network)
            mRequestQueue!!.start()
        }
        return mRequestQueue!!
    }

    fun insert_data(): Boolean {
        var data=text
        val REGISTER_URL = "http://gtae.info/PasteProject/link/link_insert.php"

        val stringRequest = object : StringRequest(com.android.volley.Request.Method.POST, REGISTER_URL,
                Response.Listener { response ->
                    try {
                        val jsonObject = JSONObject(response)
                        val text = Integer.parseInt(jsonObject.getString("text"))

                        if (text == 1) {

                            val title = jsonObject.getString("title")
                            val link = jsonObject.getString("link")
                            val image = jsonObject.getString("image")

                            Log.i("TITLE",title)

                            var baseUrl = ""
                            try {
                                val url = URL(link)
                                baseUrl = url.authority

                            } catch (e: MalformedURLException) {
                                // do something
                            }

                            var insertIndex = 0;
                            val linkData = LinkData("1", title, link, baseUrl, image)
                            linkDataList.add(insertIndex, linkData);
                            linkAdapter.notifyItemInserted(insertIndex);

                            snack.dismiss()
                            recyclerView.smoothScrollToPosition(0);

                        } else if (text == 0) {

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
                    params["link"] = data
                    params["user_id"] = user_id
                }

                println("params:" + params.toString())
                Log.e("Insert params", params.toString())
                return params
            }
        }

        val requestQueue = Volley.newRequestQueue(activity!!.applicationContext)
        //val requestQueue = getRequestQueue()
        requestQueue.add(stringRequest)

        return true
    }
    internal fun dataFetch() {

        val user_id = FirebaseAuth.getInstance().currentUser?.uid
        var JsonURL: String = ""
        if (user_id != null) {
            JsonURL = "http://gtae.info/PasteProject/link/get_link.php?user_id=" + user_id
            Log.e("url", JsonURL)
            val req = JsonObjectRequest(Request.Method.GET,
                    JsonURL, null, Response.Listener { jo ->
                Log.i("Response", jo.toString())
                try {
                    val success = jo.getInt("success")
                    Log.i("success:", Integer.toString(success))
                    if (success == 1) {
                        val data = jo.getJSONArray("link")
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

                            val t = o.getString("title")
                            val link = o.getString("link")
                            var baseUrl = ""

                            try {
                                val url = URL(link)
                                baseUrl = url.authority
                            } catch (e: MalformedURLException) {
                                // do something
                            }

                            val image = o.getString("image")
                            Log.i("title", t)
                            //val d = o.getString("link")
                            val id = o.getString("id")
                            val linkData = LinkData(id, t, link, baseUrl, image)

                            if (!containsId(id)) {

                                linkDataList.add(linkData)
                                Log.i("h", "helo")

                            }
                        }
                        s = n
                        n += 5
                        linkAdapter.notifyDataSetChanged()
                        //progress.setVisibility(View.GONE)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener { })
            mRequestQueue!!.add(req)
        }


    }

    fun containsId(id: String): Boolean {
        for (f in linkDataList) {
            if (f.id.equals(id)) {
                return true
            }
        }
        return false
    }


}