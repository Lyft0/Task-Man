package com.example.taskman.activities

import android.app.Activity
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskman.R
import com.example.taskman.adapters.MemberListItemsAdapter
import com.example.taskman.firebase.FirestoreClass
import com.example.taskman.models.Board
import com.example.taskman.models.User
import com.example.taskman.utils.Constants

class MembersActivity : BaseActivity() {

    private lateinit var mBoardDetails: Board
    private lateinit var mAssignedMembersList:ArrayList<User>
    private var anyChangesDone: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_members)

        if (intent.hasExtra(Constants.BOARD_DETAIL)) {
            mBoardDetails = intent.getParcelableExtra<Board>(Constants.BOARD_DETAIL)!!
        }

        setupActionBar()

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAssignedMembersListDetails(
            this@MembersActivity,
            mBoardDetails.assignedTo
        )
    }

    private fun setupActionBar() {
        val toolbarMember = findViewById<Toolbar>(R.id.toolbar_members_activity)
        setSupportActionBar(toolbarMember)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_arrow_back)
            actionBar.title = "Members"
        }
        toolbarMember.setNavigationOnClickListener { onBackPressed() }
    }

    fun memberDetails(user: User) {
        mBoardDetails.assignedTo.add(user.id)
        FirestoreClass().assignMemberToBoard(this@MembersActivity, mBoardDetails, user)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_member, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add_member -> {
                 dialogSearchMember()
                 return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun dialogSearchMember() {
        val dialog = Dialog(this)

        dialog.setContentView(R.layout.dialog_search_member)
        dialog.findViewById<TextView>(R.id.tv_add).setOnClickListener(View.OnClickListener {
            val email = dialog.findViewById<EditText>(R.id.et_email_search_member).text.toString()
            if (email.isNotEmpty()) {
                dialog.dismiss()
                showProgressDialog(resources.getString(R.string.please_wait))
                FirestoreClass().getMemberDetails(this@MembersActivity, email)
            } else {
                Toast.makeText(
                    this@MembersActivity,
                    "Please enter members email address.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        dialog.findViewById<TextView>(R.id.tv_cancel).setOnClickListener(View.OnClickListener {
            dialog.dismiss()
        })
        dialog.show()
    }

    fun setupMembersList(list: ArrayList<User>) {
        mAssignedMembersList = list
        hideProgressDialog()
        val rvMemberList = findViewById<RecyclerView>(R.id.rv_members_list)
        rvMemberList.layoutManager = LinearLayoutManager(this@MembersActivity)
        rvMemberList.setHasFixedSize(true)
        val adapter = MemberListItemsAdapter(this@MembersActivity, list)
        rvMemberList.adapter = adapter
    }

    fun removeMember(position: Int, id: String){
        Log.v("test","${mBoardDetails}")
//        mBoardDetails.taskList.removeAt(position)
//        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)
//        showProgressDialog(resources.getString(R.string.please_wait))
//        FirestoreClass().removeMemberFromBoard(this@MembersActivity, mBoardDetails)
    }


    override fun onBackPressed() {
        if (anyChangesDone) {
            setResult(Activity.RESULT_OK)
        }
        super.onBackPressed()
    }

    fun memberAssignSuccess(user: User) {
        hideProgressDialog()
        mAssignedMembersList.add(user)
        anyChangesDone = true
        setupMembersList(mAssignedMembersList)
    }
}