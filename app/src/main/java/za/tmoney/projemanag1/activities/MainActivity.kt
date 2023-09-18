package za.tmoney.projemanag1.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import za.tmoney.projemanag1.R
import za.tmoney.projemanag1.adapters.BoardItemsAdapter
import za.tmoney.projemanag1.databinding.ActivityMainBinding
import za.tmoney.projemanag1.databinding.ContentMainBinding
import za.tmoney.projemanag1.databinding.NavHeaderMainBinding
import za.tmoney.projemanag1.firebase.FirestoreClass
import za.tmoney.projemanag1.models.Board
import za.tmoney.projemanag1.models.User
import za.tmoney.projemanag1.utils.Constants

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var binding : ActivityMainBinding? = null


    private lateinit var mUserName : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupActionBar()
        binding?.navView?.setNavigationItemSelectedListener(this)

        FirestoreClass().loadUserData(this, true)

        binding?.appBarMainTb?.fabCreateBoard?.setOnClickListener {
            val intent = Intent(this, CreateBoardActivity::class.java)
            intent.putExtra(Constants.NAME, mUserName)
            start2ndActivityForResult.launch(intent)
        }
    }

    private fun setupActionBar(){
        setSupportActionBar(binding?.appBarMainTb?.toolbarMainActivity)
        binding?.appBarMainTb?.toolbarMainActivity?.setNavigationIcon(R.drawable.ic_action_navigation_menu)
        binding?.appBarMainTb?.toolbarMainActivity?.setNavigationOnClickListener {
            toggleDrawer()
        }

    }

    private fun toggleDrawer(){
        if (binding?.drawerLayout?.isDrawerOpen(GravityCompat.START) == true){
            binding?.drawerLayout?.closeDrawer(GravityCompat.START)
        }else
            binding?.drawerLayout?.openDrawer(GravityCompat.START)
    }

    override fun onBackPressed() {
        if (binding?.drawerLayout?.isDrawerOpen(GravityCompat.START) == true){
            binding?.drawerLayout?.closeDrawer(GravityCompat.START)
        }else{
            doubleBackToExit()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_my_profile -> {
                startUpdateActivityAndGetResult.launch(Intent(this, MyProfileActivity::class.java))
            }
            R.id.nav_sign_out -> {
                FirebaseAuth.getInstance().signOut()

                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        binding?.drawerLayout?.closeDrawer(GravityCompat.START)

        return true
    }

    fun updateNavigationUserDetails(user: User, readBoardList: Boolean){

        mUserName = user.name

        val viewHeader = binding?.navView?.getHeaderView(0)
        val headerBinding = NavHeaderMainBinding.bind(viewHeader!!)

        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_placeholder)
            .into(headerBinding.navUserImage)

        headerBinding.tvUsername.text = user.name

        if (readBoardList){
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getBoardList(this)
        }
    }

    private var startUpdateActivityAndGetResult = registerForActivityResult(ActivityResultContracts
        .StartActivityForResult()){ result ->
        if (result.resultCode == Activity.RESULT_OK){
            FirestoreClass().loadUserData(this)
        }else{
            Log.e("Cancelled", "Cancelled")
        }

    }

    private var start2ndActivityForResult = registerForActivityResult(ActivityResultContracts
        .StartActivityForResult()){result ->
        if (result.resultCode == Activity.RESULT_OK){
            FirestoreClass().getBoardList(this)
        }
    }

    fun populateBoardListToUI(boardList : ArrayList<Board>){

        hideProgressDialog()

        if (boardList.size > 0){
            binding?.appBarMainTb?.contentMain?.rvBoardsList?.visibility = View.VISIBLE
            binding?.appBarMainTb?.contentMain?.tvNoBoardsAvailable?.visibility = View.GONE

            binding?.appBarMainTb?.contentMain?.rvBoardsList?.layoutManager = LinearLayoutManager(this)
            binding?.appBarMainTb?.contentMain?.rvBoardsList?.setHasFixedSize(true)

            val adapter = BoardItemsAdapter(this, boardList)
            binding?.appBarMainTb?.contentMain?.rvBoardsList?.adapter = adapter

            adapter.setOnClickListener(object: BoardItemsAdapter.OnClickListener{
                override fun onClick(position: Int, model: Board) {
                    val intent = Intent(this@MainActivity, TaskListActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID, model.documentID)
                    startActivity(intent)
                }
            })

        }else{
            binding?.appBarMainTb?.contentMain?.rvBoardsList?.visibility = View.GONE
            binding?.appBarMainTb?.contentMain?.tvNoBoardsAvailable?.visibility = View.VISIBLE
        }
    }
}