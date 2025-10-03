package com.tohir.booksandstuff.ui.books

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commitNow
import com.tohir.booksandstuff.R
import com.tohir.booksandstuff.databinding.FragmentReaderBinding
import com.tohir.booksandstuff.util.BooksAndStuffApplication
import org.readium.r2.navigator.epub.EpubNavigatorFactory
import org.readium.r2.navigator.epub.EpubNavigatorFragment

class EpubReaderFragment: Fragment() {

    lateinit var navigator: EpubNavigatorFragment
    private lateinit var binding: FragmentReaderBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        var navigatorFactory: EpubNavigatorFactory? = null

        if (BooksAndStuffApplication.currentPublication != null)
        {
            navigatorFactory = EpubNavigatorFactory(publication = BooksAndStuffApplication.currentPublication!!)

        }


        childFragmentManager.fragmentFactory = navigatorFactory?.createFragmentFactory(initialLocator = null)!!

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
                add(R.id.fragment_reader_container, EpubNavigatorFragment::class.java, Bundle(), tag)
            }
        }

        navigator = childFragmentManager.findFragmentByTag(tag) as EpubNavigatorFragment

        return binding.root
    }



}