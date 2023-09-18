package za.tmoney.projemanag1.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import za.tmoney.projemanag1.R
import za.tmoney.projemanag1.databinding.ActivityMyProfileBinding
import za.tmoney.projemanag1.firebase.FirestoreClass
import za.tmoney.projemanag1.models.User
import za.tmoney.projemanag1.utils.Constants

class MyProfileActivity : BaseActivity() {


    private lateinit var binding: ActivityMyProfileBinding
    private var mSelectedImageFileUri : Uri? = null
    private var mProfileImageUri : String = ""
    private lateinit var mUserDetails : User


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        FirestoreClass().loadUserData(this)

        binding.ivProfileUserImage.setOnClickListener {
            chooseGalleryPhotos()
        }

        binding.btnUpdate.setOnClickListener {
            if (mSelectedImageFileUri != null){
                uploadUserImage()
            }else{
                showProgressDialog(resources.getString(R.string.please_wait))
                updateUserProfileData()
            }
        }

        registerGalleryOnActivityForResult(func = {uri -> updateProfileImage(uri) })
    }

    private fun setupActionBar(){
        setSupportActionBar(binding.toolbarMyProfileActivity)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.my_profile_title)
        }
        binding.toolbarMyProfileActivity.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    fun setUserDataInUI(user: User){

        mUserDetails = user

        updateProfileImage(user.image)

        binding.etName.setText(user.name)
        binding.etEmail.setText(user.email)
        if (user.mobile != 0L){
            binding.etMobile.setText(user.mobile.toString())
        }
    }


    private fun uploadUserImage(){
        showProgressDialog(resources.getString(R.string.please_wait))

        if (mSelectedImageFileUri != null){
            val sRef : StorageReference = FirebaseStorage.getInstance()
                .reference.child("USER_IMAGE" + System.currentTimeMillis() + "." +
                        getFileExtension(mSelectedImageFileUri))

            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener{
                taskSnapShot ->
                Log.i("Firebase image URL",
                    taskSnapShot.metadata!!.reference!!.downloadUrl.toString())

                taskSnapShot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri ->
                    Log.i("Downloadable image URI", uri.toString())
                    mProfileImageUri = uri.toString()

                    hideProgressDialog()

                    updateUserProfileData()
                }
            }.addOnFailureListener{
                exception ->
                Toast.makeText(this,
                    exception.message,
                    Toast.LENGTH_SHORT).show()

                hideProgressDialog()
            }
        }
    }



    fun profileUpdateSuccess(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun updateUserProfileData(){
        val userHashMap = HashMap<String, Any>()

        if (mProfileImageUri.isNotEmpty() && mProfileImageUri != mUserDetails.image){
            userHashMap[Constants.IMAGE] = mProfileImageUri

        }
        if (binding.etName.text.toString() != mUserDetails.name){
            userHashMap[Constants.NAME] = binding.etName.text.toString()

        }
        if (binding.etMobile.text.toString() != mUserDetails.mobile.toString()){
            if (binding.etMobile.text.toString() == ""){
                userHashMap[Constants.MOBILE] = 0L
            }else{
                userHashMap[Constants.MOBILE] = binding.etMobile.text.toString().toLong()
            }
        }
        FirestoreClass().updateUserProfileDetails(this, userHashMap)

    }

    private fun updateProfileImage(image: String){
        Glide
            .with(this@MyProfileActivity)
            .load(image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_placeholder)
            .into(binding.ivProfileUserImage)
    }

    private fun updateProfileImage(imageUri : Uri?){

        mSelectedImageFileUri = imageUri

        Glide
            .with(this@MyProfileActivity)
            .load(mSelectedImageFileUri)
            .centerCrop()
            .placeholder(R.drawable.ic_user_placeholder)
            .into(binding.ivProfileUserImage)
    }

}