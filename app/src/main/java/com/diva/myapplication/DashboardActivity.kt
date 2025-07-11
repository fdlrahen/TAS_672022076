package com.diva.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.diva.myapplication.databinding.ActivityDashboardBinding
import com.diva.myapplication.dosen.DosenHomeFragment
import com.diva.myapplication.dosen.DosenMainActivity
import com.diva.myapplication.kaprodi.KaprodiMainActivity
import com.diva.myapplication.siswa.SiswaMainActivity

class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActivityDashboardBinding.inflate(layoutInflater).root)
        val pref = getSharedPreferences("user_session", MODE_PRIVATE)
        val role = pref.getString("role", "")
        val username = intent.getStringExtra("USERNAME")

        when (role) {
            "kaprodi" -> {
                val intent = Intent(this, KaprodiMainActivity::class.java)
                startActivity(intent)
                finish()
            }
            "dosen" -> {
                val intent = Intent(this, DosenMainActivity::class.java)
                intent.putExtra("NAMA_DOSEN", username)
                startActivity(intent)
                finish()
            }
            "siswa" -> {
                val intent = Intent(this, SiswaMainActivity::class.java)
                intent.putExtra("NAMA_SISWA", username)
                startActivity(intent)
                finish()
            }
        }
    }
}