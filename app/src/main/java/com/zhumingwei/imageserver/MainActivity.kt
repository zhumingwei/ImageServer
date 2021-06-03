package com.zhumingwei.imageserver

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.zhumingwei.imageserver.Util.getLocalIPAddress

class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"
    var handler:Handler? = null;
    val textview:TextView by lazy {
        findViewById<TextView>(R.id.textview)
    }
    val simpleHttpServer: SimpleHttpServer = SimpleHttpServer();
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        handler = Handler(Looper.getMainLooper())
        startLoading()
        if(mcheckPermission()){
            requestImageList()

        }

    }

    val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123
    fun mcheckPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {

//            // Should we show an explanation?
//            if (shouldShowRequestPermissionRationale(
//                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
//                // Explain to the user why we need to read the contacts
//            }

            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

            // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
            // app-defined int constant that should be quite unique

            return false;
        }else {
            return true;
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted.
            requestImageList()
        } else {
            // User refused to grant permission.
            finish()
        }
    }

    private fun requestImageList() {

        val imageList: List<String> = ImageProvider.getAllShownImagesPath(this)!!.filterNotNull().toList()
        Log.d(TAG, "requestImageList: size = ${imageList.size}")
        stopLoading()
        val ipAddress = getLocalIPAddress(this);
        textview.text = "局域网打开 ${ipAddress}:8080"
        textview.setOnClickListener {
            val uri: Uri = Uri.parse("http://${ipAddress}:8080")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)

        }
        simpleHttpServer.bindData(imageList);
        simpleHttpServer.start()
    }


    fun startLoading(){
        handler?.postDelayed(loopRunnable,300)
    }

    fun stopLoading(){
        handler?.removeCallbacks(loopRunnable)
    }

    var loadingCount = 1
    fun loopLoading(){
        var s = "loading"
        for (i:Int in 1..loadingCount){
            s += "."
        }
        textview.text = s
        loadingCount++
        if(loadingCount == 4){
            loadingCount = 1
        }
        handler?.postDelayed(loopRunnable,300)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLoading()
        simpleHttpServer.stop()
    }

    val loopRunnable = object :Runnable{
        override fun run() {
            loopLoading();
        }

    }
}