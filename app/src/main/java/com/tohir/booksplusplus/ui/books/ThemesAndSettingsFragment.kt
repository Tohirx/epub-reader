package com.tohir.booksplusplus.ui.books

import android.content.Context
import android.os.Bundle
import android.support.v4.os.IResultReceiver
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.tohir.booksplusplus.R
import com.tohir.booksplusplus.databinding.ThemesAndSettingsLayoutBinding

class ThemesAndSettingsFragment : BottomSheetDialogFragment() {

    private lateinit var binding: ThemesAndSettingsLayoutBinding
    private val shareViewModel: SharedThemesViewModel by activityViewModels()
    private val userPreferences by lazy {
        requireContext().getSharedPreferences(
            "user_pref",
            Context.MODE_PRIVATE
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = ThemesAndSettingsLayoutBinding.inflate(inflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        setInitialValuesForViews()


    }

    fun setupClickListeners() {
        binding.imageButtonCancel.setOnClickListener { dialog?.dismiss() }
        binding.cardViewCustomTheme1.setOnClickListener {

            shareViewModel.setTheme(
                Theme(
                    backgroundColor = "#FFFFFF",
                    textColor = "#000000",
                    fontFamily = "OpenDyslexic"
                )
            )

            binding.fontFamilyChipGroup.check(R.id.chip_font_open_dyslexic)
            userPreferences.edit {
                putString(EpubReaderFragment.FONT_FAMILY, "OpenDyslexic")
            }
        }

        binding.cardViewCustomTheme2.setOnClickListener {
            shareViewModel.setTheme(Theme("#000000", textColor = "#FFFFFF", fontFamily = "Roboto"))

            binding.fontFamilyChipGroup.check(R.id.chip_font_roboto)
            userPreferences.edit {
                putString(EpubReaderFragment.FONT_FAMILY, "Roboto")
            }
        }

        binding.cardViewCustomTheme3.setOnClickListener {
            shareViewModel.setTheme(
                Theme(
                    "#423b30",
                    textColor = "#FEEFCC",
                    fontFamily = "OpenSans"
                )
            )

            binding.fontFamilyChipGroup.check(R.id.chip_font_open_sans)
            userPreferences.edit {
                putString(EpubReaderFragment.FONT_FAMILY, "OpenDyslexic")
            }
        }

        binding.cardViewCustomTheme4.setOnClickListener {
            shareViewModel.setTheme(Theme("#504B38", textColor = "#FEEFCC", fontFamily = "Serif"))

            binding.fontFamilyChipGroup.check(R.id.chip_font_serif)
            userPreferences.edit {
                putString(EpubReaderFragment.FONT_FAMILY, "Serif")
            }
        }

        binding.cardViewCustomTheme5.setOnClickListener {
            shareViewModel.setTheme(Theme("#FAFAFA", textColor = "#2E2E2E", fontFamily = "Lexend"))

            binding.fontFamilyChipGroup.check(R.id.chip_font_lexend)
            userPreferences.edit {
                putString(EpubReaderFragment.FONT_FAMILY, "Lexend")
            }
        }

        binding.cardViewCustomTheme6.setOnClickListener {
            shareViewModel.setTheme(
                Theme(
                    "#121212",
                    textColor = "#E0E0E0",
                    fontFamily = "Montserrat"
                )
            )

            binding.fontFamilyChipGroup.check(R.id.chip_font_montserrat)
            userPreferences.edit {
                putString(EpubReaderFragment.FONT_FAMILY, "Montserrat")
            }
        }

        binding.cardViewCustomTheme7.setOnClickListener {
            shareViewModel.setTheme(
                Theme(
                    "#2C2C2C",
                    textColor = "#D6D6D6",
                    fontFamily = "PoiretOne"
                )
            )

            binding.fontFamilyChipGroup.check(R.id.chip_font_poiret_one)
            userPreferences.edit {
                putString(EpubReaderFragment.FONT_FAMILY, "PoiretOne")
            }
        }

        binding.cardViewCustomTheme8.setOnClickListener {
            shareViewModel.setTheme(Theme("#0D1117", textColor = "#C9D1D9", fontFamily = "Casual"))

            binding.fontFamilyChipGroup.check(R.id.chip_font_casual)
            userPreferences.edit {
                putString(EpubReaderFragment.FONT_FAMILY, "Casual")
            }
        }

        binding.fontFamilyChipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val chip = group.findViewById<Chip>(checkedIds[0])
                val selectedText = chip.text.toString()
                val formattedText = convertChipStringToStandardFormat(selectedText)

                userPreferences.edit {
                    putInt("CHIP_ID", checkedIds[0])
                    putString(EpubReaderFragment.FONT_FAMILY, formattedText)
                }


                shareViewModel.setFontFamily(formattedText)

            }
        }

        binding.sliderFontSize.addOnChangeListener { _, value, _  ->
            shareViewModel.setFontSize(value.toDouble())
        }

        binding.sliderLineSpacing.addOnChangeListener { _, value, _ ->
            shareViewModel.setLineSpacing(value.toDouble())
        }
    }

    fun setInitialValuesForViews() {

        val activeChip = userPreferences.getInt("CHIP_ID", -1)

        if (activeChip != -1)
            binding.fontFamilyChipGroup.check(activeChip)

        val fontSizeValue = userPreferences.getFloat(EpubReaderFragment.FONT_SIZE, -1.0f)

        if (fontSizeValue != -1.0f)
            binding.sliderFontSize.value = fontSizeValue

        val lineSpacingValue = userPreferences.getFloat(EpubReaderFragment.LINE_HEIGHT, -1.0f)

        if (lineSpacingValue != -1.0f)
            binding.sliderLineSpacing.value = lineSpacingValue


    }

    fun convertChipStringToStandardFormat(text: String): String {
        return when (text) {
            "Serif" -> "Serif"
            "Sans-serif" -> "sans-serif"
            "Open Dyslexic" -> "OpenDyslexic"
            "Roboto" -> "Roboto"
            "Lexend" -> "Lexend"
            "Montserrat" -> "Montserrat"
            "Poiret One" -> "PoiretOne"
            "Casual" -> "Casual"
            else -> "OpenSans"
        }
    }


}