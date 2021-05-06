package com.example.instagram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    var auth: FirebaseAuth? = null //firebaseAuth가져오기
    var googlesignInClient : GoogleSignInClient? = null
    var GOOGLE_LOGIN_CODE = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance() //firebaseAuth에서 auth 가져오기

        // email로그인 버튼 클릭시 signInAndSignUp 메소드 동작
        email_login_button.setOnClickListener {
            signinAndSignUp()
        }

        // google로그인 버튼 클릭시 signInAndSignUp 메소드 동작
        google_signin_button.setOnClickListener {
            googleLogin()
        }

        // 구글 로그인 옵션 설정
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        // 구글 로그인 객체에서 client 추출
        googlesignInClient = GoogleSignIn.getClient(this, gso)
    }
    // 구글 로그인
    fun googleLogin() {
        var signInIntent = googlesignInClient?.signInIntent
        Log.i("googleLogin::::::::::", signInIntent.toString())
        startActivityForResult(signInIntent, GOOGLE_LOGIN_CODE)
    }

    // 오버라이드
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GOOGLE_LOGIN_CODE) {
            var result = Auth.GoogleSignInApi?.getSignInResultFromIntent(data)
            Log.i("onActivity의 result는??", result?.isSuccess.toString())
            if(result?.isSuccess==true) {
                var account = result.signInAccount
                if (account != null) {
                    firebaseAuthWithGoogle(account)
                }
            }
        }
    }

    // 구글 로그인
    fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
    var credential = GoogleAuthProvider.getCredential(account.idToken,null)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    // 성공시 메인페이지로 이동
                    moveMainPage(task.result?.user)
                } else if(task.exception?.message.isNullOrEmpty()){
                    // 실패시 토스트 알림 보여주기
                    Toast.makeText(this, task.exception?.message,Toast.LENGTH_LONG).show()
                }else{
                    // 로그인 하는 부분으로 이동
                    signinEmail()
                }
            }
    }

    // 이메일 회원가입 및 로그인
    fun signinAndSignUp() {
        auth?.createUserWithEmailAndPassword(email_edittext.text.toString(),password_edittext.text.toString())
            ?.addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    // 성공시 메인페이지로 이동
                    moveMainPage(task.result?.user)
                } else if(task.exception?.message.isNullOrEmpty()){
                    // 실패시 토스트 알림 보여주기
                    Toast.makeText(this, task.exception?.message,Toast.LENGTH_LONG).show()
                }else{
                    // 로그인 하는 부분으로 이동
                    signinEmail()
                }
            }
    }

    // 이메일 로그인
    fun signinEmail() {
        auth?.signInWithEmailAndPassword(email_edittext.text.toString(),password_edittext.text.toString())
            ?.addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    // 성공시
                    moveMainPage(task.result?.user)
                } else{
                    // 에러 발생
                    Toast.makeText(this, task.exception?.message,Toast.LENGTH_LONG).show()
                }
            }
    }

    // 로그인 성공시 메인 화면으로 이동하는 function
    fun moveMainPage(user:FirebaseUser?) {
        if(user!= null) {
            startActivity(Intent(this,MainActivity::class.java))
        }
    }
}