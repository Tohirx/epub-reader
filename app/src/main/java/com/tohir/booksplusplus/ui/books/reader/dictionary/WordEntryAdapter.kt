package com.tohir.booksplusplus.ui.books.reader.dictionary

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.recyclerview.widget.RecyclerView
import com.tohir.booksplusplus.data.database.dictionary.DictionaryModels
import com.tohir.booksplusplus.databinding.ItemWordEntryBinding
import java.io.File

class WordEntryAdapter : RecyclerView.Adapter<WordEntryAdapter.ViewHolder>() {
    private var wordEntries: List<DictionaryModels.WordEntry> = listOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val inflater = LayoutInflater.from(parent.context)

        return ViewHolder((ItemWordEntryBinding.inflate(inflater, parent, false)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(wordEntries[position])
    }

    fun setWordEntries(entries: List<DictionaryModels.WordEntry>) {
        this.wordEntries = entries
    }


    override fun getItemCount(): Int {
        return wordEntries.size
    }

    class ViewHolder(private val binding: ItemWordEntryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        object AudioPlayer {
            private var player: ExoPlayer? = null

            @OptIn(UnstableApi::class)
            fun getPlayer(context: Context): ExoPlayer {
                if (player == null) {

                    val cacheDir = File(context.cacheDir, "audio_cache")
                    val cacheSize = 10L * 1024 * 1024 // 10MB

                    val cache = SimpleCache(
                        cacheDir,
                        LeastRecentlyUsedCacheEvictor(cacheSize),
                        StandaloneDatabaseProvider(context)
                    )

                    val dataSourceFactory = CacheDataSource.Factory()
                        .setCache(cache)
                        .setUpstreamDataSourceFactory(
                            DefaultHttpDataSource.Factory()
                        )

                    player = ExoPlayer.Builder(context)
                        .setMediaSourceFactory(
                            DefaultMediaSourceFactory(dataSourceFactory)
                        )
                        .build()
                }
                return player!!
            }

        }

        fun bind(wordEntry: DictionaryModels.WordEntry) {


            val audioUrls: MutableList<String> = mutableListOf()
            val phoneticsTexts: MutableList<String> = mutableListOf()

            for (phonetic: DictionaryModels.Phonetic in wordEntry.phonetics) {
                if (!phonetic.audio.isNullOrBlank()) {
                    audioUrls.add(phonetic.audio)
                }

                if (!phonetic.text.isNullOrBlank())
                    phoneticsTexts.add(phonetic.text)

            }

            if (phoneticsTexts.isNotEmpty())
            {
                val phonetics = StringBuilder()
                for (s: String in phoneticsTexts) phonetics.append(s.removeSuffix("/").removePrefix("/") + ", ")
                binding.textViewPronunciationText.text = "| " + phonetics.removeSuffix(", ").toString() + " |"
            } else
                binding.textViewPronunciationText.visibility = View.GONE


            binding.textViewWord.text = wordEntry.word

            val adapter = MeaningsAdapter()
            adapter.setMeanings(wordEntry.meanings)
            adapter.setWord(wordEntry.word)

            binding.recyclerViewMeanings.adapter = adapter


            if (audioUrls.isNotEmpty()) {
                binding.buttonPlayPronunciation.visibility = View.VISIBLE

                binding.buttonPlayPronunciation.setOnClickListener {


                    val player = AudioPlayer.getPlayer(it.context)

                    // Prevent multiple clicks
                    if (player.isPlaying) return@setOnClickListener

                    val selectedAudioUrl = audioUrls.firstOrNull { audio ->
                        audio.endsWith("uk.mp3", ignoreCase = true)
                    } ?: audioUrls.first()

                    val mediaItem = MediaItem.fromUri(selectedAudioUrl)

                    player.setMediaItem(mediaItem)
                    player.prepare()
                    player.play()

                    binding.buttonPlayPronunciation.isEnabled = false

                    player.addListener(object : Player.Listener {
                        override fun onPlaybackStateChanged(state: Int) {
                            if (state == Player.STATE_ENDED) {
                                player.stop()
                                binding.buttonPlayPronunciation.isEnabled = true
                            }
                        }
                    })
                }
            } else {
                binding.buttonPlayPronunciation.visibility = View.GONE
            }

        }

    }

}