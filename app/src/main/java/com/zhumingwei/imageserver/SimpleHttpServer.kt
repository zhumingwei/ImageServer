package com.zhumingwei.imageserver

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.util.Log
import fi.iki.elonen.NanoHTTPD
import java.io.*


class SimpleHttpServer {
    val TAG = "SimpleHttpServer";
    var httpserver: InternalServer? = null
    var starting = false
    var imageList: List<String>? = null

    constructor()

    fun start() {
        if (starting) {
            Log.e(TAG, "start: areadyStart");
            return
        }
        starting = true;
        httpserver = InternalServer(null, 8080)
        httpserver!!.start()
    }

    fun stop() {
        httpserver?.stop()
        starting = false
    }

    fun bindData(imageList: List<String>) {
        this.imageList = imageList
    }

    inner class InternalServer(hostname: String?, port: Int) : NanoHTTPD(hostname, port) {

        override fun serve(session: IHTTPSession): Response {
            val uri = session.uri
            val parms = session.parms
            val header = session.headers
            if (uri == "/") {
                return handlerHost(session)
            } else if ((uri.endsWith("jpg") || uri.endsWith("png") || uri.endsWith("webp")) && File(uri).exists()) {
                return handlerImage(session)
            }

            return newFixedLengthResponse("un hood error <h1> ${uri} </h1>\n <h1>${parms}</h1> \n <h1>${header}</h1>");
        }

        private fun handlerImage(session: IHTTPSession): Response {
            val uri = session.uri
            val parms = session.parms
            val header = session.headers
            val file = File(uri);


            val thumb: Boolean = parms.containsKey("thumb")
            if(thumb){
                val THUMBSIZE = 64

                val thumbImage = ThumbnailUtils.extractThumbnail(
                    BitmapFactory.decodeFile(uri),
                    THUMBSIZE, THUMBSIZE
                )
                val outStream:ByteArrayOutputStream = ByteArrayOutputStream();
                thumbImage.compress(Bitmap.CompressFormat.PNG, 50, outStream)
                val inputStream:ByteArrayInputStream = ByteArrayInputStream(outStream.toByteArray())
                val length = outStream.size().toLong()
                outStream.close()
                return newFixedLengthResponse(
                    Response.Status.OK,
                    "image",
                    inputStream,
                    length
                );
            } else {
                var inputStream: InputStream? = FileInputStream(file)
                var length: Long = file.length();
                return newFixedLengthResponse(
                    Response.Status.OK,
                    "image",
                    inputStream,
                    length
                );
            }

        }

        private fun handlerHost(session: IHTTPSession): Response {
            var msg = "<html><body><h1>Hello,there is image list</h1>\n"
            val parms = session!!.parms
            if (imageList == null) {
                msg += "<a href='http://www.baidu.com'><h3>啥也没有</h3></a>\n";
            } else {
                imageList!!.forEach {
                    msg += "<a href='${it}'><h3>${it}  " +
                            "<img src='${it}?thumb'  alt=\"上海鲜花港 - 郁金香\" />" +
                            "</h3></a>\n";
                }
            }
            msg += "</body></html>\n"
            return newFixedLengthResponse("$msg</body></html>\n")
        }
    }
}
