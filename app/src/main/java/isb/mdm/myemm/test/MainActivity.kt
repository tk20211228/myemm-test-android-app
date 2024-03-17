package isb.mdm.myemm.test

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {
    companion object {
        const val OTHER_PERMISSIONS_REQUEST_CODE = 101
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
        findViewById<Button>(R.id.button_caCert_list).setOnClickListener {
            val intent = Intent(this, CertificateListActivity::class.java)
            startActivity(intent)
        }

    }
    private fun setupPermissions() {
        val permissionsRequired = getAllPermissions()

        val permissionsNotGranted = permissionsRequired.filterNot { isPermissionGranted(it) }

        if (permissionsNotGranted.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNotGranted.toTypedArray(), OTHER_PERMISSIONS_REQUEST_CODE)
        }
    }
    
    private fun getAllPermissions(): List<String> {
        // Base permissions that are required regardless of the Android version.
        return mutableListOf(
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
        ).apply {
            // Permissions for Android 10 (API level 29) and above.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                add(Manifest.permission.ACTIVITY_RECOGNITION)
            }
        }
    }
    
    private fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }
    
    override fun onRequestPermissionsResult(requestCode: Int,permissions: Array<String>,grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val deniedPermissions = permissions.zip(grantResults.toTypedArray())
            .filter { it.second != PackageManager.PERMISSION_GRANTED }
            .map { it.first }

        if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            showAllPermissionsGrantedDialog()
        } else {
            showDeniedPermissionsDialog(deniedPermissions)
        }

    }

    private fun showAllPermissionsGrantedDialog() {
        AlertDialog.Builder(this)
            .setTitle("パーミッション承認")
            .setMessage("全てのパーミッションが承認されました！")
            .setPositiveButton("OK", null)
            .show()
    }
    private fun showDeniedPermissionsDialog(deniedPermissions: List<String>) {
        val deniedPermissionsList = deniedPermissions.joinToString("\n")
        AlertDialog.Builder(this)
            .setTitle("パーミッション拒否")
            .setMessage("以下のパーミッションが拒否されました\n" +
                        "このアプリの機能を利用するためには、設定から権限を有効にしてください。\n"+
                        "$deniedPermissionsList")
            .setPositiveButton("設定") { dialog, which ->
                openAppSettings()
            }
            .setNegativeButton("キャンセル", null)
            .setCancelable(false) // ダイアログ外のタップやバックボタンを無効化
            .create()
            .show()
    }
    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }
}