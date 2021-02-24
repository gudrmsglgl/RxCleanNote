package com.cleannote.notedetail.edit

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.EditText
import androidx.annotation.VisibleForTesting
import androidx.annotation.VisibleForTesting.PRIVATE
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
import com.cleannote.extension.menu.showImageLoaderMenu
import com.cleannote.extension.rxbinding.singleClick
import com.cleannote.model.NoteImageUiModel
import com.cleannote.notedetail.Keys.REQUEST_KEY_ON_BACK
import com.cleannote.notedetail.Keys.REQ_DELETE_KEY
import com.cleannote.notedetail.Keys.REQ_UPDATE_KEY
import com.cleannote.notedetail.edit.dialog.LoadingImageUpdateDialog
import com.cleannote.notedetail.edit.factory.NoteFactory
import com.cleannote.presentation.data.State.*
import com.cleannote.presentation.data.notedetail.DetailToolbarState
import com.cleannote.presentation.data.notedetail.TextMode.*
import com.cleannote.presentation.data.notedetail.DetailToolbarState.TbCollapse
import com.cleannote.presentation.data.notedetail.DetailToolbarState.TbExpanded
import com.cleannote.presentation.notedetail.NoteDetailViewModel
import com.jakewharton.rxbinding4.core.scrollChangeEvents
import com.jakewharton.rxbinding4.material.offsetChanges
import com.jakewharton.rxbinding4.widget.textChanges
import io.reactivex.rxjava3.core.Observable
import kotlinx.android.synthetic.main.footer_note_detail.view.*
import kotlinx.android.synthetic.main.layout_note_detail_toolbar.*
import java.util.*
import java.util.concurrent.TimeUnit

class NoteDetailEditFragment constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val dateUtil: DateUtil,
    private val glideRequestManager: RequestManager
) : BaseFragment<FragmentNoteDetailEditBinding>(R.layout.fragment_note_detail_edit) {

    private val viewModel
            by navGraphViewModels<NoteDetailViewModel>(R.id.nav_detail_graph) { viewModelFactory }
    private val imageLoader: ImageLoader
            by lazy { ImageLoader(this, glideRequestManager) }
    private val lottieLoadingDialog: LoadingImageUpdateDialog
            by lazy { LoadingImageUpdateDialog.newInstance() }
    private lateinit var imageAdapter: EditImagesAdapter

    private val collapseBoundary = -85
    private var hasKeyOnBackPress: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStatusBarTextBlack()
        initBinding()
        initFooterRcvImages()
        footerImageDeleteIconOnClick()
        changeTbState()
        changeEditMode(binding.editTitle, binding.noteBody)
        tbRightMenuOnClick()
        scrollReleaseFocus()
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
        .rcvImages
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

    private fun changeEditMode(
        title: EditText,
        body: EditText
    ) = Observable.merge(
        title.textChanges()
            .filter { title.isFocused && !viewModel.isTitleModified(title.text.toString())},
        body.textChanges()
            .filter { body.isFocused && !viewModel.isBodyModified(body.text.toString())})
        .subscribe { viewModel.editMode() }
        .addCompositeDisposable()

    private fun changeTbState() = binding
        .appBar
        .offsetChanges()
        .map { transToolbarState(it) }
        .subscribe {
            viewModel.setToolbarState(it)
            noteTitleAlpha()
        }
        .addCompositeDisposable()

    private fun tbRightMenuOnClick() = binding
        .detailToolbar
        .rightIcon
        .singleClick()
        .map { !isEditDoneMenu() }
        .subscribe { isDeleteMenu ->
            if (isDeleteMenu) showNoteDeleteDialog()
            else editDoneMode()
        }
        .addCompositeDisposable()

    private fun scrollReleaseFocus() = binding
        .editBody
        .scrollChangeEvents()
        .map { it.scrollY }
        .throttleFirst(250L, TimeUnit.MILLISECONDS)
        .subscribe {
            val pView = view
            if (it != 0 && pView != null) {
                pView.clearFocus()
                pView.hideKeyboard()
            }
        }
        .addCompositeDisposable()

    private fun subscribeUpdateNote() = viewModel
        .updatedNote
        .observe(viewLifecycleOwner, Observer {
            if (it != null){
                when (it.status) {
                    is LOADING -> {
                        hideLottieImgLoadingDialog()
                    }
                    is SUCCESS -> {
                        showUpdateMsg()
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
            it.showImageLoaderMenu(view){ menuType ->
                imageLoader
                    .onLoaded(menuType) { path ->
                        if (path.isNotEmpty()){
                            viewModel.uploadImage(path, dateUtil.getCurrentTimestamp())
                            showLottieImgLoadingDialog()
                        }
                    }
            }
        }
    }

    private fun editDoneMode() = viewModel.editDoneMode(
        currentNote().copy(
            title = binding.editTitle.text.toString(),
            body = binding.noteBody.text.toString(),
            updatedAt = dateUtil.getCurrentTimestamp()
        )
    )

    private fun transToolbarState(offset: Int): DetailToolbarState {
        return if (offset < collapseBoundary){
            binding.editTitle.contentDescription = getString(R.string.desc_state_collapse)
            TbCollapse
        } else {
            binding.editTitle.contentDescription = getString(R.string.desc_state_expanded)
            TbExpanded
        }
    }

    private fun noteTitleAlpha(){
        val alpha = binding.appBar.offsetChangeRatio()
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

    private fun showLottieImgLoadingDialog(){
        if (!lottieLoadingDialog.isAdded) lottieLoadingDialog.show(childFragmentManager, "lottie_dialog")
    }

    private fun hideLottieImgLoadingDialog(){
        if (lottieLoadingDialog.isAdded){
            Handler().postDelayed({
                lottieLoadingDialog.dismiss()
            }, 1700)
        }
    }

    private fun showUpdateMsg(){
        if (lottieLoadingDialog.isAdded){
            Handler().postDelayed({
                showToast(getString(R.string.updateSuccessMsg))
            }, 1600)
        }
        else
            showToast(getString(R.string.updateSuccessMsg))
    }

    private fun currentNote() = viewModel.finalNote() ?: NoteFactory.defaultNote()
    fun isEditCancelMenu(): Boolean = binding.detailToolbar.leftIcon.drawable.equalDrawable(R.drawable.ic_cancel_24dp)
    private fun isEditDoneMenu(): Boolean = binding.detailToolbar.rightIcon.drawable.equalDrawable(R.drawable.ic_done_24dp)

    @VisibleForTesting(otherwise = PRIVATE)
    fun initNoteDefaultMode(){
        viewModel.defaultMode(currentNote())
    }
}
