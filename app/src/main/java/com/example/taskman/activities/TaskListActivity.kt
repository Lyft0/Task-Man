package com.example.taskman.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskman.R
import com.example.taskman.adapters.TaskListItemsAdapter
import com.example.taskman.firebase.FirestoreClass
import com.example.taskman.models.Board
import com.example.taskman.models.Card
import com.example.taskman.models.Task
import com.example.taskman.models.User
import com.example.taskman.utils.Constants

class TaskListActivity : BaseActivity() {

    private lateinit var mBoardDetails: Board
    private lateinit var mBoardDocumentId: String
    lateinit var mAssignedMembersDetailList: ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        if (intent.hasExtra(Constants.DOCUMENT_ID)) {
            mBoardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID)!!
        }

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getBoardDetails(this@TaskListActivity, mBoardDocumentId)
    }

    private fun setupActionBar() {
        val toolbarTaskList = findViewById<Toolbar>(R.id.toolbar_task_list_activity)
        setSupportActionBar(toolbarTaskList)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_arrow_back)
            actionBar.title = mBoardDetails.name
        }
        toolbarTaskList.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK
            && (requestCode == MEMBERS_REQUEST_CODE || requestCode == CARD_DETAILS_REQUEST_CODE)
        ) {
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getBoardDetails(this@TaskListActivity, mBoardDocumentId)
        } else {
            Log.e("Cancelled", "Cancelled")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_members, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_members -> {
                val intent = Intent(this@TaskListActivity, MembersActivity::class.java)
                intent.putExtra(Constants.BOARD_DETAIL, mBoardDetails)
                startActivityForResult(intent, MEMBERS_REQUEST_CODE)
                return true
            }
            R.id.deleted_board -> {
                alertDialogForDeleteBoard(mBoardDetails.name)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun deleteBoard(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().deleteBoard(this@TaskListActivity, mBoardDetails.documentId)
        startActivity(Intent(this, MainActivity::class.java))
    }

    fun boardDetails(board: Board) {
        mBoardDetails = board
        hideProgressDialog()
        setupActionBar()
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAssignedMembersListDetails(
            this@TaskListActivity,
            mBoardDetails.assignedTo
        )
    }

    fun addUpdateTaskListSuccess() {
        hideProgressDialog()
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getBoardDetails(this@TaskListActivity, mBoardDetails.documentId)
    }

    fun createTaskList(taskListName: String) {
        Log.e("Task List Name", taskListName)
        val task = Task(taskListName, FirestoreClass().getCurrentUserID())

        mBoardDetails.taskList.add(0, task)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this@TaskListActivity, mBoardDetails)
    }

    fun updateTaskList(position: Int, listName: String, model: Task) {
        val task = Task(listName, model.createdBy)
        mBoardDetails.taskList[position] = task
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this@TaskListActivity, mBoardDetails)
    }

    fun deleteTaskList(position: Int){
        mBoardDetails.taskList.removeAt(position)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this@TaskListActivity, mBoardDetails)
    }

    fun cardDetails(taskListPosition: Int, cardPosition: Int) {
        val intent = Intent(this@TaskListActivity, CardDetailsActivity::class.java)
        intent.putExtra(Constants.BOARD_DETAIL, mBoardDetails)
        intent.putExtra(Constants.TASK_LIST_ITEM_POSITION, taskListPosition)
        intent.putExtra(Constants.CARD_LIST_ITEM_POSITION, cardPosition)
        intent.putExtra(Constants.BOARD_MEMBERS_LIST, mAssignedMembersDetailList)
        startActivityForResult(intent, CARD_DETAILS_REQUEST_CODE)
    }

    fun addCardToTaskList(position: Int, cardName: String) {
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        val cardAssignedUsersList: ArrayList<String> = ArrayList()
        cardAssignedUsersList.add(FirestoreClass().getCurrentUserID())

        val card = Card(cardName, FirestoreClass().getCurrentUserID(), cardAssignedUsersList)

        val cardsList = mBoardDetails.taskList[position].cards
        cardsList.add(card)

        val task = Task(
            mBoardDetails.taskList[position].title,
            mBoardDetails.taskList[position].createdBy,
            cardsList
        )
        mBoardDetails.taskList[position] = task
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this@TaskListActivity, mBoardDetails)
    }


    fun boardMembersDetailList(list: ArrayList<User>) {
        mAssignedMembersDetailList = list
        hideProgressDialog()

        val addTaskList = Task("Add List")
        mBoardDetails.taskList.add(addTaskList)

        val rvTaskList = findViewById<RecyclerView>(R.id.rv_task_list)
        rvTaskList.layoutManager =
            LinearLayoutManager(this@TaskListActivity, LinearLayoutManager.HORIZONTAL, false)
        rvTaskList.setHasFixedSize(true)

        val adapter = TaskListItemsAdapter(this@TaskListActivity, mBoardDetails.taskList)
        rvTaskList.adapter = adapter
    }

    fun boardDeletedSuccessfully() {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun alertDialogForDeleteBoard(cardName: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alert")
        builder.setMessage(
            resources.getString(
                R.string.confirmation_message_to_delete_board,
                cardName
            )
        )
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("Yes") { dialogInterface, which ->
            dialogInterface.dismiss()
            deleteBoard()
        }
        builder.setNegativeButton("No") { dialogInterface, which ->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }


    companion object {
        const val MEMBERS_REQUEST_CODE: Int = 13
        const val CARD_DETAILS_REQUEST_CODE: Int = 14
    }
}