package com.gtae.app.pasteproject

import android.content.Context
import android.net.Uri
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.customtabs.CustomTabsIntent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class LinkAdapter() : RecyclerView.Adapter<LinkViewHolder>() {
    lateinit var linkDataList : List<LinkData>
    lateinit internal var rowView: View
    lateinit var  inflater: LayoutInflater
    lateinit var context: Context


    constructor(context: Context,linkDataList : List<LinkData>) : this() {
        this.linkDataList = linkDataList
        this.context = context
        this.inflater= context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater;
    }



    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: LinkViewHolder, position: Int) {
        holder.link_title.setText(linkDataList.get(position).title)
        holder.link_address.setText(linkDataList.get(position).link)
        holder.link_base.setText(linkDataList.get(position).base)
        if (!linkDataList.get(position).image.equals("")) {
            Picasso.with(context).load(linkDataList.get(position).image).resize(400, 400).centerCrop().into(holder.link_image, object : Callback {
                override fun onSuccess() {
                    println("loaded Image")
                }

                override fun onError() {
                    holder.link_image.setImageResource(R.drawable.cartoon)

                    println("Unable to load Image")
                }
            })
        }
        holder.linearLayout.setOnClickListener {
            /*  var browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(newsDataList[position].news_link))
              context.startActivity(browserIntent)*/
            //var url = "https://paul.kinlan.me/";
            var builder =  CustomTabsIntent.Builder();

            builder.setToolbarColor(context.getColor(R.color.colorPrimary));
            var customTabsIntent = builder.build ();
            customTabsIntent.launchUrl(context, Uri.parse(linkDataList[position].link));
        }

    }

    override fun getItemCount(): Int {
        return linkDataList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinkViewHolder {
        rowView = inflater.inflate(R.layout.link_card, parent, false)
        return LinkViewHolder(rowView)
    }


}
class LinkViewHolder(rowView: View) : RecyclerView.ViewHolder(rowView) {

    internal var link_address: TextView
    internal var link_base: TextView
    internal var link_image: ImageView
    internal var link_title: TextView

    internal var linearLayout: LinearLayout

    init {
        link_title = rowView.findViewById<View>(R.id.title) as TextView
        link_address = rowView.findViewById<View>(R.id.link_address) as TextView
        link_base = rowView.findViewById<View>(R.id.link_base) as TextView
        link_image = rowView.findViewById<View>(R.id.photo) as ImageView
        linearLayout = rowView.findViewById<View>(R.id.ad_layout) as LinearLayout
    }
}