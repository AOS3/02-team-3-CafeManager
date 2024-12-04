package com.lion.cafemanager_1.fragment

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.ContentResolver
import android.content.Context
import android.content.DialogInterface
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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lion.cafemanager_1.MainActivity
import com.lion.cafemanager_1.R
import com.lion.cafemanager_1.databinding.FragmentModifyBinding
import com.lion.cafemanager_1.fragment.InputFragment.Companion.REQUEST_CAMERA_PERMISSION
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
import kotlinx.coroutines.selects.select
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ModifyFragment : Fragment() {

    lateinit var fragmentModifyBinding: FragmentModifyBinding
    lateinit var mainActivity: MainActivity
    lateinit var albumLauncher: ActivityResultLauncher<Intent>
    lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    var selectedUri: Uri? = null
    var filePath = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentModifyBinding = FragmentModifyBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        // Toolbar 설정 메서드 호출
        settingToolbar()

        // 입력 요소들 초기 설정
        settingInput()

        // 이미지 저장
        settingImageModify()

        // 앨범 실행 기능 설정
        createAlbumLauncher()

        // 카메라 실행 기능 설정
        createCameraLauncher()

        return fragmentModifyBinding.root
    }

    // Toolbar 설정 메서드
    fun settingToolbar(){
        fragmentModifyBinding.apply {
            // 타이틀
            toolbarModify.title = "카페 메뉴 정보 수정"
            // 네비게이션
            toolbarModify.setNavigationIcon(R.drawable.arrow_back_24px)
            toolbarModify.setNavigationOnClickListener {
                mainActivity.removeFragment(FragmentName.MODIFY_FRAGMENT)
            }
            // 메뉴
            toolbarModify.inflateMenu(R.menu.toolbar_modify_menu)
            toolbarModify.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.toolbar_modify_menu_done ->{
                        // mainActivity.removeFragment(FragmentName.MODIFY_FRAGMENT)
                        modifyDone()
                    }
                }
                true
            }
        }
    }

    // 입력 요소들 설정
    fun settingInput() {
        fragmentModifyBinding.apply {
            // 카페 메뉴 번호 가져오기
            val cafeIdx = arguments?.getInt("cafeIdx")!!

            // 카페 메뉴 데이터를 가져오기
            CoroutineScope(Dispatchers.Main).launch {
                val work1 = async(Dispatchers.IO){
                    CafeRepository.selectCafeInfoByCafeIdx(mainActivity, cafeIdx)
                }
                val cafeViewModel = work1.await()

                // 카페 타입 설정
                when(cafeViewModel.cafeType){
                    CafeType.CAFE_TYPE_COFFEE -> {
                        toggleModifyCafeType.check(R.id.buttonModifyCoffee)
                    }
                    CafeType.CAFE_TYPE_NONCOFFEE -> {
                        toggleModifyCafeType.check(R.id.buttonModifyNonCoffee)
                    }
                    CafeType.CAFE_TYPE_BREAD -> {
                        toggleModifyCafeType.check(R.id.buttonModifyBread)
                    }
                    CafeType.CAFE_TYPE_CAKE -> {
                        toggleModifyCafeType.check(R.id.buttonModifyCake)
                    }
                }


                // 카페 메뉴 이름 설정
                textInputLayoutModifyCafeMenuName.editText?.setText(cafeViewModel.cafeName)
                // 카페 메뉴 가격 설정
                textInputLayoutModifyCafeMenuPrice.editText?.setText(cafeViewModel.cafePrice.toString())

                // 카페 메뉴 사이즈 설정
                when(cafeViewModel.cafeSize){
                    CafeSize.CAFE_SIZE_LARGE -> {
                        toggleModifyCafeSize.check(R.id.buttonModifySizeLarge)
                    }
                    CafeSize.CAFE_SIZE_MEDIUM -> {
                        toggleModifyCafeSize.check(R.id.buttonModifySizeMedium)
                    }
                    CafeSize.CAFE_SIZE_SMALL -> {
                        toggleModifyCafeSize.check(R.id.buttonModifySizeSmall)
                    }
                    CafeSize.CAFE_SIZE_NO -> {
                        toggleModifyCafeSize.check(R.id.buttonModifySizeNoChoice)
                    }
                }

                // 카페 메뉴 포장 가능 유무 설정
                when(cafeViewModel.cafeTakeOut) {
                    CafeTakeOut.CAFE_TAKEOUT_OK -> {
                        radioButtonModifyTakeOut.check(R.id.radioButtonTakeOutOk)
                    }
                    CafeTakeOut.CAFE_TAKEOUT_NO -> {
                        radioButtonModifyTakeOut.check(R.id.radioButtonTakeOutNo)
                    }
                }

                // 카페 메뉴 재고 설정
                textInputLayoutModifyCafeMenuStock.editText?.setText(cafeViewModel.cafeStock.toString())
                // 카페 메뉴 설명 설정
                textFieldModifyMenuExplain.editableText.append(cafeViewModel.cafeExplain)
                selectedUri = cafeViewModel.cafeImage.toUri()

                if(selectedUri.toString() == ""){
                    imageViewModify.setImageResource(R.drawable.image_search_24px)
                } else {
                    imageViewModify.setImageURI(selectedUri)
                }
            }
        }
    }

    // 이미지 선택 버튼 설정
    fun settingImageModify() {
        fragmentModifyBinding.buttonModifyCafeMenuPicture.setOnClickListener {
            val albumIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*"))
            }
            albumLauncher.launch(albumIntent)
        }

        fragmentModifyBinding.buttonModifyCafeMenuTakePicture.setOnClickListener {
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
        return File.createTempFile(
            "temp_image_",
            ".jpg",
            storageDir
        ).apply {
            filePath = absolutePath
        }
    }

    // 카메라 실행 함수
    private fun openCamera() {
        val photoFile: File? = try {
            createImageFile() // 이미지 파일 생성
        } catch (ex: IOException) {
            Log.e("Camera", "Error creating file", ex)
            null
        }

        photoFile?.also {
            val photoURI: Uri = FileProvider.getUriForFile(
                mainActivity,
                "com.lion.cafemanager_1.fileprovider",
                it
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
                        fragmentModifyBinding.imageViewModify.setImageBitmap(bitmap)
                    } else {
                        val cursor = mainActivity.contentResolver.query(uri, null, null, null, null)
                        cursor?.use {
                            if (it.moveToFirst()) {
                                val idx = it.getColumnIndex(MediaStore.Images.Media.DATA)
                                val path = it.getString(idx)
                                selectedUri = Uri.parse(path)
                                val bitmap = BitmapFactory.decodeFile(path)
                                fragmentModifyBinding.imageViewModify.setImageBitmap(bitmap)
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
                fragmentModifyBinding.imageViewModify.setImageURI(takenImageUri)
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

    // 수정 처리 메서드
    fun modifyDone() {
        fragmentModifyBinding.apply {
            // 카페 메뉴 이름, 가격, 설명 등의 필수 입력 필드가 비어 있는지 확인
            val cafeName = textInputLayoutModifyCafeMenuName.editText?.text.toString()
            val cafePriceString = textInputLayoutModifyCafeMenuPrice.editText?.text.toString()
            val cafeStockString = textInputLayoutModifyCafeMenuStock.editText?.text.toString()
            val cafeExplain = textFieldModifyMenuExplain.editableText.toString()

            // 필수 입력 항목 검사
            if (cafeName.isEmpty() || cafePriceString.isEmpty() || cafeStockString.isEmpty() || cafeExplain.isEmpty()) {
                // 필수 항목이 비어 있으면 경고 다이얼로그 띄움
                showAlertDialog("모든 항목을 입력해야 수정할 수 있습니다.")
                return  // 데이터 저장을 하지 않고 메서드를 종료
            }

            // 가격을 정수로 변환
            val cafePrice = cafePriceString.toIntOrNull() ?: 0

            // 카페 재고 처리 (숫자형으로 변환)
            val cafeStock = cafeStockString.toIntOrNull() ?: 0

            // 카페 타입, 크기, 테이크 아웃 여부 설정
            val cafeType = when (toggleModifyCafeType.checkedButtonId) {
                R.id.buttonModifyCoffee -> CafeType.CAFE_TYPE_COFFEE
                R.id.buttonModifyNonCoffee -> CafeType.CAFE_TYPE_NONCOFFEE
                R.id.buttonModifyBread -> CafeType.CAFE_TYPE_BREAD
                R.id.buttonModifyCake -> CafeType.CAFE_TYPE_CAKE
                else -> CafeType.CAFE_TYPE_COFFEE
            }

            val cafeSize = when (toggleModifyCafeSize.checkedButtonId) {
                R.id.buttonModifySizeLarge -> CafeSize.CAFE_SIZE_LARGE
                R.id.buttonModifySizeMedium -> CafeSize.CAFE_SIZE_MEDIUM
                R.id.buttonModifySizeSmall -> CafeSize.CAFE_SIZE_SMALL
                R.id.buttonModifySizeNoChoice -> CafeSize.CAFE_SIZE_NO
                else -> CafeSize.CAFE_SIZE_NO
            }

            val cafeTakeOut = when (radioButtonModifyTakeOut.checkedRadioButtonId) {
                R.id.radioButtonTakeOutOk -> CafeTakeOut.CAFE_TAKEOUT_OK
                else -> CafeTakeOut.CAFE_TAKEOUT_NO
            }

            // 이미지를 내부 저장소로 저장
            val cafeImageUri = selectedUri?.let { uri ->
                selectImageToInternalStorage(uri, mainActivity)?.toString() ?: ""
            } ?: ""

            // 카페 정보를 ViewModel에 저장
            val cafeViewModel = CafeViewModel(
                cafeIdx = arguments?.getInt("cafeIdx")!!,
                cafeType = cafeType,
                cafeName = cafeName,
                cafePrice = cafePrice,
                cafeSize = cafeSize,
                cafeTakeOut = cafeTakeOut,
                cafeStock = cafeStock,
                cafeExplain = cafeExplain,
                cafeImage = cafeImageUri
            )

            // 수정 확인 다이얼로그 표시
            val materialAlertDialogBuilder = MaterialAlertDialogBuilder(mainActivity)
            materialAlertDialogBuilder.setTitle("수정")
            materialAlertDialogBuilder.setMessage("이전 데이터로 복원할 수 없습니다.")
            materialAlertDialogBuilder.setNeutralButton("취소", null)
            materialAlertDialogBuilder.setPositiveButton("수정") { dialogInterface: DialogInterface, i: Int ->
                // 비동기로 데이터 수정
                CoroutineScope(Dispatchers.Main).launch {
                    val work1 = async(Dispatchers.IO) {
                        // 카페 정보 업데이트
                        CafeRepository.updateCafeInfo(mainActivity, cafeViewModel)
                    }
                    work1.join()
                    mainActivity.removeFragment(FragmentName.MODIFY_FRAGMENT)
                }
            }
            materialAlertDialogBuilder.show()
        }
    }

    // 경고 다이얼로그 표시
    fun showAlertDialog(message: String) {
        val builder = AlertDialog.Builder(mainActivity)
        builder.setTitle("입력 오류")
        builder.setMessage(message)
        builder.setPositiveButton("확인") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

}