package com.tohir.booksandstuff.ui.books

import android.content.Context
import android.os.Bundle
import android.view.ActionMode
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commitNow
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.tohir.booksandstuff.R
import com.tohir.booksandstuff.databinding.BottomSheetDialogLayoutBinding
import com.tohir.booksandstuff.databinding.FragmentReaderBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.readium.r2.navigator.VisualNavigator
import org.readium.r2.navigator.epub.EpubNavigatorFactory
import org.readium.r2.navigator.epub.EpubNavigatorFragment
import org.readium.r2.navigator.epub.EpubPreferences
import org.readium.r2.navigator.input.InputListener
import org.readium.r2.navigator.input.TapEvent
import org.readium.r2.navigator.preferences.FontFamily
import org.readium.r2.navigator.preferences.TextAlign
import org.readium.r2.navigator.util.BaseActionModeCallback
import org.readium.r2.shared.publication.Publication

class EpubReaderFragment : Fragment() {

    private val userPreferences by lazy {
        requireContext().getSharedPreferences(
            "user_pref",
            Context.MODE_PRIVATE
        )
    }

    private val viewModel: ReaderViewModel by viewModels()
    private lateinit var navigator: EpubNavigatorFragment
    private lateinit var binding: FragmentReaderBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReaderBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        val bookUri = arguments?.getString("BOOK_PATH") ?: arguments?.getString("BOOK_URI")
        val bookId = arguments?.getInt("BOOK_ID")

