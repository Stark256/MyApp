package com.esketit.myapp.ui.main.settings

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.esketit.myapp.R
import com.esketit.myapp.models.firebase.User
import com.esketit.myapp.models.local.settings_models.SettingsBaseItem
import com.esketit.myapp.models.local.settings_models.SettingsProfileItem
import com.esketit.myapp.models.local.settings_models.SettingsSettingItem
import com.esketit.myapp.ui.base.BaseRecyclerAdapter
import kotlinx.android.synthetic.main.item_settings_profile.view.*
import kotlinx.android.synthetic.main.item_settings_setting.view.*

class SettingsAdapter(var listener: SettingsClickListener): BaseRecyclerAdapter<SettingsBaseItem, RecyclerView.ViewHolder>(){

    private lateinit var context: Context

    override fun getItemViewType(position: Int): Int = getItem(position).getType().value

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): RecyclerView.ViewHolder {
        this.context = parent.context
        return when(type){
            SettingsBaseItem.SettingItemType.TYPE_PROFILE.value -> { ProfileViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_settings_profile, parent, false)) }
            SettingsBaseItem.SettingItemType.TYPE_SETTING.value -> { SettingViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_settings_setting, parent,false)) }
            SettingsBaseItem.SettingItemType.TYPE_EMPTY.value -> { EmptyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_settings_empty, parent,false)) }
            else -> EmptyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_settings_empty, parent,false))

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when(item.getType()){
            SettingsBaseItem.SettingItemType.TYPE_EMPTY -> {}
            SettingsBaseItem.SettingItemType.TYPE_PROFILE -> {
                val profile = item as SettingsProfileItem
                profile.user?.let { (holder as ProfileViewHolder).bindView(it) }
            }
            SettingsBaseItem.SettingItemType.TYPE_SETTING -> {
                val setting = item as SettingsSettingItem
                (holder as SettingViewHolder).bindView(setting, context)
            }
        }

        holder.itemView.setOnClickListener { listener.onItemPressed(item.getType().value) }
    }


    private class ProfileViewHolder(v: View): RecyclerView.ViewHolder(v){
        val image = v.eiv_profile_avatar
        val name = v.tv_profile_name
        val email = v.tv_profile_email

        fun bindView(item: User){
            image.loadImage(item.avatarImgURL)
            name.text = item.name
            email.text = item.email
        }
    }

    private class SettingViewHolder(v: View): RecyclerView.ViewHolder(v){

        val titleV = v.tv_setting_title
        val icon = v.iv_setting_icon

        fun bindView(setting: SettingsSettingItem, context: Context){
            titleV.text = context.getString(setting.titleRes)

            Glide.with(context)
                .load(ContextCompat.getDrawable(context, setting.iconRes))
                .centerCrop()
                .into(icon);
        }
    }

    private class EmptyViewHolder(v: View): RecyclerView.ViewHolder(v){}

    interface SettingsClickListener{
        fun onItemPressed(settingsItemType: Int)
    }
}
