package com.lion.cafemanager_1.fragment

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.ContentResolver
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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import com.lion.cafemanager_1.MainActivity
import com.lion.cafemanager_1.R
import com.lion.cafemanager_1.databinding.FragmentInputBinding
import com.lion.cafemanager_1.repository.CafeRepository
import com.lion.cafemanager_1.util.CafeSize
import com.lion.cafemanager_1.util.CafeTakeOut
import com.lion.cafemanager_1.util.CafeType
import com.lion.cafemanager_1.util.FragmentName
import com.lion.cafemanager_1.viewmodel.CafeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class InputFragment : Fragment() {
    lateinit var fragmentInputBinding: FragmentInputBinding
    lateinit var mainActivity: MainActivity
    lateinit var albumLauncher: ActivityResultLauncher<Intent>
    lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    var selectedUri: Uri? = null
    var filePath = ""

    companion object {
        const val REQUEST_CAMERA_PERMISSION = 100
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentInputBinding = FragmentInputBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        // 이미지 선택 버튼 클릭 이벤트 처리
        settingImage()

        // Toolbar 설정
        settingToolbar()

        // 앨범 실행 기능 설정
        createAlbumLauncher()

        // 카메라 실행 기능 설정
        createCameraLauncher()

        return fragmentInputBinding.root
    }

    fun settingToolbar() {
        fragmentInputBinding.apply {
            toolbarInput.title = "카페 메뉴 입력"
            toolbarInput.setNavigationIcon(R.drawable.arrow_back_24px)
            toolbarInput.setNavigationOnClickListener {
                mainActivity.removeFragment(FragmentName.INPUT_FRAGMENT)
            }

            toolbarInput.inflateMenu(R.menu.toolbar_input_menu)
            toolbarInput.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.toolbar_input_menu_done -> {
                        inputDone()
                    }
                }
                true
            }
        }
    }

    // 이미지 선택 버튼 설정
    fun settingImage() {
        fragmentInputBinding.buttonCafeMenuPicture.setOnClickListener {
            val albumIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*"))
            }
            albumLauncher.launch(albumIntent)
        }

        fragmentInputBinding.buttonCafeMenuTakePicture.setOnClickListener {
            // 카메라 권한이 있다고 가정하고 바로 카메라를 실행
            if (ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                // 카메라 권한이 승인된 경우 카메라 실행
                openCamera()
            } else {
                // 카메라 권한 요청
                ActivityCompat.requestPermissions(mainActivity, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
            }
        }
    }

    // 카메라 권한 요청 후 결과 처리
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 승인되면 카메라 실행
                openCamera()
            } else {
                // 권한이 거부되었을 때 경고 메시지 표시
                showAlertDialog("카메라 권한이 필요합니다.")
            }
        }
    }

    // 카메라에서 찍은 사진을 저장할 파일을 생성하는 함수
    @Throws(IOException::class)
    private fun createImageFile(): File {
        val storageDir = mainActivity.getExternalFilesDir(null)
        return File.createTempFile("temp_image_", ".jpg", storageDir).apply {
            filePath = absolutePath
        }
    }

    // 카메라 실행 함수
    private fun openCamera() {
        val photoFile: File? = try {
            createImageFile() // 이미지 파일 생성
        } catch (ex: IOException) {
            null
        }

        photoFile?.also {
            val photoURI: Uri = FileProvider.getUriForFile(mainActivity, "com.lion.cafemanager_1.fileprovider", it
            )
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, photoURI) // 카메라 앱에서 찍은 이미지를 저장할 위치
            }
            cameraLauncher.launch(cameraIntent) // 카메라 실행
        }
    }

    // 앨범 선택 후 이미지를 선택하는 Launcher 설정
    fun createAlbumLauncher() {
        albumLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == AppCompatActivity.RESULT_OK && it.data != null) {
                it.data?.data?.let { uri ->
                    selectedUri = uri
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        val source = ImageDecoder.createSource(mainActivity.contentResolver, uri)
                        val bitmap = ImageDecoder.decodeBitmap(source)
                        fragmentInputBinding.imageView.setImageBitmap(bitmap)
                    } else {
                        val cursor = mainActivity.contentResolver.query(uri, null, null, null, null)
                        cursor?.use {
                            if (it.moveToFirst()) {
                                val idx = it.getColumnIndex(MediaStore.Images.Media.DATA)
                                val path = it.getString(idx)
                                selectedUri = Uri.parse(path)
                                val bitmap = BitmapFactory.decodeFile(path)
                                fragmentInputBinding.imageView.setImageBitmap(bitmap)
                            }
                        }
                    }
                }
            }
        }
    }

    // 카메라 촬영 후 사진을 처리하는 Launcher 설정
    fun createCameraLauncher() {
        cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val takenImageUri = Uri.fromFile(File(filePath))
                selectedUri = takenImageUri
                fragmentInputBinding.imageView.setImageURI(takenImageUri)
            }
        }
    }

    // 완료 버튼 클릭 시 데이터 저장
    fun inputDone() {
        fragmentInputBinding.apply {
            val cafeName = textInputLayoutCafeMenuName.editText?.text.toString()
            val cafePriceString = textInputLayoutCafeMenuPrice.editText?.text.toString()
            val cafeExplain = textFieldMenuExplain.editableText.toString()

            if (cafeName.isEmpty() || cafePriceString.isEmpty() || cafeExplain.isEmpty()) {
                showAlertDialog("모든 항목을 입력해야 등록됩니다.")
                return
            }

            val cafePrice = cafePriceString.toIntOrNull() ?: 0

            val cafeType = when (toggleCafeType.checkedButtonId) {
                R.id.buttonCoffee -> CafeType.CAFE_TYPE_COFFEE
                R.id.buttonNonCoffee -> CafeType.CAFE_TYPE_NONCOFFEE
                R.id.buttonBread -> CafeType.CAFE_TYPE_BREAD
                R.id.buttonCake -> CafeType.CAFE_TYPE_CAKE
                else -> CafeType.CAFE_TYPE_COFFEE
            }

            val cafeSize = when (toggleCafeSize.checkedButtonId) {
                R.id.buttonSizeLarge -> CafeSize.CAFE_SIZE_LARGE
                R.id.buttonSizeMedium -> CafeSize.CAFE_SIZE_MEDIUM
                R.id.buttonSizeSmall -> CafeSize.CAFE_SIZE_SMALL
                else -> CafeSize.CAFE_SIZE_NO
            }

            val cafeTakeOut = when (radioGroupTakeOut.checkedRadioButtonId) {
                R.id.radioButtonTakeOutOk -> CafeTakeOut.CAFE_TAKEOUT_OK
                else -> CafeTakeOut.CAFE_TAKEOUT_NO
            }

            val cafeStock = textInputLayoutCafeMenuStock.editText?.text.toString().toIntOrNull() ?: 0

            val cafeImageUri = selectedUri?.let { uri ->
                selectImageToInternalStorage(uri, mainActivity)?.toString() ?: ""
            } ?: ""

            val cafeViewModel = CafeViewModel(0, cafeType, cafeName, cafePrice, cafeSize, cafeTakeOut, cafeStock, cafeExplain, cafeImageUri)

            CoroutineScope(Dispatchers.Main).launch {
                val work1 = async(Dispatchers.IO) {
                    CafeRepository.insertCafeInfo(mainActivity, cafeViewModel)
                }
                work1.join()
                mainActivity.removeFragment(FragmentName.INPUT_FRAGMENT)
            }
        }
    }

    // 이미지 선택 후 내부 저장소에 저장하는 메서드
    fun selectImageToInternalStorage(uri: Uri, context: Context): Uri? {
        try {
            val contentResolver: ContentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(uri)
            val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
            val file = File(context.filesDir, "cafe_image_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            return Uri.fromFile(file)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    // 경고 다이얼로그 표시
    fun showAlertDialog(message: String) {
        val builder = AlertDialog.Builder(mainActivity)
        builder.setTitle("입력 오류")
        builder.setMessage(message)
        builder.setPositiveButton("확인") { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }
}