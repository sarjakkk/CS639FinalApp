package com.safe.resident.pro.app

import android.Manifest.permission.CAMERA
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.safe.resident.pro.app.data.Incident
import com.safe.resident.pro.app.data.User
import com.safe.resident.pro.app.databinding.ActivityAccountBinding
import java.io.ByteArrayOutputStream

class AccountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountBinding
    private lateinit var database: DatabaseReference
    private lateinit var userId: String
    private var PERMISSION_REQUEST_CODE: Int = 1114
    private var CAMERA: Int = 1115
    private var GALLERY: Int = 1116
    private var scopedStoragePermission: Int = PackageManager.PERMISSION_DENIED
    private lateinit var cameraActivityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_account)

        database = FirebaseDatabase.getInstance().reference
        val sharedPrefs = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        userId = sharedPrefs.getString("email", "") ?: ""
        setupViews()
        loadUserData()
        loadIncidents()


        cameraActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.extras?.get("data")?.let { photo ->
                    if (photo is Bitmap) {
                        uploadImageToFirebase(photo)
                    }
                }
            } else {
                Toast.makeText(this, "Camera canceled", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun requestScopedStoragePermission() {
        AlertDialog.Builder(this)
            .setTitle("Scoped Storage Permission Required")
            .setMessage("This app needs access to manage all files on your device to upload your profile picture. Please grant this permission to proceed.")
            .setPositiveButton("OK") { dialog, which ->
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                val uri = Uri.fromParts("package", packageName, null)
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && scopedStoragePermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.CAMERA
                ),
                PERMISSION_REQUEST_CODE
            )
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.CAMERA,
                ),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun decodeBase64ToBitmap(base64Str: String): Bitmap? {
        return try {
            val decodedBytes = Base64.decode(base64Str.substring(base64Str.indexOf(",") + 1), Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: IllegalArgumentException) {
            Log.e("AccountActivity", "Base64 decoding error", e)
            null
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            var allPermissionsGranted = true
            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false
                    break
                }
            }

            if (allPermissionsGranted) {
                Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show()
            } else {
                if (shouldShowRequestPermissionRationale(permissions)) {
                    AlertDialog.Builder(this)
                        .setTitle("Permissions Required")
                        .setMessage("This app needs camera and storage permissions to allow you to upload your profile picture. Please grant these permissions to proceed.")
                        .setPositiveButton("OK") { dialog, which ->
                            requestPermissions()
                        }
                        .setNegativeButton("Cancel") { dialog, which ->
                            dialog.dismiss()
                        }
                        .create()
                        .show()
                } else {
                    AlertDialog.Builder(this)
                        .setTitle("Permission Denied")
                        .setMessage("You have denied some permissions permanently. If you want to use this feature, please enable the permissions in the settings.")
                        .setPositiveButton("Go to Settings") { dialog, which ->
                            // Open app details settings to let user enable permissions
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri = Uri.fromParts("package", packageName, null)
                            intent.data = uri
                            startActivity(intent)
                        }
                        .setNegativeButton("Cancel") { dialog, which ->
                            dialog.dismiss()
                        }
                        .create()
                        .show()
                }
            }
        }
    }
    private fun showImagePickerOptions() {
        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder = AlertDialog.Builder(this)
        builder.setItems(options) { dialog, item ->
            when (options[item]) {
                "Take Photo" -> takePhotoFromCamera()
                "Choose from Gallery" -> choosePhotoFromGallery()
                "Cancel" -> dialog.dismiss()
            }
        }
        builder.show()
    }

    private fun takePhotoFromCamera() {
        val cameraPermission = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)

        if (cameraPermission == PackageManager.PERMISSION_GRANTED) {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraActivityResultLauncher.launch(takePictureIntent)
        } else {
            requestPermissions()
        }
    }

    private fun choosePhotoFromGallery() {
        val pickPhotoIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickPhotoIntent, GALLERY)
    }
    private fun uploadImageToFirebase(bitmap: Bitmap) {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val base64Image = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)

        // Upload to Firebase
        database.child("users").child(userId).child("profile_pic").setValue(base64Image)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (base64Image != null && base64Image.isNotEmpty()) {
                        val bitmap = decodeBase64ToBitmap(base64Image)
                        if (bitmap != null) {
                            binding.ivProfileImage.setImageBitmap(bitmap)
                        } else {
                            Log.e("AccountActivity", "Failed to decode base64 to Bitmap")
                        }
                    }
                    Toast.makeText(this, "Profile picture updated!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to update profile picture!", Toast.LENGTH_SHORT).show()
                }
            }
    }
    private fun shouldShowRequestPermissionRationale(permissions: Array<out String>): Boolean {
        for (permission in permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                return true
            }
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA -> {
                    val photo = data?.extras?.get("data") as Bitmap
                    uploadImageToFirebase(photo)
                }
                GALLERY -> {
                    val uri = data?.data
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                    uploadImageToFirebase(bitmap)
                }
            }
        }
    }
    private fun setupViews() {
        binding.tvChangePassword.setOnClickListener {
            togglePasswordVisibility()
        }

        binding.tvSave.setOnClickListener {
            updatePassword()
        }
    }

    private fun togglePasswordVisibility() {
        binding.tvPassword.visibility = View.GONE
        binding.etPassword.visibility = View.VISIBLE
        binding.tvSave.visibility = View.VISIBLE
    }

    private fun updatePassword() {
        val newPassword = binding.etPassword.text.toString().trim()
        if (newPassword.isNotEmpty()) {
            database.child("users").child(userId).child("password").setValue(newPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        binding.etPassword.visibility = View.GONE
                        binding.tvSave.visibility = View.GONE
                        binding.tvPassword.visibility = View.VISIBLE
                    } else {
                        Log.e("TAG", "updatePassword: ${task.exception!!.message}", )
                    }
                }
        }
    }

    private fun loadUserData() {
        database.child("users").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                user?.let {
                    binding.tvEmail.text = it.email
                    binding.tvPassword.text = "********"

                    val base64Image = it.profile_pic
                    if (base64Image != null && base64Image.isNotEmpty()) {
                        val bitmap = decodeBase64ToBitmap(base64Image)
                        if (bitmap != null) {
                            binding.ivProfileImage.setImageBitmap(bitmap)
                        } else {
                            Log.e("AccountActivity", "Failed to decode base64 to Bitmap")
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "onCancelled: ${databaseError.message}", )
            }
        })
    }

    private fun loadIncidents() {
        database.child("incidents").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var totalIncidents = 0
                for (snapshot in dataSnapshot.children) {
                    val incident = snapshot.getValue(Incident::class.java)
                    if (incident != null && incident.userID == userId) {
                        totalIncidents++
                    }
                }
                binding.tvTotalIncident.text = "Submitted Incidents: $totalIncidents"
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
            }
        })
    }
}