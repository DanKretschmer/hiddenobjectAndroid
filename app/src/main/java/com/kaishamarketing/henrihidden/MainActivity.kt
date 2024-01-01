package com.kaishamarketing.henrihidden

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : ComponentActivity() {
    //private var currentLevel: Level? = null
    private var currentLevelNumber = 1
    private val allLevels: List<Level> = listOf(
        Level(1, R.drawable.screen1, listOf(

            ItemInfo(
                "white bird eating",
                R.drawable.item1,
                false,
                33f,
                18f,
                59f,
                26.5f)
        )),
        Level(2, R.drawable.screen2, listOf(
            ItemInfo(
                "Lion",
                R.drawable.item1,
                false,
                45f,
                30f,
                45f,
                30f),

            ItemInfo(
                "White bird",
                R.drawable.item2,
                false,
                10f,
                20f,
                45f,
                30f)
        ))
        // Add more levels as needed
    )
    private val objectsToFind = mutableListOf<ItemInfo>()
    private lateinit var backgroundMusicPlayer: MediaPlayer
    private val focusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
            // Handle audio focus loss
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)
        backgroundMusicPlayer = MediaPlayer.create(this, R.raw.music)
        backgroundMusicPlayer.isLooping = true // Loop the music
        backgroundMusicPlayer.setVolume(0.5f, 0.5f) // Set volume to half
      //  backgroundMusicPlayer.start()
        Log.d("ActivityLifecycle", "onCreate called")
        val letsGoButton = findViewById<Button>(R.id.startButton)
        letsGoButton.setOnClickListener {
            setContentView(R.layout.screen1)
            loadLevel(currentLevelNumber)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun loadLevel(levelNumber: Int) {
        val level = allLevels.firstOrNull { it.levelNumber == levelNumber }
        level?.let {
            // Set the background image for the level
            val backgroundImageView = findViewById<ImageView>(R.id.backgroundImageView)
            backgroundImageView.setImageResource(it.backgroundImageId)
            backgroundImageView.visibility = View.VISIBLE // or View.GONE

            backgroundImageView.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    // Directly call the function to show the indicator at the clicked position
                    val imageView = v as ImageView

                    val currentLevel = allLevels.firstOrNull { it.levelNumber == currentLevelNumber }
                    currentLevel?.let { level ->
                        val clickedItem = findItemAt(event.x, event.y, imageView, level.items)
                        clickedItem?.let { item ->
                            if (!item.found) {
                                // Item is found for the first time
                                item.found = true // Mark the item as found

                                // Perform actions for when an item is found
                                playSoundEffect()
                                showFoundIndicatorAtPosition(imageView, event)
                                Toast.makeText(this,"You found the ${item.name}!", Toast.LENGTH_LONG).apply {
                                    setGravity(Gravity.CENTER, 0, 0)
                                    show()
                                }                            }
                        }?: run {
                            // Actions when no item is found at the click location
                            playWrongSoundEffect() // Play the 'wrong' sound effect
                        }
                    }


                    // Calculate and log the click position as a percentage of the ImageView's dimensions
                    val xPercent = (event.x / v.width) * 100
                    val yPercent = (event.y / v.height) * 100
                    Log.d("ImageClick", "Image clicked at: ${xPercent}%, ${yPercent}%")

                    true // Indicate that the touch event was handled
                } else {
                    false // Don't handle other touch events
                }
            }


            // Clear existing items and load new ones
            objectsToFind.clear()
            objectsToFind.addAll(it.items)

            updateItemListUI()
        }
    }

    private fun playWrongSoundEffect() {
//        val mediaPlayer: MediaPlayer = MediaPlayer.create(this, R.raw.wrong)
//        mediaPlayer.setOnCompletionListener { mp -> mp.release() } // Release the MediaPlayer once the sound is played
//        mediaPlayer.start()
    }

    private fun findItemAt(x: Float, y: Float, imageView: ImageView, items: List<ItemInfo>): ItemInfo? {
        val imageViewWidth = imageView.width
        val imageViewHeight = imageView.height

        // Check if the touch is within the bounds of any item
        return items.firstOrNull { item ->
            val itemXStart = imageViewWidth * item.xStartPercent / 100
            val itemXEnd = imageViewWidth * item.xEndPercent / 100
            val itemYStart = imageViewHeight * item.yStartPercent / 100
            val itemYEnd = imageViewHeight * item.yEndPercent / 100

            x in itemXStart..itemXEnd && y in itemYStart..itemYEnd
        }
    }



    private fun showFoundIndicatorAtPosition(imageView: ImageView, event: MotionEvent) {
        val indicatorView = ImageView(this)
        indicatorView.setImageResource(R.drawable.circle) // Your check mark drawable

        val sizeInPixels = 400 // Size of the check mark image

        // Calculate the percentage position based on the ImageView's dimensions
        val xPercent = (event.x / imageView.width) * 100
        val yPercent = (event.y / imageView.height) * 100

        // Calculate the position within the ImageView
        val xPosition = imageView.width * xPercent / 100 - sizeInPixels / 2
        val yPosition = imageView.height * yPercent / 100 - sizeInPixels / 2

        val layoutParams = RelativeLayout.LayoutParams(sizeInPixels, sizeInPixels)
        layoutParams.leftMargin = xPosition.toInt()
        layoutParams.topMargin = yPosition.toInt()

        Log.d("IndicatorDebug", "Click Coordinates: X=${event.x}, Y=${event.y}")
        Log.d("IndicatorDebug", "Percentage Position: X%=$xPercent, Y%=$yPercent")
        Log.d("IndicatorDebug", "LayoutParams: LeftMargin=${layoutParams.leftMargin}, TopMargin=${layoutParams.topMargin}")

        val parentLayout = imageView.parent as RelativeLayout // Assuming the parent is RelativeLayout
        parentLayout.addView(indicatorView, layoutParams)
       // playSoundEffect()
    }




    private fun updateItemListUI() {
        for (item in objectsToFind) {
            val textView = TextView(this)
        }
    }



    private fun playSoundEffect() {
        Log.e("plausound", "playSoundEffect called and started")
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val result = audioManager.requestAudioFocus(focusChangeListener,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            val mediaPlayer = MediaPlayer.create(this, R.raw.ding)
            mediaPlayer?.apply {
                setVolume(1.0f, 1.0f) // Max volume for both channels
                setOnCompletionListener { release() }
                start()
                Log.e("MediaPlayerStarted", "Media player started")
            }
        }
    }





    override fun onDestroy() {
        super.onDestroy()
        if (backgroundMusicPlayer.isPlaying) {
            backgroundMusicPlayer.stop()
        }
        backgroundMusicPlayer.release()
    }


}

data class Level(
    val levelNumber: Int,
    val backgroundImageId: Int,
    val items: List<ItemInfo>
)

data class ItemInfo(
    val name: String,
    val imageResourceId: Int,
    var found: Boolean,
    val xStartPercent: Float, // Left X coordinate as a percentage of ImageView's width
    val yStartPercent: Float, // Top Y coordinate as a percentage of ImageView's height
    val xEndPercent: Float,   // Right X coordinate as a percentage of ImageView's width
    val yEndPercent: Float    // Bottom Y coordinate as a percentage of ImageView's height
)
