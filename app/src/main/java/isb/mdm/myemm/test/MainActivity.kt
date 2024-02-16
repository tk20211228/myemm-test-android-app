package isb.mdm.myemm.test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.security.KeyChain
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class MainActivity : AppCompatActivity() {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    companion object {
        const val CAMERA_REQUEST_CODE = 101
        const val OTHER_PERMISSIONS_REQUEST_CODE = 102
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupPermissions()

        // ボタンのクリックリスナーを設定
        findViewById<Button>(R.id.button_app_info).setOnClickListener {
            // アプリの設定画面を開く
            openAppSettings()
        }
        findViewById<Button>(R.id.button_cert_install).setOnClickListener {
            val inputPath = "https://www.dropbox.com/scl/fi/r21tl24jg3xp5vtto6npp/p12.p12?rlkey=5ka81ac25cfaqhxeip2f1bgz9&dl=1"
//            val fileName = "downloaded_certificate.p12"
//            val outputPath = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName).path
//            downloadCertificate(inputPath, outputPath)
            // 外部ストレージのダウンロードディレクトリを取得
            val downloadsDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            // 一時ファイルを作成
            val tempFile = File.createTempFile("downloaded_certificate", ".p12", downloadsDir)
            // 一時ファイルのパスをダウンロード先として使用
            downloadCertificate(inputPath, tempFile.absolutePath)
        }
    }
    private fun setupPermissions() {
        val permissionsRequired = mutableListOf(
            Manifest.permission.CAMERA,
            Manifest.permission.BODY_SENSORS,

            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,

            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,

            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.WRITE_CALL_LOG,

            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_WAP_PUSH,
            Manifest.permission.RECEIVE_MMS,

            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        // Android 10 (APIレベル 29) 以降の場合のみ、ACTIVITY_RECOGNITIONパーミッションを追加
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissionsRequired.add(Manifest.permission.ACTIVITY_RECOGNITION)
        }

        val permissionsNotGranted = permissionsRequired.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsNotGranted.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNotGranted.toTypedArray(), OTHER_PERMISSIONS_REQUEST_CODE)
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val deniedPermissions = permissions.zip(grantResults.toTypedArray())
            .filter { it.second != PackageManager.PERMISSION_GRANTED }
            .map { it.first }

        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // パーミッションが承認された時の処理
                    AlertDialog.Builder(this)
                        .setTitle("パーミッション承認")
                        .setMessage("カメラパーミッションが承認されました！")
                        .show()
                } else {
                    // ユーザーがパーミッションを拒否した時の処理
                    AlertDialog.Builder(this)
                        .setTitle("パーミッション承認")
                        .setMessage("カメラパーミッションが拒否されました")
                        .setPositiveButton("OK") { dialog, which ->
                            // アプリの設定画面を開く
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri = Uri.fromParts("package", packageName, null)
                            intent.data = uri
                            startActivity(intent)
                        }
                        .show()
                }
            }
            OTHER_PERMISSIONS_REQUEST_CODE -> {
                // 他のパーミッションの結果の処理
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    // すべてのパーミッションが承認された場合の処理
                    // パーミッションが承認された時の処理
                    AlertDialog.Builder(this)
                        .setTitle("パーミッション承認")
                        .setMessage("全てのパーミッションが承認されました！")
                        .show()
                } else {
                    // 一部またはすべてのパーミッションが拒否された場合の処理
                    // パーミッションが承認された時の処理
                    // 拒否されたパーミッションをリスト表示する
                    val deniedPermissionsList = deniedPermissions.joinToString("\n")
                    AlertDialog.Builder(this)
                        .setTitle("パーミッション承認")
                        .setMessage("以下のパーミッションが拒否されました:\n$deniedPermissionsList")
                        .setPositiveButton("OK") { dialog, which ->
                            // アプリの設定画面を開く
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri = Uri.fromParts("package", packageName, null)
                            intent.data = uri
                            startActivity(intent)
                        }
                        .show()
                }
            }
        }
    }
    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }
    private fun installCertificate(certificateBytes: ByteArray) {
        val intent = KeyChain.createInstallIntent()
        intent.putExtra(KeyChain.EXTRA_CERTIFICATE, certificateBytes)
        intent.putExtra(KeyChain.EXTRA_NAME, "Certificate Name")
        startActivity(intent)
    }

    private fun installCertificate(context: Context, certificateBytes: ByteArray, certificateName: String) {
        try {
            // 証明書をインストールするためのIntentを作成
            val installIntent = KeyChain.createInstallIntent()
            installIntent.putExtra(KeyChain.EXTRA_CERTIFICATE, certificateBytes)
            installIntent.putExtra(KeyChain.EXTRA_NAME, certificateName)

            // 証明書インストールのIntentを開始
            Log.e("MainActivity", "証明書のインストール開始")
            context.startActivity(installIntent)
            Log.e("MainActivity", "証明書のインストール終了")
        } catch (e: Exception) {
            Log.e("MainActivity", "証明書のインストールに失敗しました。", e)
            // UIスレッドでToastを表示
            runOnUiThread {
                Toast.makeText(context, "証明書のインストールに失敗しました。", Toast.LENGTH_SHORT).show()
            }
        }
    }
    // 証明書をダウンロードしてインストールする処理
    private fun downloadCertificate(urlString: String, outputPath: String) {
        coroutineScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    // ダウンロード処理...
                    val url = URL(urlString)
                    val connection = url.openConnection()
                    BufferedInputStream(connection.getInputStream()).use { input ->
                        FileOutputStream(outputPath).use { output ->
                            val dataBuffer = ByteArray(1024)
                            var bytesRead: Int
                            while (input.read(dataBuffer).also { bytesRead = it } != -1) {
                                output.write(dataBuffer, 0, bytesRead)
                            }
                        }
                    }
                    // ダウンロードしたファイルを読み込み、バイト配列を取得
                    val file = File(outputPath)
                    val certificateBytes = file.readBytes()
                    // ダウンロード成功後、証明書をインストール
                    certificateBytes.let {
                        withContext(Dispatchers.Main) {
                            installCertificate(this@MainActivity, it, "downloaded_certificate")
                        }
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("MainActivity", "証明書のダウンロードに失敗しました。", e)
                    Toast.makeText(this@MainActivity, "証明書のダウンロードに失敗しました。", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel() // アクティビティ破棄時にコルーチンをキャンセル
    }


}