        if (bookUri != null) {
            lifecycleScope.launch {
                val publication =
                    viewModel.importPublication(bookUri.toUri(), requireContext(), bookId)

                if (publication != null) {
                    val navigatorFactory = EpubNavigatorFactory(publication = publication)
                    childFragmentManager.fragmentFactory = navigatorFactory.createFragmentFactory(
                        initialLocator = viewModel.restoreReadingProgression(bookId!!),
                        configuration = EpubNavigatorFragment.Configuration {
                            selectionActionModeCallback = customSelectionActionModeCallback
                        }

                    )
                } else {
                    return@launch
                }

                val tag = "EpubNavigatorFragment"
                if (savedInstanceState == null) {
                    childFragmentManager.commitNow {
                        add(
                            R.id.fragment_reader_container,
                            EpubNavigatorFragment::class.java,
                            Bundle(),
                            tag
                        )
                    }
                }

                navigator = childFragmentManager.findFragmentByTag(tag) as EpubNavigatorFragment

                setupClickListeners()

                (navigator as VisualNavigator).apply {
                    addInputListener(object : InputListener {
                        override fun onTap(event: TapEvent): Boolean {
                            showButtons()
                            return true
                        }
                    })
                }

                setupPreferences(publication)
                saveReadingProgression(bookId)
            }

        }

    }

    fun setupClickListeners() {
        binding.imageViewCancelButton.setOnClickListener {
            requireActivity().finish()
        }

        binding.imageViewOptions.setOnClickListener {
            showSettings()
        }
    }

    private fun showSettings() {
        val dialogBinding = BottomSheetDialogLayoutBinding.inflate(layoutInflater).apply {

            val fontFamilies = listOf("san-serif", "serif", "cursive", "fantasy", "monospace")
            val itemsAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, fontFamilies)
            itemsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerFontFamily.adapter = itemsAdapter

            setInitialValuesForSettings(this, itemsAdapter)

        }

        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(dialogBinding.root)

        dialog.show()


        dialogBinding.buttonApply.setOnClickListener {
            saveUserPreferences(dialogBinding)

            dialog.dismiss()
        }
    }

    fun setInitialValuesForSettings(
        dialogBinding: BottomSheetDialogLayoutBinding,
        itemsAdapter1: ArrayAdapter<String>
    ) {

        dialogBinding.apply {

            justifyContentSwitch.isChecked = userPreferences.getBoolean(JUSTIFY_CONTENT, false)

            val savedValue = userPreferences.getString(FONT_FAMILY, null)
            val position = itemsAdapter1.getPosition(savedValue)
            spinnerFontFamily.setSelection(position)

            dialogBinding.sliderFontSize.value = userPreferences.getFloat(FONT_SIZE, 1f)

            dialogBinding.sliderLineSpacing.value = userPreferences.getFloat(LINE_HEIGHT, 1f)

            scrollSwitch.isChecked = userPreferences.getBoolean(SCROLL, false)

            justifyContentSwitch.isChecked = userPreferences.getBoolean(JUSTIFY_CONTENT, false)
        }

    }

    private fun setupPreferences(publication: Publication) {
        val preferences = EpubPreferences(
            fontFamily = FontFamily.SANS_SERIF,
            fontSize = 1.0,
            lineHeight = 1.0,
            scroll = false,
            publisherStyles = false,
            textAlign = TextAlign.START
        )

        val editor = EpubNavigatorFactory(publication).createPreferencesEditor(preferences)

        editor.apply {

            if (userPreferences.getString(FONT_FAMILY, null) != null) {
                fontFamily.set(FontFamily(userPreferences.getString(FONT_FAMILY, null)!!))
            }

            fontSize.set(userPreferences.getFloat(FONT_SIZE, 1.0f).toDouble())
            scroll.set(userPreferences.getBoolean(SCROLL, false))
            lineHeight.set(userPreferences.getFloat(LINE_HEIGHT, 1.0f).toDouble())

            if (userPreferences.getBoolean("JUSTIFY_CONTENT", false)) {
                textAlign.set(TextAlign.START)
            }
        }

        navigator.submitPreferences(editor.preferences)
    }

    suspend fun saveReadingProgression(bookId: Int) {

        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            navigator.currentLocator
                .onEach { viewModel.saveReadingProgression(it, bookId) }
                .launchIn(this)
        }
    }


    private fun showButtons() {

        binding.apply {

            if (imageViewCancelButton.isVisible && imageViewOptions.isVisible) {
                imageViewCancelButton.visibility = View.INVISIBLE
                imageViewOptions.visibility = View.INVISIBLE

            } else if (imageViewCancelButton.isInvisible && imageViewOptions.isInvisible) {
                imageViewCancelButton.visibility = View.VISIBLE
                imageViewOptions.visibility = View.VISIBLE
            }

        }

    }


    private fun saveUserPreferences(dialogBinding: BottomSheetDialogLayoutBinding) {

        dialogBinding.apply {
            userPreferences.edit {
                putString(FONT_FAMILY, spinnerFontFamily.selectedItem.toString())
                putFloat(FONT_SIZE, sliderFontSize.value)
                putFloat(LINE_HEIGHT, sliderLineSpacing.value)
                putBoolean(SCROLL, scrollSwitch.isChecked)
                putBoolean(JUSTIFY_CONTENT, justifyContentSwitch.isChecked)

            }
        }
    }

    val customSelectionActionModeCallback: ActionMode.Callback by lazy { SelectionActionModeCallBack() }

    private inner class SelectionActionModeCallBack : BaseActionModeCallback() {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {

            mode?.menuInflater?.inflate(R.menu.menu_action_mode, menu)

            menu?.findItem(R.id.highlight)?.isVisible = true
            menu?.findItem(R.id.underline)?.isVisible = true
            menu?.findItem(R.id.note)?.isVisible = true
            menu?.findItem(R.id.dictionary)?.isVisible = true

            return true

        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            return true
        }

    }

    companion object {
        const val FONT_FAMILY = "FONT_FAMILY"
        const val FONT_SIZE = "FONT_SIZE"
        const val LINE_HEIGHT = "LINE_HEIGHT"
        const val SCROLL = "SCROLL"
        const val JUSTIFY_CONTENT = "JUSTIFY_CONTENT"


    }

}
