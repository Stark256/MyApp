package com.esketit.myapp.util

import android.content.Context
import android.os.PatternMatcher
import android.util.Patterns
import com.esketit.myapp.R
import java.util.regex.Pattern

object FieldsValidatorUtil{


    fun isEmpty(value: String, context: Context): String? {
        return if(value.isEmpty() || value.isBlank()){
            context.getString(R.string.empty_field_message)
        }else null
    }

    fun isNameValid(value: String?, context: Context): String? {
        return if(value != null){
            return isEmpty(value, context)
        }else{
            context.getString(R.string.empty_field_message)
        }
    }

    fun isEmailValid(value: String?, context: Context): String? {
        return if(value != null){
            return isEmpty(value, context)
        }else{
            context.getString(R.string.empty_field_message)
        }
    }

    fun isPassValid(value: String?, context: Context): String? {
        return if(value != null){
            return isEmpty(value, context)
        }else{
            context.getString(R.string.empty_field_message)
        }
    }


//
//    fun isUserNameValid(title: String?, context: Context): String?{
//        return if(title != null){
//            return isEmpty(title, context) ?:
//            if(title.length > Limits.USER_NAME_LENGTH){
//                context.getString(R.string.empty_field_error)
//            }else{
//                null
//            }
//        }else context.getString(R.string.empty_field_error)
//    }

    //Patterns.EMAIL_ADDRESS.matcher("").matches()
}
