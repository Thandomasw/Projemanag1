package za.tmoney.projemanag1.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import za.tmoney.projemanag1.R
import za.tmoney.projemanag1.databinding.ActivitySignInBinding
import za.tmoney.projemanag1.firebase.FirestoreClass
import za.tmoney.projemanag1.models.User

class SignInActivity : BaseActivity() {

    private var binding : ActivitySignInBinding? = null

    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setupActionBar()

        auth = FirebaseAuth.getInstance()

        binding?.btnSignIn?.setOnClickListener {
            signInRegisteredUser()
        }
    }

    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarSignInActivity)

        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }
        binding?.toolbarSignInActivity?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    fun signInSuccess(user: User){
        hideProgressDialog()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun signInRegisteredUser(){
        val email : String = binding?.etEmail?.text.toString().trim { it <= ' ' }
        val password : String = binding?.etPassword?.text.toString().trim { it <= ' ' }

        if (validateForm(email, password)){
            showProgressDialog(resources.getString(R.string.please_wait))
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this){
                task ->
                hideProgressDialog()
                if (task.isSuccessful){

                    FirestoreClass().loadUserData(this)
                }else{
                    Log.w("Sign In", "signInWithEmail:failure", task.exception)
                    Toast.makeText(this, "Authentication failure",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun validateForm(email : String, password : String): Boolean{
        return when{
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Please enter an email address")
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Please enter a password")
                false
            }else -> {
                true
            }
        }
    }
}