package com.example.openxcam
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.openxcam.databinding.FragmentGalleryBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

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

        binding.saveButton.setOnClickListener {
            saveFilteredBitmap()
        }

        // Konfigurieren Sie den Spinner
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.filter_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerFilter.adapter = adapter
        }

        binding.spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    when (position) {
                    0 -> applyOriginalFilter()
                    1 -> applyBlackWhiteFilter()
                    2 -> applyGreenFilter()
                    3 -> applyRedFilter()
                    4 -> applyBlueFilter()
                    5 -> applyBrownFilter()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun saveFilteredBitmap() {
        val imageView = binding.imageView

        // Erstellen Sie ein Bitmap basierend auf der Größe der ImageView
        val bitmap = Bitmap.createBitmap(imageView.width, imageView.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        imageView.draw(canvas)

        // Speichern Sie das Bitmap im externen Speicher
        val filename = "filtered_image_${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null
        var imageUri: Uri? = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }

            val contentResolver = requireContext().contentResolver
            imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            fos = imageUri?.let { contentResolver.openOutputStream(it) }
        } else {
            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
            val image = File(imagesDir, filename)
            imageUri = Uri.fromFile(image)
            fos = FileOutputStream(image)
        }

        fos?.use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Toast.makeText(requireContext(), "Bild gespeichert", Toast.LENGTH_SHORT).show()
        }

        // Informieren Sie das System über das neue Bild
        imageUri?.let {
            requireContext().sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, it))
        }
    }


    private fun applyOriginalFilter() {
        binding.imageView.colorFilter = null
    }

    private fun applyBlackWhiteFilter() {
        val imageView = binding.imageView
        val colorMatrix = ColorMatrix()
        colorMatrix.setSaturation(0f)
        val filter = ColorMatrixColorFilter(colorMatrix)
        imageView.colorFilter = filter
    }

    private fun applyGreenFilter() {
        val imageView = binding.imageView
        val colorMatrix = ColorMatrix(floatArrayOf(
            0f, 0f, 0f, 0f, 0f, // Rot-Kanal
            0f, 1f, 0f, 0f, 0f, // Grün-Kanal
            0f, 0f, 0f, 0f, 0f, // Blau-Kanal
            0f, 0f, 0f, 1f, 0f  // Alpha-Kanal
        ))
        val filter = ColorMatrixColorFilter(colorMatrix)
        imageView.colorFilter = filter
    }

    private fun applyRedFilter() {
        val imageView = binding.imageView
        val colorMatrix = ColorMatrix(floatArrayOf(
            1f, 0f, 0f, 0f, 0f, // Rot-Kanal
            0f, 0f, 0f, 0f, 0f, // Grün-Kanal
            0f, 0f, 0f, 0f, 0f, // Blau-Kanal
            0f, 0f, 0f, 1f, 0f  // Alpha-Kanal
        ))
        val filter = ColorMatrixColorFilter(colorMatrix)
        imageView.colorFilter = filter
    }

    private fun applyBrownFilter() {
        val imageView = binding.imageView
        val sepiaMatrix = ColorMatrix()
        sepiaMatrix.set(floatArrayOf(
            0.393f, 0.769f, 0.189f, 0f, 0f,
            0.349f, 0.686f, 0.168f, 0f, 0f,
            0.272f, 0.534f, 0.131f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        ))
        val filter = ColorMatrixColorFilter(sepiaMatrix)
        imageView.colorFilter = filter
    }

    private fun applyBlueFilter() {
        val imageView = binding.imageView
        val colorMatrix = ColorMatrix(floatArrayOf(
            0f, 0f, 0f, 0f, 0f, // Rot-Kanal
            0f, 0f, 0f, 0f, 0f, // Grün-Kanal
            0f, 0f, 1f, 0f, 0f, // Blau-Kanal
            0f, 0f, 0f, 1f, 0f  // Alpha-Kanal
        ))
        val filter = ColorMatrixColorFilter(colorMatrix)
        imageView.colorFilter = filter
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