package com.example.openxcam
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.openxcam.databinding.FragmentGalleryBinding

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    private val PICK_IMAGE_REQUEST = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnOpenGallery.setOnClickListener {
            openGallery()
        }
        binding.rotateButton.setOnClickListener {
            rotateImage()
        }
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri = data.data

            // Verzögern Sie die Skalierung des Bildes, bis die ImageView gemessen wurde
            binding.imageView.post {
                val bitmap = decodeBitmap(selectedImageUri)
                binding.imageView.setImageBitmap(bitmap)
            }
        }
    }

    private fun decodeBitmap(imageUri: Uri?): Bitmap? {
        // Verwenden Sie die Abmessungen der ImageView oder Standardwerte, falls diese noch nicht gemessen wurde
        val targetW: Int = if (binding.imageView.width == 0) 800 else binding.imageView.width
        val targetH: Int = if (binding.imageView.height == 0) 600 else binding.imageView.height

        val bmOptions = BitmapFactory.Options().apply {
            // Get the dimensions of the bitmap
            inJustDecodeBounds = true
            BitmapFactory.decodeStream(imageUri?.let { context?.contentResolver?.openInputStream(it) }, null, this)

            // Determine how much to scale down the image
            val scaleFactor: Int = Math.max(1, Math.min(outWidth / targetW, outHeight / targetH))

            // Decode the image file into a Bitmap sized to fill the View
            inJustDecodeBounds = false
            inSampleSize = scaleFactor
        }

        return imageUri?.let { uri ->
            context?.contentResolver?.openInputStream(uri).use { stream ->
                BitmapFactory.decodeStream(stream, null, bmOptions)
            }
        }
    }

    private fun rotateImage() {
        binding.imageView.rotation = (binding.imageView.rotation + 90) % 360
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}