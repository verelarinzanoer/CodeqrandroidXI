package com.verel.codeqr_xi

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.verel.codeqr_xi.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var codeScanner: CodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getPermission()
        qRScanner(binding)
        reScan(binding)

        binding.cardRescan.setOnClickListener {
            reScan(binding)
        }

        binding.cardCopy.setOnClickListener{
            copyQR(binding)
        }

        binding.cardShare.setOnClickListener{
            shareCopy(binding)
        }
    }

    private fun getPermission() {
        val permission = ContextCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA)
        if (permission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                this, arrayOf(android.Manifest.permission.CAMERA), CAMERA_REQ
            )
        }
    }

    private fun shareCopy(binding: ActivityMainBinding) {

        val sendIntent : Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, binding.textQR.text)
            type = "text/plain"

        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)

    }

    override fun onPause() {
        super.onPause()
        codeScanner.releaseResources()
    }

    private fun copyQR(binding: ActivityMainBinding) {

        val  clipBoard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip : ClipData = ClipData.newPlainText("siple text", binding.textQR.text)
        clipBoard.setPrimaryClip(clip)

        Toast.makeText(this, "-${binding.textQR.text} - is copied to clipboard",
        Toast.LENGTH_SHORT).show()
    }

    private fun reScan(binding: ActivityMainBinding) {
        codeScanner.startPreview()
        binding.textQR.text = "scanning..."
    }

    private fun qRScanner(binding: ActivityMainBinding) {
        codeScanner = CodeScanner(this,binding.scanView)
        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS
            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.SINGLE
            isAutoFocusEnabled = true
            isFlashEnabled = false
            decodeCallback = DecodeCallback {
                runOnUiThread {
                    binding.textQR.text = it.text
                }
            }

            errorCallback = ErrorCallback {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Camera initialization error: ${it.message}",
                    Toast.LENGTH_LONG).show()
                }
            }
            binding.scanView.setOnClickListener {
                reScan(binding)
        }

        }
    }



    companion object{
        private const val CAMERA_REQ = 101
    }


}