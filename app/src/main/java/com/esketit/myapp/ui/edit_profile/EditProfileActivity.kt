package com.esketit.myapp.ui.edit_profile

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import com.esketit.myapp.R
import com.esketit.myapp.managers.Injector
import com.esketit.myapp.managers.LocationHelper
import com.esketit.myapp.models.firebase.User
import com.esketit.myapp.ui.base.BaseActivity
import com.esketit.myapp.ui.welcome.WelcomeActivity
import com.esketit.myapp.util.FieldsValidatorUtil
import com.esketit.myapp.view.edit_image_view.EditImageDialogBaseClickListener
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.layout_toolbar.view.*


class  EditProfileActivity : BaseActivity() {

    private val RESULT_CAMERA = 15
    private val RESULT_GALARY = 25

    private val locationManager = LocationHelper(this)
    private lateinit var viewModel: EditProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        setSupportActionBar(this.toolbar_view_edit_profile.toolbar, true, true)
        setToolbarTitle(getString(R.string.edit_profile))

        initView()

        initViewModel()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            RESULT_GALARY -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.data?.let {
                        val contentURI = it
                        startCropperActivity(contentURI)
                    }
                }
            }

            RESULT_CAMERA -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.extras?.let {
                        val bitmap = it.get("data") as Bitmap
                        val uri = viewModel.getBitmapUri(bitmap, contentResolver)
                        startCropperActivity(uri)
                    }
                }
            }

            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                data?.let {
                    val result: CropImage.ActivityResult = CropImage.getActivityResult(data);
                    if (resultCode == Activity.RESULT_OK) {
                        val resultUri = result.getUri();
                        eiv_edit_profile_avatar.loadImage(resultUri)

                    } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                        //Timber.i("Crop image error = ${result.error}")
                    }else{}
                }
            }

            199 -> {
                locationPressed()
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit_profile, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> { this@EditProfileActivity.onBackPressed() }
            R.id.mi_save -> { savePressed() }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initViewModel(){
        this.viewModel = ViewModelProviders.of(this).get(EditProfileViewModel::class.java)
        this.viewModel.apply {
            user.observe(this@EditProfileActivity, Observer<User>{ user ->
                user?.let { updateUserUI(user) }
            })

            updateUI()
        }
    }

    private fun initView(){
        // Username
        etv_name?.initBuilder(hintRes = R.string.username,
            colorRes = R.color.gray,
            inputType = InputType.TYPE_CLASS_TEXT)
        etv_name?.onFocusChanged { hasFocus -> if (!hasFocus) checkUsername() }
        etv_name?.onActionPressed { checkUsername() }

        // Location
        etv_location?.initBuilder(hintRes = R.string.location,
            colorRes = R.color.gray,
            inputType = InputType.TYPE_CLASS_TEXT,
            focusable = false)
        etv_location?.editTextPressed { locationPressed() }

        // Bio
        etv_bio?.initBuilder(hintRes = R.string.bio,
            colorRes = R.color.gray,
            inputType = InputType.TYPE_CLASS_TEXT)
        etv_bio?.onFocusChanged { hasFocus -> if (!hasFocus) checkBio() }
        etv_bio?.onActionPressed { checkBio() }

        // Logout
        btn_edit_profile_logout?.setOnClickListener { logOutPressed() }

        // Avatar image
        eiv_edit_profile_avatar.setBigView()
        eiv_edit_profile_avatar.setAddImageView()
        eiv_edit_profile_avatar.setDialogBaseCliclListener(object : EditImageDialogBaseClickListener {
            override fun onGalaryPressed() {
                if(Injector.permissionManager.isPermissionForGalleryGranted(this@EditProfileActivity)) {
                    openGallery()
                }
            }

            override fun onCameraPressed() {
                if(Injector.permissionManager.isPermissionCameraWriteStorageGranted(this@EditProfileActivity)) {
                    openCamera()
                }
            }
        })
    }

    private fun savePressed() {
        if (isFieldValid()) {
            // TODO save user data
            // TODO compare user data from fields and user data fro view model and then save if needed
        }
    }

    private fun logOutPressed() {
        Injector.auth.signOut()
        val intentWelcome = Intent(this, WelcomeActivity::class.java)
        intentWelcome.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intentWelcome)
        finish()
    }

    private fun locationPressed() {
        if(Injector.permissionManager.isPermissionLocationGranted(this)) {
            if (!isLocationEnabled()) {
                locationManager.init(this)
            } else {
                //viewModel.setLocation(Injector.locationManager.getLastLocation(this))
                // TODO open location screen
            }
        }
    }

    fun isLocationEnabled(): Boolean{
        val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun startCropperActivity(uri: Uri) {
        CropImage.activity(uri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(1, 1)
            .start(this)
    }

    private fun openGallery() {
        if(!Injector.permissionManager.isPermissionForGalleryGranted(this)) return

        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, RESULT_GALARY)
            }
        }
    }

    private fun openCamera() {
        if(!Injector.permissionManager.isPermissionCameraWriteStorageGranted(this)) return

        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, RESULT_CAMERA)
            }
        }
    }

    private fun updateUserUI(user: User) {
        etv_name.text = user.name
        etv_bio.text = user.bio
        eiv_edit_profile_avatar.loadImage(user.avatarImgURL)
        // TODO set location
    }

    private fun isFieldValid() : Boolean {
        var isValid = true
        if (checkUsername()) isValid = false
        if (checkBio()) isValid = false
        return isValid
    }

    private fun checkUsername() : Boolean {
        etv_name.setError(FieldsValidatorUtil.isNameValid(etv_name.text, this))
        return etv_name.hasError
    }

    private fun checkBio() : Boolean {
        return if(etv_bio.hasText) {
            etv_bio.setError(FieldsValidatorUtil.isBioValid(etv_bio.text, this))
            etv_bio.hasError
        } else { false }
    }
}
