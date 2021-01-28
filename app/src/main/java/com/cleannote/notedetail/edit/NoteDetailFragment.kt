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
import com.afollestad.materialdialogs.MaterialDialog
import com.bumptech.glide.RequestManager

import com.cleannote.app.R
import com.cleannote.app.databinding.FragmentNoteDetailBinding
import com.cleannote.common.BaseFragment
import com.cleannote.common.DateUtil
import com.cleannote.extension.*
import com.cleannote.extension.menu.visibleIcon
import com.cleannote.model.NoteUiModel
import com.cleannote.notedetail.Keys.NOTE_DETAIL_BUNDLE_KEY
import com.cleannote.notedetail.Keys.REQUEST_KEY_ON_BACK
import com.cleannote.notedetail.Keys.REQ_DELETE_KEY
import com.cleannote.notedetail.Keys.REQ_UPDATE_KEY
import com.cleannote.presentation.data.State.ERROR
import com.cleannote.presentation.data.State.SUCCESS
import com.cleannote.presentation.data.notedetail.TextMode.*
import com.cleannote.presentation.data.notedetail.DetailToolbarState.TbCollapse
import com.cleannote.presentation.data.notedetail.DetailToolbarState.TbExpanded
import com.cleannote.presentation.notedetail.NoteDetailViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.jakewharton.rxbinding4.material.offsetChanges
import com.jakewharton.rxbinding4.widget.itemClicks
import com.jakewharton.rxbinding4.widget.textChanges
import io.reactivex.rxjava3.core.Observable
import kotlinx.android.synthetic.main.footer_note_detail.view.*
import kotlinx.android.synthetic.main.fragment_note_detail.*
import kotlinx.android.synthetic.main.layout_note_detail_toolbar.*

class NoteDetailFragment constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val dateUtil: DateUtil,
    private val glideRequestManager: RequestManager
) : BaseFragment<FragmentNoteDetailBinding>(R.layout.fragment_note_detail) {

    private val viewModel
            by navGraphViewModels<NoteDetailViewModel>(R.id.nav_detail_graph) { viewModelFactory }

    private val COLLAPSING_TOOLBAR_VISIBILITY_THRESHOLD = -85

    lateinit var noteUiModel: NoteUiModel

    var onBackPressThenKey: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initBinding()
        getPreviousFragmentNote()
        initRecyclerImages()

        appBarOffSetChangeSource()
        titleBodyChangeSource()

        subscribeUpdateNote()
        subscribeDeleteNote()
    }

    override fun initBinding() {
        binding.apply {
            vm = viewModel
            fragment = this@NoteDetailFragment
        }
    }

    private fun initRecyclerImages(){
        binding.noteBodyContainer.rcy_images.apply {
            adapter = EditImagesAdapter(glideRequestManager)
            addItemDecoration(HorizontalItemDecoration(15))
        }
    }

    private fun subscribeUpdateNote() = viewModel.updatedNote
        .observe(viewLifecycleOwner, Observer {
            if (it != null){
                when (it.status) {
                    is SUCCESS -> {
                        showToast(getString(R.string.updateSuccessMsg))
                        onBackPressThenKey = REQ_UPDATE_KEY
                    }
                    is ERROR -> {
                        showToast(getString(R.string.updateErrorMsg))
                        it.sendFirebaseThrowable()
                    }
                }
            }
        })

    private fun subscribeDeleteNote() = viewModel.deletedNote
        .observe( viewLifecycleOwner, Observer {
            if (it != null){
                when (it.status) {
                    is SUCCESS -> {
                        showToast(getString(R.string.deleteSuccessMsg))
                        onBackPressThenKey = REQ_DELETE_KEY
                        navNoteListFragment()
                    }
                    is ERROR -> {
                        it.sendFirebaseThrowable()
                        showToast(getString(R.string.deleteErrorMsg))
                    }
                }
            }
        })

    private fun titleBodyChangeSource() = Observable.merge(
        note_title.textChanges().filter { note_title.isFocused  && noteUiModel.title != note_title.text.toString()},
        note_body.textChanges().filter { note_body.isFocused && noteUiModel.body != note_body.text.toString()})
        .subscribe { editMode() }
        .addCompositeDisposable()

    fun showDeleteDialog() {
        activity?.let {
            MaterialDialog(it).show {
                title(R.string.delete_title)
                message(R.string.delete_message)
                positiveButton(R.string.delete_ok){
                    viewModel.deleteNote(noteUiModel.transNoteView())
                }
                negativeButton(R.string.delete_cancel){
                    showToast(getString(R.string.deleteCancelMsg))
                    dismiss()
                }
                cancelable(false)
            }
        }
    }

    private fun getPreviousFragmentNote(){
        arguments?.let {
            noteUiModel = it[NOTE_DETAIL_BUNDLE_KEY] as NoteUiModel
            defaultMode()
        }
    }


    private fun appBarOffSetChangeSource() = app_bar.offsetChanges()
        .map { offset ->
            if (offset < COLLAPSING_TOOLBAR_VISIBILITY_THRESHOLD) TbCollapse
            else TbExpanded
        }
        .subscribe { viewModel.setToolbarState(it) }
        .addCompositeDisposable()


    private fun defaultMode() = viewModel.defaultMode(
        viewModel.finalNote() ?: noteUiModel.transNoteView()
    )

    fun editCancel() = viewModel.editCancel()

    private fun editMode() = viewModel.editMode()

    fun editDoneMode() = viewModel.editDoneMode(
        noteUiModel.copy(
            title = note_title.text.toString(),
            body = note_body.text.toString(),
            updatedAt = dateUtil.getCurrentTimestamp()
        ).transNoteView()
    )


    fun navNoteListFragment(){
        view?.clearFocus()
        onBackPressThenKey?.let { reqKey ->
            setFragmentResult(REQUEST_KEY_ON_BACK , bundleOf(reqKey to viewModel.finalNote()?.transNoteUiModel()))
        }
        findNavController().popBackStack()
    }

    fun isEditCancelMenu(): Boolean = toolbar_primary_icon.drawable
        .equalDrawable(R.drawable.ic_cancel_24dp)

    fun isEditDoneMenu(): Boolean = toolbar_secondary_icon.drawable
        .equalDrawable(R.drawable.ic_done_24dp)

    fun showAddImagePopupMenu(view: View){
        activity?.let {
            PopupMenu(it, view).apply {
                val inflater = menuInflater
                inflater.inflate(R.menu.menu_image_add, menu)
                visibleIcon(it)
                itemClicks()
                    .subscribe {
                        when (it.itemId){
                            R.id.album -> {
                                loadImagePicker(PickerType.GALLERY)
                            }
                            R.id.camera -> {
                                loadImagePicker(PickerType.CAMERA)
                            }
                            R.id.link -> {
                                println("todo: InsertImageLink Then add Image")
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
                    showToast("취소 되었습니다.")
                }
            }
    }

    enum class PickerType{
        CAMERA, GALLERY
    }
}
