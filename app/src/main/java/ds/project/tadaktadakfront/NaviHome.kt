package ds.project.tadaktadakfront

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import ds.project.tadaktadakfront.databinding.FragmentNaviHomeBinding
import kotlinx.android.synthetic.main.fragment_navi_home.*
import kotlinx.android.synthetic.main.fragment_navi_home.view.*
import java.io.File



class NaviHome : Fragment() {
    var selectedImage: Uri? = null
    var selectedBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?


    ): View? {
        return inflater.inflate(R.layout.fragment_navi_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        btnGallery.setOnClickListener{
            select_imge(it)
        }

        val requestCameraThumbnailLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult() ) {
            val bitmap = it?.data?.extras?.get("data") as Bitmap
            select_ImageView.setImageBitmap(bitmap)

        }

        btnCamera.setOnClickListener{
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            requestCameraThumbnailLauncher.launch(intent)
        }


    }
    fun select_imge(view: View){
        activity?.let{
        if(ContextCompat.checkSelfPermission(it.applicationContext, android.Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
          requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1)
        } else {
            val Intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(Intent, 2)
            }
        }
    }

    fun capture_imge(view: View){
        activity?.let{
            if(ContextCompat.checkSelfPermission(it.applicationContext, android.Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED){
                dispatchTakePictureIntent()
            } else {
                requestPermissions(arrayOf(android.Manifest.permission.CAMERA),1)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode ==1){
            if(grantResults.size > 0 && grantResults[0] ==PackageManager.PERMISSION_GRANTED){
                val intent=Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent,2)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private val REQUEST_IMAGE_CAPTURE=2
    private fun dispatchTakePictureIntent(){
        val pm = activity!!.packageManager

        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also{ takePictureIntent->
            takePictureIntent.resolveActivity(pm)?.also{
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 2 && resultCode == Activity.RESULT_OK && data != null){
            selectedImage = data.data
        }
        try {
            context?.let {
                if (selectedImage != null) {
                    if (Build.VERSION.SDK_INT >= 28) {
                        val source =ImageDecoder.createSource(it.contentResolver, selectedImage!!)
                        selectedBitmap =ImageDecoder.decodeBitmap(source)
                        select_ImageView.setImageBitmap(selectedBitmap)
                    }else{
                        selectedBitmap = MediaStore.Images.Media.getBitmap(it.contentResolver, selectedImage)
                        select_ImageView.setImageBitmap(selectedBitmap)
                    }
                }
            }
        } catch (e:Exception){
            e.printStackTrace()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}

