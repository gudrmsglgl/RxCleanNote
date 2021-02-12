package com.cleannote.notedetail.edit

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.bumptech.glide.RequestManager

import com.cleannote.app.R
import com.cleannote.app.databinding.FragmentNoteDetailEditBinding
import com.cleannote.common.BaseFragment
import com.cleannote.common.DateUtil
import com.cleannote.common.dialog.DeleteDialog
import com.cleannote.extension.*
import com.cleannote.extension.menu.visibleIcon
import com.cleannote.extension.rxbinding.singleClick
import com.cleannote.model.NoteImageUiModel
import com.cleannote.notedetail.Keys.REQUEST_KEY_ON_BACK
import com.cleannote.notedetail.Keys.REQ_DELETE_KEY
import com.cleannote.notedetail.Keys.REQ_UPDATE_KEY
import com.cleannote.presentation.data.State.ERROR
import com.cleannote.presentation.data.State.SUCCESS
import com.cleannote.presentation.data.notedetail.DetailToolbarState
import com.cleannote.presentation.data.notedetail.TextMode.*
import com.cleannote.presentation.data.notedetail.DetailToolbarState.TbCollapse
import com.cleannote.presentation.data.notedetail.DetailToolbarState.TbExpanded
import com.cleannote.presentation.model.NoteView
import com.cleannote.presentation.notedetail.NoteDetailViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.jakewharton.rxbinding4.material.offsetChanges
import com.jakewharton.rxbinding4.widget.itemClicks
import com.jakewharton.rxbinding4.widget.textChanges
import io.reactivex.rxjava3.core.Observable
import kotlinx.android.synthetic.main.footer_note_detail.view.*
import kotlinx.android.synthetic.main.layout_note_detail_toolbar.*
import java.util.*
import kotlin.math.absoluteValue

