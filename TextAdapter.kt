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

class TextAdapter() : RecyclerView.Adapter<TextViewHolder>() {
    lateinit var textDataList : List<TextData>
    lateinit internal var rowView: View
    lateinit var  inflater: LayoutInflater
    lateinit var context: Context


    constructor(context: Context,textDataList : List<TextData>) : this() {
        this.textDataList = textDataList
        this.context = context
        this.inflater= context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater;
    }



    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: TextViewHolder, position: Int) {

        holder.text.setText(textDataList.get(position).text)
        holder.linearLayout.setOnClickListener {

        }

    }

    override fun getItemCount(): Int {
        return textDataList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextViewHolder {
        rowView = inflater.inflate(R.layout.text_card, parent, false)
        return TextViewHolder(rowView)
    }


}
class TextViewHolder(rowView: View) : RecyclerView.ViewHolder(rowView) {

    internal var text: TextView
    internal var linearLayout: LinearLayout

    init {
        text = rowView.findViewById<View>(R.id.text) as TextView

        linearLayout = rowView.findViewById<View>(R.id.text_layout) as LinearLayout
    }
}