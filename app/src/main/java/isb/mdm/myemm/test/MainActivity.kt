package isb.mdm.myemm.test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    companion object {
        const val CAMERA_REQUEST_CODE = 101
        const val OTHER_PERMISSIONS_REQUEST_CODE = 102
//        const val ACTIVITY_RECOGNITION_REQUEST_CODE = 103
//        const val BODY_SENSORS_REQUEST_CODE = 104
//        const val READ_CALENDAR_REQUEST_CODE = 105
//        const val WRITE_CALENDAR_REQUEST_CODE = 106
//        const val READ_CONTACTS_REQUEST_CODE = 107
//        const val WRITE_CONTACTS_REQUEST_CODE = 108
//        const val ACCESS_FINE_LOCATION_REQUEST_CODE = 109
//        const val ACCESS_COARSE_LOCATION_REQUEST_CODE = 110
//        const val RECORD_AUDIO_REQUEST_CODE = 111
//        const val READ_PHONE_STATE_REQUEST_CODE = 112
//        const val CALL_PHONE_REQUEST_CODE = 113
//        const val READ_CALL_LOG_REQUEST_CODE = 114
//        const val WRITE_CALL_LOG_REQUEST_CODE = 115
//        const val SEND_SMS_REQUEST_CODE = 116
//        const val RECEIVE_SMS_REQUEST_CODE = 117
//        const val READ_SMS_REQUEST_CODE = 118
//        const val RECEIVE_WAP_PUSH_REQUEST_CODE = 119
//        const val RECEIVE_MMS_REQUEST_CODE = 120
//        const val READ_EXTERNAL_STORAGE_REQUEST_CODE = 121
//        const val WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 122

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupPermissions()
    }
    private fun setupPermissions() {
        val permissionsRequired = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACTIVITY_RECOGNITION,
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
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // パーミッションが承認された時の処理
                    AlertDialog.Builder(this)
                        .setTitle("パーミッション承認")
                        .setMessage("カメラパーミッションが承認されました！")
                        .setPositiveButton("OK") { dialog, which ->
                            // アプリの設定画面を開く
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri = Uri.fromParts("package", packageName, null)
                            intent.data = uri
                            startActivity(intent)

                        }
                        .show()
                } else {
                    // ユーザーがパーミッションを拒否した時の処理
                }
            }
            OTHER_PERMISSIONS_REQUEST_CODE -> {
                // 他のパーミッションの結果の処理
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    // すべてのパーミッションが承認された場合の処理

                    // アプリの設定画面を開く
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } else {
                    // 一部またはすべてのパーミッションが拒否された場合の処理
                    // アプリの設定画面を開く
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }
            }




        }
    }


}