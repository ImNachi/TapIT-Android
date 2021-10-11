package com.example.tapit_android.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tapit_android.R
import com.example.tapit_android.models.UserContact
import com.example.tapit_android.models.Users
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
import kotlin.collections.ArrayList

class ContactListAdapter(mContext:Context, contactList:List<Users>):
        RecyclerView.Adapter<ContactListAdapter.MyViewHolder>() {

    var contactList:List<Users> = ArrayList()
    var mContext:Context

    val TAG = "ContactListAdapter"

    init {
        this.contactList=contactList
        this.mContext = mContext
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var profileDp:CircleImageView
        var displayName:TextView
        var professionName:TextView
        var companyName:TextView

        init {
            profileDp = itemView.findViewById(R.id.user_dp)
            displayName = itemView.findViewById(R.id.display_name)
            professionName = itemView.findViewById(R.id.profession_name)
            companyName = itemView.findViewById(R.id.company_name)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contacts_rv_item,parent,false)

        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var theItem:Users = contactList.get(position)

        if(contactList.get(position).dpUrl!=null){
            Glide.with(mContext).load(theItem.dpUrl).into(holder.profileDp)
        }
        Log.d(TAG,theItem.displayName.toString())
        holder.displayName.text = theItem.displayName.toString()
        holder.professionName.text = theItem.profession.toString()
        holder.companyName.text = theItem.companyName.toString()
    }

    override fun getItemCount(): Int {
        return contactList.size;
    }


}