package za.tmoney.projemanag1.activities

import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import za.tmoney.projemanag1.R
import za.tmoney.projemanag1.databinding.ActivityCreateBoardBinding
import za.tmoney.projemanag1.firebase.FirestoreClass
import za.tmoney.projemanag1.models.Board
import za.tmoney.projemanag1.utils.Constants

class CreateBoardActivity : BaseActivity() {

    private lateinit var binding : ActivityCreateBoardBinding

    private var mSelectedBoardImageFileUri : Uri? = null

    private lateinit var mUserName : String

    private var mBoardImageURL : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        if (intent.hasExtra(Constants.NAME)){
            mUserName = intent.getStringExtra(Constants.NAME).toString()
        }

        binding.ivBoardImage.setOnClickListener {
            chooseGalleryPhotos()
        }

        registerGalleryOnActivityForResult(func = {uri -> updateBoardImageURI(uri!!)})

        binding.btnCreate.setOnClickListener {
            if (mSelectedBoardImageFileUri != null){
                uploadBoardImage()
            }else{
                showProgressDialog(resources.getString(R.string.please_wait))
                createBoard()
            }
        }
    }


    private fun setupActionBar(){
        setSupportActionBar(binding.toolbarCreateBoardActivity)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarCreateBoardActivity.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    fun createBoard(){
        val assignedUsersArrayList : ArrayList<String> = ArrayList()
        assignedUsersArrayList.add(getCurrentUserID())

        val board = Board(
            binding.etBoardName.text.toString(),
            mBoardImageURL,
            mUserName,
            assignedUsersArrayList
        )

        FirestoreClass().createBoard(this, board)
    }

    private fun uploadBoardImage(){
        showProgressDialog(resources.getString(R.string.please_wait))

        val sRef : StorageReference = FirebaseStorage.getInstance()
            .reference.child("BOARD_IMAGE" + System.currentTimeMillis() + "." +
                    getFileExtension(mSelectedBoardImageFileUri))

        sRef.putFile(mSelectedBoardImageFileUri!!).addOnSuccessListener{
                taskSnapShot ->
            Log.i("Firebase image URL",
                taskSnapShot.metadata!!.reference!!.downloadUrl.toString())

            taskSnapShot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri ->
                Log.i("Downloadable image URI", uri.toString())
                mBoardImageURL = uri.toString()

                hideProgressDialog()

                createBoard()
            }
        }.addOnFailureListener{
                exception ->
            Toast.makeText(this@CreateBoardActivity,
                exception.message,
                Toast.LENGTH_SHORT).show()

            hideProgressDialog()
        }
    }

    fun boardCreatedSuccessfully(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun updateBoardImageURI(imageUri : Uri){

        mSelectedBoardImageFileUri = imageUri

        Glide
            .with(this)
            .load(mSelectedBoardImageFileUri)
            .centerCrop()
            .placeholder(R.drawable.ic_board_place_holder)
            .into(binding.ivBoardImage)
    }


}