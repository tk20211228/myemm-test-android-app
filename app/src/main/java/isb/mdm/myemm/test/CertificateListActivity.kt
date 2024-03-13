package isb.mdm.myemm.test

import android.app.admin.DevicePolicyManager
import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

class CertificateListActivity : AppCompatActivity() {
    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_certificate_list)

        listView = findViewById(R.id.certificate_list_view)

        // CA証明書リストを読み込む
        loadInstalledCaCerts()
    }

    private fun loadInstalledCaCerts() {
        val dpm = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        try {
            // システムにインストールされているCA証明書を取得
            val caBytesList = dpm.getInstalledCaCerts(null)

            if (caBytesList.isEmpty()) {
                // 証明書のリストが空の場合、ユーザーにトーストで通知
                Toast.makeText(this, "インストールされているCA証明書はありません。", Toast.LENGTH_LONG).show()
                return
            }

            val certList = caBytesList.mapNotNull { bytes ->
                // X.509証明書フォーマットにデコード
                try {
                    val certificate = CertificateFactory.getInstance("X.509").generateCertificate(bytes.inputStream())
                    certificate as? X509Certificate
                } catch (e: Exception) {
                    null // パースに失敗した場合は無視
                }
            }

            // ListView用のアダプターを作成
            // 証明書のSubject DNをリスト項目として表示する
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, certList.map { it.subjectDN.toString() })
            listView.adapter = adapter
        } catch (e: SecurityException) {
            // ユーザーにフレンドリーなエラーメッセージを表示
            Toast.makeText(this, "権限がありません。", Toast.LENGTH_LONG).show()

            // エラーログをアプリケーションのログファイルやデバッグウィンドウに記録
            logErrorDetails(e)
        } catch (e: Exception) {
            // その他のエラーハンドリング
            Toast.makeText(this, "想定外のエラーが発生しました。", Toast.LENGTH_LONG).show()

            // エラーログをアプリケーションのログファイルやデバッグウィンドウに記録
            logErrorDetails(e)
        }

    }
    // ヘルパー関数: エラーの詳細をロギングする
    private fun logErrorDetails(e: Exception) {
        // 実際のロギングメカニズムで例外のスタックトレースを記録
        e.printStackTrace() // これはコンソールに出力するだけの例です
    }
}