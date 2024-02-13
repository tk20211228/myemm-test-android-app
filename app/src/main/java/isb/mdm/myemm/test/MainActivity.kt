package isb.mdm.myemm.test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    companion object {
        const val CAMERA_REQUEST_CODE = 101
        const val OTHER_PERMISSIONS_REQUEST_CODE = 102
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupPermissions()

        // ボタンのクリックリスナーを設定
        val buttonAppInfo = findViewById<Button>(R.id.button_app_info)
        buttonAppInfo.setOnClickListener {
            // アプリの設定画面を開く
            openAppSettings()
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

}