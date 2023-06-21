package com.example.taskman.adapters

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.taskman.R
import com.example.taskman.activities.MembersActivity
import com.example.taskman.activities.TaskListActivity
import com.example.taskman.models.User
import com.example.taskman.utils.Constants
import de.hdodenhof.circleimageview.CircleImageView

open class MemberListItemsAdapter(
    private val context: Context,
    private var list: ArrayList<User>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_member,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {

            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(holder.itemView.findViewById<CircleImageView>(R.id.iv_member_image))

            holder.itemView.findViewById<TextView>(R.id.tv_member_name).text = model.name
            holder.itemView.findViewById<TextView>(R.id.tv_member_email).text = model.email
            holder.itemView.findViewById<ImageButton>(R.id.iv_remove_member).visibility = View.VISIBLE

            if (model.selected) {
                holder.itemView.findViewById<ImageView>(R.id.iv_selected_member).visibility = View.VISIBLE
                holder.itemView.findViewById<ImageButton>(R.id.iv_remove_member).visibility = View.GONE
            } else {
                holder.itemView.findViewById<ImageView>(R.id.iv_selected_member).visibility = View.GONE
                holder.itemView.findViewById<ImageButton>(R.id.iv_remove_member).visibility = View.VISIBLE
            }

            holder.itemView.setOnClickListener {
                if (onClickListener != null) {
                    if (model.selected) {
                        onClickListener!!.onClick(position, model, Constants.UN_SELECT)
                    } else {
                        onClickListener!!.onClick(position, model, Constants.SELECT)
                    }
                }
            }

            holder.itemView.findViewById<ImageButton>(R.id.iv_remove_member).setOnClickListener {
                Log.v("fuck","$model")
                alertDialogForDeleteList(position, model.name, model.id)
            }
        }
    }

    private fun alertDialogForDeleteList(position: Int, title: String, id: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Alert")
        builder.setMessage("Are you sure you want to remove $title.")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("Yes") { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed
            if (context is MembersActivity) {
                context.removeMember(position,id)
            }
        }
        builder.setNegativeButton("No") { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }


    override fun getItemCount(): Int {
        return list.size
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int, user: User, action: String)
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}