class NoteDetailEditFragment constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val dateUtil: DateUtil,
    private val glideRequestManager: RequestManager
) : BaseFragment<FragmentNoteDetailEditBinding>(R.layout.fragment_note_detail_edit) {

    private val viewModel
            by navGraphViewModels<NoteDetailViewModel>(R.id.nav_detail_graph) { viewModelFactory }

    private lateinit var imageAdapter: EditImagesAdapter

    private val collapseBoundary = -85

    private var hasKeyOnBackPress: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBinding()
        initFooterRcvImages()
        footerImageDeleteIconOnClick()

        toolbarStateSource()
        textChangeEditModeSource()
        toolbarRightMenuSource()

        subscribeUpdateNote()
        subscribeDeleteNote()
    }

    override fun initBinding() {
        binding.apply {
            vm = viewModel
            fragment = this@NoteDetailEditFragment
        }
    }

    private fun initFooterRcvImages() = binding
        .footer
        .rcyImages
        .apply {
            imageAdapter = EditImagesAdapter(glideRequestManager)
            adapter = imageAdapter
            addItemDecoration(HorizontalItemDecoration(15))
        }

    private fun footerImageDeleteIconOnClick() = imageAdapter
        .imageDeleteSubject
        .subscribe {
            showImageDeleteDialog(it)
        }
        .addCompositeDisposable()

    private fun textChangeEditModeSource() = Observable.merge(
        etTitle().textChanges().filter { etTitle().isFocused && !isTitleModified()},
        etBody().textChanges().filter { etBody().isFocused && !isBodyModified()})
        .subscribe { editMode() }
        .addCompositeDisposable()

    private fun toolbarStateSource() = binding
        .appBar
        .offsetChanges()
        .map { transToolbarState(it) }
        .subscribe {
            viewModel.setToolbarState(it)
            noteTitleAlpha()
        }
        .addCompositeDisposable()

    private fun toolbarRightMenuSource() = binding
        .detailToolbar
        .rightIcon
        .singleClick()
        .map { !isEditDoneMenu() }
        .subscribe { isDeleteMenu ->
            if (isDeleteMenu) showNoteDeleteDialog()
            else editDoneMode()
        }

    private fun subscribeUpdateNote() = viewModel
        .updatedNote
        .observe(viewLifecycleOwner, Observer {
            if (it != null){
                when (it.status) {
                    is SUCCESS -> {
                        showToast(getString(R.string.updateSuccessMsg))
                        hasKeyOnBackPress = REQ_UPDATE_KEY
                    }
                    is ERROR -> {
                        showErrorDialog(getString(R.string.updateErrorMsg))
                        it.sendFirebaseThrowable()
                    }
                }
            }
        })

    private fun subscribeDeleteNote() = viewModel
        .deletedNote
        .observe( viewLifecycleOwner, Observer {
            if (it != null){
                when (it.status) {
                    is SUCCESS -> {
                        showToast(getString(R.string.deleteSuccessMsg))
                        hasKeyOnBackPress = REQ_DELETE_KEY
                        navPopBackStack(inclusive = true)
                    }
                    is ERROR -> {
                        showErrorDialog(getString(R.string.deleteErrorMsg))
                        it.sendFirebaseThrowable()
                    }
                }
            }
        })

    fun navPopBackStack(inclusive: Boolean = false){
        view?.clearFocus()
        requestToNoteList()
        if (inclusive)
            findNavController().popBackStack(R.id.noteDetailViewFragment, inclusive)
        else
            findNavController().popBackStack()
    }

    private fun showNoteDeleteDialog() = activity?.let{
        DeleteDialog(it, viewLifecycleOwner)
            .showDialog(getString(R.string.delete_message))
            .positiveButton {
                viewModel.deleteNote(currentNote())
            }
    }

    private fun showImageDeleteDialog(imageModel: NoteImageUiModel) = activity?.let {
        DeleteDialog(it, viewLifecycleOwner)
            .showDialog(getString(R.string.delete_image_message))
            .positiveButton {
                viewModel.deleteImage(imageModel.imgPath, dateUtil.getCurrentTimestamp())
            }
    }

    fun addImagePopupMenu(view: View){
        activity?.let {
            getView()?.clearFocus()
            view.hideKeyboard()
            PopupMenu(it, view).apply {
                menuInflater.inflate(R.menu.menu_image_add, menu)
                visibleIcon(it)
                itemClicks()
                    .subscribe { menuItem ->
                        when (menuItem.itemId){
                            R.id.album -> {
                                loadImagePicker(PickerType.GALLERY)
                            }
                            R.id.camera -> {
                                loadImagePicker(PickerType.CAMERA)
                            }
                            R.id.link -> {
                                inputLinkDialog()
                            }
                        }
                    }
                    .addCompositeDisposable()
                show()
            }
        }
    }

    private fun loadImagePicker(type: PickerType){
        val builder = ImagePicker.with(this)
        if (type == PickerType.CAMERA)
            builder.cameraOnly()
        else
            builder.galleryOnly()
        builder.compress(1024)
            .start { resultCode, data ->
                if (resultCode == Activity.RESULT_OK) {
                    //You can also get File Path from intent
                    val filePath:String = ImagePicker.getFilePath(data)!!
                    viewModel.uploadImage(filePath,dateUtil.getCurrentTimestamp())
                } else if (resultCode == ImagePicker.RESULT_ERROR) {
                    showToast(ImagePicker.getError(data))
                } else {
                    showToast(getString(R.string.cancel_message))
                }
            }
    }

    private fun inputLinkDialog() = activity?.let { context ->
        view?.clearFocus()
        LinkImageDialog(context, glideRequestManager, viewLifecycleOwner)
            .onUploadImage {
                if (it.isNotEmpty())
                    viewModel.uploadImage(it, dateUtil.getCurrentTimestamp())
            }
    }

    private fun etTitle() = binding.editTitle
    private fun etBody() = binding.noteBody
    private fun tbLeftIcon() = binding.detailToolbar.leftIcon
    private fun tbRightIcon() = binding.detailToolbar.rightIcon

    private fun isTitleModified() = viewModel.finalNote()?.title == etTitle().text.toString()
    private fun isBodyModified() = viewModel.finalNote()?.body == etBody().text.toString()

    private fun currentNote() = viewModel.finalNote() ?: emptyNoteView()

    private fun emptyNoteView() = NoteView(
        id = UUID.randomUUID().toString(), title = "", body = "", updatedAt = "",createdAt = "", noteImages = null
    )

    fun editCancel() = viewModel.editCancel()
    private fun editMode() = viewModel.editMode()
    private fun editDoneMode() = viewModel.editDoneMode(
        currentNote().copy(
            title = etTitle().text.toString(),
            body = etBody().text.toString(),
            updatedAt = dateUtil.getCurrentTimestamp()
        )
    )

    fun isEditCancelMenu(): Boolean = tbLeftIcon().drawable.equalDrawable(R.drawable.ic_cancel_24dp)
    private fun isEditDoneMenu(): Boolean = tbRightIcon().drawable.equalDrawable(R.drawable.ic_done_24dp)

    private fun transToolbarState(offset: Int): DetailToolbarState =
        if (offset < collapseBoundary) TbCollapse else TbExpanded

    private fun noteTitleAlpha(){
        val alpha = (binding.appBar.y / binding.appBar.totalScrollRange).absoluteValue
        binding.editTitle.alpha = 1 - alpha
        binding.detailToolbar.toolBarTitle.alpha = alpha
    }

    private fun requestToNoteList(){
        hasKeyOnBackPress?.let { reqIdentityKey ->
            setFragmentResult(
                REQUEST_KEY_ON_BACK,
                bundleOf(reqIdentityKey to viewModel.finalNote()?.transNoteUiModel())
            )
        }
    }

    enum class PickerType{
        CAMERA, GALLERY
    }

}
