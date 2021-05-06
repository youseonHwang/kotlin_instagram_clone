package com.example.instagram.navigation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.instagram.R
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add_photo.*
import java.util.Date
import java.text.SimpleDateFormat

class AddPhotoActivity : AppCompatActivity() {
    var PICK_IMAGE_FROM_ALBUM = 0
    var storage : FirebaseStorage? = null //firebaseStorage 가져옴
    var photoUri : Uri? = null // uri를 담을 수 있는

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

        // storage 초기화
        storage = FirebaseStorage.getInstance()

        // 앨범 오픈
        var photoPickerIntent= Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent,PICK_IMAGE_FROM_ALBUM) // request코드를 넣어줌

        // 버튼에다가 이벤트
        addphoto_btn_upload.setOnClickListener{
            contentUpload()
        }
    }

    // onActivityResult를 만들어서 선택한 이미지를 받는 부분
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_FROM_ALBUM){ // 결과 코드가 0일때
            if(resultCode == Activity.RESULT_OK){ // 결과 ok가 왔을때
                // 사진을 선택했을때 이미지의 경로가 여기로 넘어옴
                photoUri = data?.data
                addphoto_image.setImageURI(photoUri) // 선택한 경로 이미지 바로 보여주기
            } else{ // 취소버튼을 클릭했을때 동작하는 부분 (without selecting it)
                finish()
            }
        }
    }

    fun contentUpload() {
        // 파일 이름 만들기(중복생성 되지 않도록)
        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageFileName = "IMAGE_"+ timestamp + "_.png"

        // 이미지 업로드
        var storageRef = storage?.reference?.child("images")?.child(imageFileName)
        storageRef?.putFile(photoUri!!)?.addOnSuccessListener {
            Toast.makeText(this,getString(R.string.upload_success),Toast.LENGTH_SHORT).show()
        }

    }
}