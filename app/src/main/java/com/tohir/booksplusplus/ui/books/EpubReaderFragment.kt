package com.tohir.booksplusplus.ui.books


import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.ActionMode
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.ColorInt
import androidx.core.content.edit
import androidx.core.graphics.toColorInt
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
import com.tohir.booksplusplus.R
import com.tohir.booksplusplus.data.database.DictionaryProvider
import com.tohir.booksplusplus.data.model.Highlight
import com.tohir.booksplusplus.databinding.BottomSheetDialogLayoutBinding
import com.tohir.booksplusplus.databinding.FragmentReaderBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.readium.r2.navigator.Decoration
import org.readium.r2.navigator.SelectableNavigator
import org.readium.r2.navigator.VisualNavigator
import org.readium.r2.navigator.epub.EpubNavigatorFactory
import org.readium.r2.navigator.epub.EpubNavigatorFragment
import org.readium.r2.navigator.epub.EpubPreferences
import org.readium.r2.navigator.epub.EpubPreferencesEditor
import org.readium.r2.navigator.epub.css.FontStyle
import org.readium.r2.navigator.epub.css.FontWeight
import org.readium.r2.navigator.input.InputListener
import org.readium.r2.navigator.input.TapEvent
import org.readium.r2.navigator.preferences.ColumnCount
import org.readium.r2.navigator.preferences.FontFamily
import org.readium.r2.navigator.preferences.TextAlign
import org.readium.r2.navigator.util.BaseActionModeCallback
import org.readium.r2.shared.ExperimentalReadiumApi
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.publication.services.positions

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
    private var bookUri: String? = null
    private var bookId: Long? = null
    private var publication: Publication? = null

    val FontFamily.Companion.ROBOTO get() = FontFamily("Roboto")
    val FontFamily.Companion.OPEN_SANS get() = FontFamily("OpenSans")

    @OptIn(ExperimentalReadiumApi::class)
    private val editor: EpubPreferencesEditor by lazy {
        val preferences = EpubPreferences(

            columnCount = ColumnCount.ONE,
            fontFamily = FontFamily.SANS_SERIF,
            fontSize = 1.0,
            lineHeight = 1.0,
            scroll = false,
            publisherStyles = false,
            textAlign = TextAlign.START,
        )

        EpubNavigatorFactory(publication!!).createPreferencesEditor(preferences)
    }

    @OptIn(ExperimentalReadiumApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {


        bookUri = arguments?.getString("BOOK_PATH") ?: arguments?.getString("BOOK_URI")
        bookId = arguments?.getLong("BOOK_ID")

        if (bookUri != null) {
            publication = runBlocking {
                viewModel.importPublication(
                    bookUri!!.toUri(),
                    requireContext(),
                    bookId
                )
            }


            if (publication != null) {
                val navigatorFactory = EpubNavigatorFactory(publication = publication!!)


                childFragmentManager.fragmentFactory = navigatorFactory.createFragmentFactory(
                    initialLocator = runBlocking { viewModel.restoreReadingProgression(bookId!!) },
                    configuration = EpubNavigatorFragment.Configuration {
                        selectionActionModeCallback = customSelectionActionModeCallback

                        servedAssets += "font/.*"


                        addFontFamilyDeclaration(FontFamily.OPEN_SANS) {
                            addFontFace {
                                addSource("font/open_sans.ttf", preload = true)
                                setFontStyle(FontStyle.NORMAL)
                                setFontWeight(FontWeight.NORMAL)
                            }

                        }

                        addFontFamilyDeclaration(FontFamily.ROBOTO) {
                            addFontFace {
                                addSource("font/roboto_regular.ttf", preload = true)
                                setFontStyle(FontStyle.NORMAL)
                                setFontWeight(FontWeight.NORMAL)
                            }
                        }
                    }
                )
            } else {
                throw IllegalStateException("Publication is null")
            }
        }
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReaderBinding.inflate(inflater, container, false)

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

        return binding.root
    }

    @OptIn(ExperimentalReadiumApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()



        (navigator as VisualNavigator).apply {
            addInputListener(object : InputListener {
                override fun onTap(event: TapEvent): Boolean {
                    showButtons()
                    return true
                }
            })
        }

        setupHighlights()
        setupPreferences()
        setPageNumber()
    }

    override fun onPause() {
        super.onPause()
        saveReadingProgress()
    }

    private fun setupHighlights() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getAllHighlights(bookId!!).collectLatest { highlightList ->

                val decorations = highlightList.map { highlight ->
                    Decoration(
                        id = "decoration-${highlight.id}",
                        highlight.locator,
                        Decoration.Style.Highlight(highlight.tint)
                    )

                }


                navigator.applyDecorations(decorations, "user-highlights")

            }

        }
    }

    private fun saveReadingProgress() {
        viewLifecycleOwner.lifecycleScope.launch { saveReadingProgression(bookId!!) }
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

            val fontFamilies = listOf(
                "san-serif",
                "serif",
                "cursive",
                "fantasy",
                "monospace",
                "OpenDyslexic",
                "OpenSans",
                "Roboto"
            )
            val itemsAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, fontFamilies)
            itemsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerFontFamily.adapter = itemsAdapter

            setInitialValuesForSettings(this, itemsAdapter)

        }

        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(dialogBinding.root)

        dialog.show()

        setupClickListenersForSettings(dialogBinding)

        updatePreferences(dialogBinding)

    }

    private fun setupClickListenersForSettings(dialogBinding: BottomSheetDialogLayoutBinding) {
        dialogBinding.materialCardViewCustomTheme1.setOnClickListener {
            setThemes(
                "#FFFFFF",
                "#000000",
                "Roboto"
            )
        }
        dialogBinding.materialCardViewCustomTheme2.setOnClickListener {
            setThemes(
                "#feefcc",
                "#423b30",
                "OpenSans"
            )
        }
        dialogBinding.materialCardViewCustomTheme3.setOnClickListener {
            setThemes(
                "#222222",
                "#ffffff",
                "OpenDyslexic"
            )
        }
        dialogBinding.materialCardViewCustomTheme4.setOnClickListener {
            setThemes(
                "#feefcc",
                "#504B38",
                "serif"
            )
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


            justifyContentSwitch.isChecked = userPreferences.getBoolean(JUSTIFY_CONTENT, false)
        }

    }

    private fun setupPreferences() {

        editor.apply {

            if (userPreferences.getString(FONT_FAMILY, null) != null) {
                fontFamily.set(FontFamily(userPreferences.getString(FONT_FAMILY, null)!!))
            }

            fontSize.set(userPreferences.getFloat(FONT_SIZE, 1.0f).toDouble())
            scroll.set(
                userPreferences.getBoolean(
                    SCROLL,
                    false
                )
            )
            lineHeight.set(userPreferences.getFloat(LINE_HEIGHT, 1.0f).toDouble())

            if (userPreferences.getString(TEXT_COLOR, null) != null) {

                textColor.set(
                    org.readium.r2.navigator.preferences.Color(
                        userPreferences.getString(
                            TEXT_COLOR,
                            null
                        )!!.toColorInt()
                    )
                )
                backgroundColor.set(
                    org.readium.r2.navigator.preferences.Color(
                        userPreferences.getString(
                            BACKGROUND_COLOR,
                            null
                        )!!.toColorInt()
                    )
                )
            }

            if (userPreferences.getBoolean("JUSTIFY_CONTENT", false)) {
                textAlign.set(TextAlign.START)
            }
        }

        navigator.submitPreferences(editor.preferences)
    }



    suspend fun saveReadingProgression(bookId: Long) {

        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            navigator.currentLocator
                .onEach { viewModel.saveReadingProgression(it, bookId) }
                .launchIn(this)
        }
    }

    fun updatePreferences(dialogBinding: BottomSheetDialogLayoutBinding) {

        dialogBinding.sliderFontSize.addOnChangeListener { _, value, _ ->
            editor.apply {
                fontSize.set(value.toDouble())
            }

            navigator.submitPreferences(editor.preferences)
            userPreferences.edit {
                putFloat(FONT_SIZE, value)
            }
        }

        dialogBinding.spinnerFontFamily.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem = parent?.getItemAtPosition(position)

                    if (selectedItem != null) {
                        editor.apply {
                            fontFamily.set(FontFamily(selectedItem.toString()))
                            navigator.submitPreferences(editor.preferences)
                        }

                        userPreferences.edit { putString(FONT_FAMILY, selectedItem.toString()) }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

            }

        dialogBinding.sliderLineSpacing.addOnChangeListener { _, value, _ ->
            editor.apply {
                lineHeight.set(value.toDouble())
            }
            userPreferences.edit { putFloat(LINE_HEIGHT, value) }
            navigator.submitPreferences(editor.preferences)

        }
    }

    private fun showButtons() {

        binding.apply {

            if (imageViewCancelButton.isVisible && imageViewOptions.isVisible) {
                imageViewCancelButton.visibility = View.INVISIBLE
                imageViewOptions.visibility = View.INVISIBLE
                cardViewPageNumberContainer.visibility = View.INVISIBLE

            } else if (imageViewCancelButton.isInvisible && imageViewOptions.isInvisible) {
                imageViewCancelButton.visibility = View.VISIBLE
                imageViewOptions.visibility = View.VISIBLE
                cardViewPageNumberContainer.visibility = View.VISIBLE
            }
        }

    }

    fun setPageNumber() {

        lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                navigator.currentLocator.collect {

                    if (!publication!!.positions().isEmpty()) {

                        val currentPage = navigator.currentLocator.value.locations.position ?: 1

                        val totalPages = publication!!.positions().size

                        binding.textViewPageNumber.text = "$currentPage of $totalPages"

                    }
                }
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
            menu?.findItem(R.id.copy)?.isVisible = true

            return true

        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {

            when (item.itemId) {
                R.id.highlight -> addHighlight(Highlight.Style.HIGHLIGHT, "#9CCC65".toColorInt())
                R.id.underline -> addHighlight(Highlight.Style.UNDERLINE, "#9CCC65".toColorInt())
                R.id.copy -> lifecycleScope.launch { copy() }
                R.id.dictionary -> lifecycleScope.launch { dictionary() }

            }


            mode.finish()


            return true
        }

        suspend fun dictionary() {

            val selectedWord = (navigator as? SelectableNavigator).let { navigator ->
                navigator?.currentSelection()?.locator?.text?.highlight
            } ?: ""

            val db = DictionaryProvider.getInstance(requireContext())
            val definition = db.dictionaryDao().getDefinition(selectedWord.lowercase()) ?: "No available definitions"

           val dialog = DictionaryBottomSheet.newInstance(selectedWord, definition = definition)

            dialog.show(parentFragmentManager, "DictionaryBottomSheet")
            (navigator as? SelectableNavigator)?.clearSelection()

        }

        private suspend fun copy() {
            val clipboard =
                requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

            val selectedText =
                (navigator as SelectableNavigator).currentSelection()?.locator?.text?.highlight

            clipboard.setPrimaryClip(ClipData.newPlainText("Selected text", selectedText))
            (navigator as SelectableNavigator).clearSelection()

        }

        private fun addHighlight(style: Highlight.Style, @ColorInt tint: Int) {
            viewLifecycleOwner.lifecycleScope.launch {

                (navigator as? SelectableNavigator)?.let { navigator ->
                    navigator.currentSelection()?.let { selection ->

                        viewModel.addHighlight(
                            locator = selection.locator,
                            style = style,
                            tint = tint,
                            bookID = bookId!!
                        )
                    }
                }

            }
        }
    }

    fun setThemes(textColor: String, backgroundColor: String, fontFamily: String) {
        editor.apply {
            this.textColor.set(org.readium.r2.navigator.preferences.Color(textColor.toColorInt()))
            this.backgroundColor.set(org.readium.r2.navigator.preferences.Color(backgroundColor.toColorInt()))
            this.fontFamily.set(FontFamily(fontFamily))
        }

        userPreferences.edit {
            putString(TEXT_COLOR, textColor)
            putString(BACKGROUND_COLOR, backgroundColor)
            putString(FONT_FAMILY, fontFamily)
        }

        navigator.submitPreferences(editor.preferences)
    }

    companion object {
        const val FONT_FAMILY = "FONT_FAMILY"
        const val FONT_SIZE = "FONT_SIZE"
        const val LINE_HEIGHT = "LINE_HEIGHT"
        const val SCROLL = "SCROLL"
        const val JUSTIFY_CONTENT = "JUSTIFY_CONTENT"
        const val TEXT_COLOR = "TEXT_COLOR"
        const val BACKGROUND_COLOR = "BACKGROUND_COLOR"

    }

}
