package com.cleannote.notelist

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.IdRes
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import com.cleannote.app.R
import com.cleannote.app.databinding.LayoutMultideleteToolbarBinding
import com.cleannote.domain.Constants
import com.cleannote.extension.rxbinding.singleClick
import com.cleannote.notelist.dialog.ListFilterDialog
import com.jakewharton.rxbinding4.appcompat.queryTextChangeEvents
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import java.util.concurrent.TimeUnit

class NoteListTbFactory(private val fragment: NoteListFragment) {

    private val compositeDisposable = CompositeDisposable()

    fun addSearchViewToolbarContainer() = fragment.view?.let {
        fragment.binding.toolbarContentContainer.apply {
            removeAllViews()
            addView(searchToolbarLayout(it.context))
            setupSearchViewToolbar()
        }
    }

    private fun searchToolbarLayout(context: Context) = View
        .inflate(context, R.layout.layout_search_toolbar, null)
        .apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

    private fun LinearLayout.setupSearchViewToolbar() =
        findViewById<Toolbar>(R.id.search_toolbar)
        .apply {
            searchViewSetQuery(R.id.sv, fragment.viewModel.queryLike)
            searchEventSource(R.id.sv)
            filterMenu()
        }

    private fun Toolbar.searchViewSetQuery(
        @IdRes idRes: Int,
        keyword: String
    ) = findViewById<SearchView>(idRes)
        .apply {
            if (keyword.isNotEmpty()) {
                isIconified = false
                setQuery(keyword, false)
                clearFocus()
            }
            else
                isIconified = true
        }

    private fun Toolbar.searchEventSource(@IdRes idRes: Int) = findViewById<SearchView>(idRes)
        .apply {
            queryTextChangeEvents()
                .skipInitialValue()
                .debounce(1000, TimeUnit.MILLISECONDS)
                .subscribe { fragment.viewModel.searchKeyword(it.queryText.toString()) }
                .addCompositeDisposable()
        }

    private fun Toolbar.filterMenu() = findViewById<ImageView>(R.id.action_filter)
        .singleClick()
        .subscribe {
            showFilterDialog()
        }
        .addCompositeDisposable()

    fun addMultiDeleteToolbarContainer() = fragment.view?.let {
        fragment.binding
            .toolbarContentContainer
            .apply {
                removeAllViews()
                addView(multiDeleteToolbarBinding(this).root)
            }
    }

    private fun multiDeleteToolbarBinding(parent: ViewGroup): LayoutMultideleteToolbarBinding {
        val binding: LayoutMultideleteToolbarBinding = fragment.bindingInflate(R.layout.layout_multidelete_toolbar, parent)
        return binding.apply {
            fragment = this@NoteListTbFactory.fragment
            adapter = this@NoteListTbFactory.fragment.noteAdapter
        }
    }

    fun setupSearchViewToolbarInit() = fragment.binding
        .toolbarContentContainer
        .findViewById<Toolbar>(R.id.search_toolbar)
        ?.findViewById<SearchView>(R.id.sv)
        ?.apply {
            onActionViewCollapsed()
            setQuery(null, false)
            clearFocus()
        }

    private fun showFilterDialog() {
        fragment.activity?.let { activity ->
            ListFilterDialog(activity, fragment.sharedPref, fragment.viewLifecycleOwner)
                .showDialog { dialog, checkedOrder ->
                    saveCacheThenOrdering(checkedOrder)
                    fragment.scrollTop()
                    dialog.dismiss()
                }
        }
    }

    private fun saveCacheThenOrdering(order: String) = with (fragment) {
        sharedPref.edit().putString(Constants.FILTER_ORDERING_KEY, order).apply()
        viewModel.setOrdering(order)
    }

    fun rxDispose(){
        compositeDisposable.clear()
    }

    private fun Disposable.addCompositeDisposable(){
        compositeDisposable.add(this)
    }
}