package com.example.openxcam
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.openxcam.GalleryFragment
import com.example.openxcam.MediaCaptureFragment
import com.example.openxcam.R
import com.example.openxcam.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Definiere die erforderlichen Berechtigungen
    companion object {
        val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
    private const val PERMISSION_REQUEST_CODE = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Öffne das GalleryFragment, wenn der Galerie-Button geklickt wird
        binding.btnOpenGallery.setOnClickListener {
            if (allPermissionsGranted()) {
                replaceFragment(GalleryFragment())
            } else {
                requestPermissions()
            }
        }

        // Öffne das MediaCaptureFragment, wenn der Aufnahme-Button geklickt wird
        binding.btnOpenCamera.setOnClickListener {
            if (allPermissionsGranted()) {
                replaceFragment(MediaCaptureFragment())
            } else {
                requestPermissions()
            }
        }
    }

    private fun allPermissionsGranted(): Boolean {
        return REQUIRED_PERMISSIONS.all {
            ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSION_REQUEST_CODE)
    }

    private fun openGalleryFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, GalleryFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun openMediaCaptureFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, MediaCaptureFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, fragment)
            addToBackStack(null)
            commit()
        }
    }


}

