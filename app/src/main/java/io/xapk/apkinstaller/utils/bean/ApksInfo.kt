package io.xapk.apkinstaller.utils.bean

class ApksInfo(var fileName:String,
               var lastModified: Long,
               var filePath:String,
               var fileSize: Long,
               var apkNameArrays: ArrayList<String>?){
    constructor() : this( String(),0L,String(),0L,null)
}