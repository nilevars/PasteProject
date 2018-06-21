package com.gtae.app.pasteproject

public class LinkData{
    internal var id: String=""
    internal var title: String=""
    internal var link: String=""
    internal var base: String=""
    internal var image: String=""
    init {

    }
    constructor(id:String , title : String , link : String , base : String , image : String){
        this.id = id
        this.title = title
        this.link = link
        this.base = base
        this.image = image

    }
}