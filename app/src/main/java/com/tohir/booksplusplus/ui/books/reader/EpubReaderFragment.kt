package com.tohir.booksplusplus.ui.books.reader


import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.graphics.RectF
import android.os.Bundle
import android.os.SystemClock.elapsedRealtime
import android.view.ActionMode
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.annotation.ColorInt
import androidx.core.content.edit
import androidx.core.graphics.toColorInt
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commitNow
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.tohir.booksplusplus.R
import com.tohir.booksplusplus.data.database.DictionaryProvider
import com.tohir.booksplusplus.data.model.Bookmark
import com.tohir.booksplusplus.data.model.Highlight
import com.tohir.booksplusplus.databinding.FragmentReaderBinding
import com.tohir.booksplusplus.dictionary.DictionaryApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.readium.r2.navigator.DecorableNavigator
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
    private val viewModel: EpubReaderViewModel by viewModels()

    private val readerViewModel: ReaderViewModel by activityViewModels()
    private lateinit var navigator: EpubNavigatorFragment
    private lateinit var binding: FragmentReaderBinding
    private var bookUri: String? = null
    private var bookId: Long? = null
    
    private var readingStartTime: Long? = null
    private var publication: Publication? = null

    private val prefs by lazy {
        requireContext().getSharedPreferences(
            "user_pref",
            Context.MODE_PRIVATE
        )
    }

    private val decorableListener by lazy { DecorableListener() }

    val FontFamily.Companion.ROBOTO get() = FontFamily("Roboto")
    val FontFamily.Companion.OPEN_SANS get() = FontFamily("OpenSans")
    val FontFamily.Companion.LEXEND get() = FontFamily("Lexend")
    val FontFamily.Companion.MONTSERRAT get() = FontFamily("Montserrat")
    val FontFamily.Companion.POIRET_ONE get() = FontFamily("PoiretOne")


    @OptIn(ExperimentalReadiumApi::class)
    private val editor: EpubPreferencesEditor by lazy {
        val preferences = EpubPreferences(

            columnCount = ColumnCount.ONE,
            fontFamily = FontFamily.ACCESSIBLE_DFA,
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

                readerViewModel.setPublication(publication!!)


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

                        addFontFamilyDeclaration(FontFamily.LEXEND) {
                            addFontFace {
                                addSource("font/lexend_regular.ttf", preload = true)
                                setFontStyle(FontStyle.NORMAL)
                                setFontWeight(FontWeight.NORMAL)
                            }

                        }

                        addFontFamilyDeclaration(FontFamily.MONTSERRAT) {
                            addFontFace {
                                addSource("font/montserrat_regular.ttf", preload = true)
                                setFontStyle(FontStyle.NORMAL)
                                setFontWeight(FontWeight.NORMAL)
                            }

                        }

                        addFontFamilyDeclaration(FontFamily.POIRET_ONE) {
                            addFontFace {
                                addSource("font/poiret_one_regular.ttf", preload = true)
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
        setupObservers()

        readingStartTime = elapsedRealtime()

        setupHighlights()
        setupPreferences()
    }

    override fun onPause() {
        super.onPause()

        val elapsedMillis = elapsedRealtime() - readingStartTime!!
        val minutesRead = (elapsedMillis / 1000 / 60).toInt()

        val currentValue = prefs.getInt("MINUTES", minutesRead)

        prefs.edit { putInt("MINUTES", currentValue + minutesRead) }

        saveReadingProgress()
    }

    private fun setupHighlights() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getAllHighlights(bookId!!).collectLatest { highlightList ->


                val decorations = highlightList.map { highlight ->

                    val style = if (highlight.style == Highlight.Style.HIGHLIGHT) {
                        Decoration.Style.Highlight(highlight.tint)
                    } else {
                        Decoration.Style.Underline(highlight.tint)   // underline fallback
                    }

                    Decoration(
                        id = highlight.id.toString(),
                        locator = highlight.locator,
                        style = style
                    )
                }

                (navigator as DecorableNavigator).apply {
                    applyDecorations(decorations, "user-highlights")
                    addDecorationListener("user-highlights", decorableListener)
                }

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
            showOptionsFragment()
        }

        binding.cardViewPageNumberContainer.setOnClickListener {
            val pageNumberBottomSheetFragment = PageNumberBottomSheetFragment()
            pageNumberBottomSheetFragment.show(parentFragmentManager, "PageNumberBottomSheet")

        }

        binding.imageButtonBookmark.setOnClickListener {
            addToBookmark()
        }

    }

    fun addToBookmark() {

        viewLifecycleOwner.lifecycleScope.launch {
            val locator = navigator.currentLocator.value
            viewModel.addBookmark(bookId!!, locator)
        }
    }

    private fun showOptionsFragment() {

        val fragment = OptionsFragment().apply {
            arguments = bundleOf("bookId" to bookId)
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_options, fragment)
            .commitNow()
    }


    override fun onDestroyView() {
        super.onDestroyView()

        (navigator as DecorableNavigator).removeDecorationListener(decorableListener)
    }

    private fun setupPreferences() {

        editor.apply {

            if (prefs.getString(FONT_FAMILY, null) != null) {
                fontFamily.set(FontFamily(prefs.getString(FONT_FAMILY, null)!!))
            }

            fontSize.set(prefs.getFloat(FONT_SIZE, 1.0f).toDouble())
            scroll.set(
                prefs.getBoolean(
                    SCROLL,
                    false
                )
            )
            lineHeight.set(prefs.getFloat(LINE_HEIGHT, 1.0f).toDouble())

            if (prefs.getString(TEXT_COLOR, null) != null) {

                textColor.set(
                    org.readium.r2.navigator.preferences.Color(
                        prefs.getString(
                            TEXT_COLOR,
                            null
                        )!!.toColorInt()
                    )
                )
                backgroundColor.set(
                    org.readium.r2.navigator.preferences.Color(
                        prefs.getString(
                            BACKGROUND_COLOR,
                            null
                        )!!.toColorInt()
                    )
                )
            }

            if (prefs.getBoolean("JUSTIFY_CONTENT", false)) {
                textAlign.set(TextAlign.START)
            }
        }

        navigator.submitPreferences(editor.preferences)
    }

    fun setupObservers() {
        readerViewModel.selectedFontFamily.observe(viewLifecycleOwner) { font ->
            if (!font.isNullOrEmpty()) {
                editor.apply {
                    this.fontFamily.set(FontFamily(font))
                }
            }

            navigator.submitPreferences(editor.preferences)

        }

        readerViewModel.bookmark.observe(viewLifecycleOwner) { bookmark ->
            navigator.go(bookmark.locator)
        }

        readerViewModel.page.observe(viewLifecycleOwner) { page ->

            if (page > 0) {
                lifecycleScope.launch {
                    if (page <= publication!!.positions().size)
                        navigator.go(publication!!.positions()[page - 1])
                }


            }

        }

        readerViewModel.link.observe(viewLifecycleOwner) { link ->
            if (link != null) {
                navigator.go(link, true)
            }
        }

        readerViewModel.locator.observe(viewLifecycleOwner) { locator ->
            if (locator != null)
                navigator.go(locator, true)

        }

        readerViewModel.selectedHighlight.observe(viewLifecycleOwner) { highlight ->
            if (highlight != null) {
                navigator.go(highlight.locator, true)
            }
        }


        (navigator as VisualNavigator).apply {
            addInputListener(object : InputListener {
                override fun onTap(event: TapEvent): Boolean {
                    showButtons()
                    return true
                }
            })
        }

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

        readerViewModel.selectedTheme.observe(viewLifecycleOwner) { theme ->

            if (theme != null) {
                editor.apply {
                    this.textColor.set(org.readium.r2.navigator.preferences.Color(theme.textColor.toColorInt()))
                    this.backgroundColor.set(org.readium.r2.navigator.preferences.Color(theme.backgroundColor.toColorInt()))
                    this.fontFamily.set(FontFamily(theme.fontFamily))

                }

                prefs.edit {
                    putString(TEXT_COLOR, theme.textColor)
                    putString(BACKGROUND_COLOR, theme.backgroundColor)
                    putString(FONT_FAMILY, theme.fontFamily)
                }
                navigator.submitPreferences(editor.preferences)
            }
        }

        readerViewModel.selectedFontSize.observe(viewLifecycleOwner) { size ->
            editor.apply {
                this.fontSize.set(size)
            }
            prefs.edit { putFloat(FONT_SIZE, size.toFloat()) }
            navigator.submitPreferences(editor.preferences)
        }

        readerViewModel.selectedLineSpacing.observe(viewLifecycleOwner) { spacing ->

            editor.apply {
                this.lineHeight.set(spacing)
            }
            prefs.edit { putFloat(LINE_HEIGHT, spacing.toFloat()) }
            navigator.submitPreferences(editor.preferences)

        }
    }


    suspend fun saveReadingProgression(bookId: Long) {

        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            navigator.currentLocator
                .onEach { viewModel.saveReadingProgression(it, bookId) }
                .launchIn(this)
        }
    }


    private fun showButtons() {

        binding.apply {

            if ((imageViewCancelButton.isVisible && imageViewOptions.isVisible) && (cardViewPageNumberContainer.isVisible && imageButtonBookmark.isVisible)) {
                imageViewCancelButton.visibility = View.INVISIBLE
                imageViewOptions.visibility = View.INVISIBLE
                cardViewPageNumberContainer.visibility = View.INVISIBLE
                imageButtonBookmark.visibility = View.INVISIBLE

            } else {
                imageViewCancelButton.visibility = View.VISIBLE
                imageViewOptions.visibility = View.VISIBLE
                cardViewPageNumberContainer.visibility = View.VISIBLE
                imageButtonBookmark.visibility = View.VISIBLE
            }
        }
    }

    private var popupWindow: PopupWindow? = null
    private var mode: ActionMode? = null


    private val highlightTints = mapOf(
        R.id.red to Color.rgb(247, 124, 124),
        R.id.green to Color.rgb(173, 247, 123),
        R.id.blue to Color.rgb(124, 198, 247),
        R.id.yellow to Color.rgb(249, 239, 125),
        R.id.purple to Color.rgb(182, 153, 255)
    )

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
                R.id.highlight -> showHighlightPopupWithStyle(Highlight.Style.HIGHLIGHT)
                R.id.underline -> showHighlightPopupWithStyle(Highlight.Style.UNDERLINE)
                R.id.copy -> lifecycleScope.launch { copy() }
                R.id.dictionary -> lifecycleScope.launch { dictionary() }

            }
            mode.finish()

            return true
        }
    }

    private fun selectHighlightTint(
        highlightId: Long? = null,
        style: Highlight.Style,
        @ColorInt tint: Int,
    ) =
        viewLifecycleOwner.lifecycleScope.launch {

            if (highlightId != null) {
                viewModel.updateHighlight(highlightId, tint)
            } else {

                (navigator as? SelectableNavigator)?.let { navigator ->
                    navigator.currentSelection()?.let { selection ->
                        viewModel.addHighlight(
                            bookID = bookId!!,
                            locator = selection.locator,
                            style = style,
                            tint = tint
                        )
                    }
                    navigator.clearSelection()
                }

            }

            popupWindow?.dismiss()
            mode?.finish()

        }


    suspend fun dictionary() {

        val selectedWord = (navigator as? SelectableNavigator).let { navigator ->
            navigator?.currentSelection()?.locator?.text?.highlight
        } ?: ""


        val onlineLookup = showDictionaryIfOnline(selectedWord)

        if (onlineLookup != null)
            onlineLookup.show(parentFragmentManager, "DictionaryBottomSheet")
        else {
            showDictionaryIfOffline(selectedWord).show(
                parentFragmentManager,
                "DictionaryBottomSheet"
            )
        }

        (navigator as? SelectableNavigator)?.clearSelection()

    }

    private suspend fun showDictionaryIfOnline(selectedWord: String): DictionaryBottomSheet? {

        val api = DictionaryApi()
        val result = api.lookup(selectedWord)

        if (result.isSuccess) {

            val entries = result.getOrNull()!!
            val entry = entries[0]


            val definitions = arrayListOf<String>()
            val pos = arrayListOf<String>()
            val usages = arrayListOf<String>()

            var audioUrl: String? = null
            entries.forEach { entry ->
                entry.phonetics.forEach { phonetic ->
                    if (!phonetic.audio.isNullOrEmpty()) {
                        audioUrl = phonetic.audio
                        return@forEach
                    }
                }
            }


            entry.meanings.forEach { meaning ->

                pos.add(meaning.partOfSpeech)

                meaning.definitions.forEach { def ->

                    definitions.add(def.definition)

                    def.example?.let { usages.add(it) }
                }
            }


            return DictionaryBottomSheet.Companion.newInstance(
                selectedWord,
                definition = definitions,
                pos = pos[0],
                usages = usages,
                audioUrl
            )
        }

        return null
    }


    private suspend fun showDictionaryIfOffline(selectedWord: String): DictionaryBottomSheet {
        val db = DictionaryProvider.getInstance(requireContext())
        val definition = db.dictionaryDao().getDefinitions(selectedWord)

        val pos = db.dictionaryDao().getPos(selectedWord)

        val usages = db.dictionaryDao().getUsageExamples(selectedWord)

        val dialog =
            DictionaryBottomSheet.Companion.newInstance(
                selectedWord,
                definition,
                pos = pos,
                usages = usages,
                null
            )
        return dialog
    }

    private suspend fun copy() {
        val clipboard =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        val selectedText =
            (navigator as SelectableNavigator).currentSelection()?.locator?.text?.highlight

        clipboard.setPrimaryClip(ClipData.newPlainText("Selected text", selectedText))
        (navigator as SelectableNavigator).clearSelection()

    }

    private fun showHighlightPopupWithStyle(style: Highlight.Style) {

        viewLifecycleOwner.lifecycleScope.launch {
            (navigator as SelectableNavigator).currentSelection()?.rect?.let { rectF ->
                showHighlightPopUp(rectF, style)
            }
        }
    }

    fun showHighlightPopUp(rectF: RectF, style: Highlight.Style, highlightId: Long? = null) {

        viewLifecycleOwner.lifecycleScope.launch {

            if (popupWindow?.isShowing == true) return@launch

            val popupView = layoutInflater.inflate(
                R.layout.color_action_mode,
                null,
                false
            )

            popupView.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )


            popupWindow = PopupWindow(
                popupView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                isFocusable = true
            }

            val x = rectF.left
            val y = rectF.top

            popupWindow?.showAtLocation(popupView, Gravity.NO_GRAVITY, x.toInt(), y.toInt() - 150)

            fun selectTint(view: View) {
                val tint = highlightTints[view.id] ?: return
                selectHighlightTint(highlightId, style, tint)
            }

            popupView.findViewById<View>(R.id.red).setOnClickListener(::selectTint)
            popupView.findViewById<View>(R.id.green).setOnClickListener(::selectTint)
            popupView.findViewById<View>(R.id.blue).setOnClickListener(::selectTint)
            popupView.findViewById<View>(R.id.yellow).setOnClickListener(::selectTint)
            popupView.findViewById<View>(R.id.purple).setOnClickListener(::selectTint)
            popupView.findViewById<View>(R.id.del).apply {
                visibility = if (highlightId != null) View.VISIBLE else View.GONE
                setOnClickListener {
                    if (highlightId != null) {
                        viewLifecycleOwner.lifecycleScope.launch {
                            viewModel.deleteHighlightById(highlightId)
                            popupWindow?.dismiss()
                            mode?.finish()
                        }
                    }
                }
            }
        }
    }

    inner class DecorableListener : DecorableNavigator.Listener {
        override fun onDecorationActivated(event: DecorableNavigator.OnActivatedEvent): Boolean {
            val decoration = event.decoration

            val id = decoration.id.toLong()

            event.rect?.let { rectF ->
                showHighlightPopUp(rectF, Highlight.Style.HIGHLIGHT, id)

            }

            return true
        }

    }

    companion object {
        const val FONT_FAMILY = "FONT_FAMILY"
        const val FONT_SIZE = "FONT_SIZE"
        const val LINE_HEIGHT = "LINE_HEIGHT"
        const val SCROLL = "SCROLL"
        const val TEXT_COLOR = "TEXT_COLOR"
        const val BACKGROUND_COLOR = "BACKGROUND_COLOR"

    }
}
