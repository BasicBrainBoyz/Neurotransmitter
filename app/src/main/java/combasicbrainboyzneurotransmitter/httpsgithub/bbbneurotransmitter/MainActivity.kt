package combasicbrainboyzneurotransmitter.httpsgithub.bbbneurotransmitter

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Animatable
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.SystemClock
import android.provider.MediaStore
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.widget.ImageView
import combasicbrainboyzneurotransmitter.httpsgithub.bbbneurotransmitter.neurotransmitter.Neurotransmitter
import java.util.Timer
import java.util.TimerTask

import combasicbrainboyzneurotransmitter.httpsgithub.bbbneurotransmitter.views.StimuliView

class MainActivity : AppCompatActivity() {


    private var mLeftStimuli: ImageView? = null
    private var mMiddleStimuli: ImageView? = null
    private var mRightStimuli: ImageView? = null

    private var mAudioPlay: ImageView? = null
    private var mAudioSkipFwd: ImageView? = null
    private var mAudioSkipBack: ImageView? = null

    private var mAudioManager: AudioManager? = null
    private var mAudioPlaying: Boolean = false

    private var mPlayIndicator: ImageView? = null
    //private var mStimuliView: StimuliView? = null
    //private var mTimer: Timer? = null
    private var mHandler: NeurotransmitterHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.decorView.setBackgroundColor(Color.BLACK)

        mAudioManager = applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        mLeftStimuli = findViewById(R.id.stimuli_left)

        mLeftStimuli?.setOnClickListener({
            val d: Drawable? = mLeftStimuli?.drawable

            if(d is Animatable){
                d.start()
            }
        })

        mMiddleStimuli = findViewById(R.id.stimuli_middle)

        mMiddleStimuli?.setOnClickListener({
            val c: Drawable? = mMiddleStimuli?.drawable

            if(c is Animatable){
                c.start()
            }
        })

        mRightStimuli = findViewById(R.id.stimuli_right)

        mRightStimuli?.setOnClickListener({
            val e: Drawable? = mRightStimuli?.drawable

            if(e is Animatable){
                e.start()
            }
        })

        mAudioPlay = findViewById(R.id.play)

        mAudioPlay?.setOnClickListener({
            //val e: Drawable? = mAudioPlay?.drawable

            //if(e is Animatable){
            //    e.start()
            //}

            //if(mAudioPlaying)
            // KEYCODE_MEDIA_PLAY
            // KEYCODE_MEDIA_PAUSE
            // KEYCODE_MEDIA_NEXT
            // KEYCODE_MEDIA_PREVIOUS


            if(!mAudioPlaying) {
                var eventTime = (SystemClock.uptimeMillis() - 1)
                val audioPlayDown: KeyEvent? = KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY, 0)
                mAudioManager?.dispatchMediaKeyEvent(audioPlayDown)
                eventTime = (SystemClock.uptimeMillis() - 1)
                val audioPlayUp: KeyEvent? = KeyEvent(eventTime, eventTime, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY, 0)
                mAudioManager?.dispatchMediaKeyEvent(audioPlayUp)
                mAudioPlaying = true
            }
            else{
                var eventTime = (SystemClock.uptimeMillis() - 1)
                val audioPauseDown: KeyEvent? = KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PAUSE, 0)
                mAudioManager?.dispatchMediaKeyEvent(audioPauseDown)
                eventTime = (SystemClock.uptimeMillis() - 1)
                val audioPauseUp: KeyEvent? = KeyEvent(eventTime, eventTime, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PAUSE, 0)
                mAudioManager?.dispatchMediaKeyEvent(audioPauseUp)
                mAudioPlaying = false
            }
        })

        // SKIP FWD AND BACK LISTENERS

        mAudioSkipFwd = findViewById(R.id.skip_fwd)

        mAudioSkipFwd?.setOnClickListener({
            //val e: Drawable? = mAudioPlay?.drawable

            //if(e is Animatable){
            //    e.start()
            //
            var eventTime = (SystemClock.uptimeMillis() - 1)
            val audioSkipFwdDown: KeyEvent? = KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_NEXT, 0)
            mAudioManager?.dispatchMediaKeyEvent(audioSkipFwdDown)
            eventTime = (SystemClock.uptimeMillis() - 1)
            val audioSkipFwdUp: KeyEvent? = KeyEvent(eventTime, eventTime, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_NEXT, 0)
            mAudioManager?.dispatchMediaKeyEvent(audioSkipFwdUp)
        })

        mAudioSkipBack = findViewById(R.id.skip_back)

        mAudioSkipBack?.setOnClickListener({
            //val e: Drawable? = mAudioPlay?.drawable

            //if(e is Animatable){
            //    e.start()
            //
            var eventTime = (SystemClock.uptimeMillis() - 1)
            val audioSkipBackDown: KeyEvent? = KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PREVIOUS, 0)
            mAudioManager?.dispatchMediaKeyEvent(audioSkipBackDown)
            eventTime = (SystemClock.uptimeMillis() - 1)
            val audioSkipBackUp: KeyEvent? = KeyEvent(eventTime, eventTime, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PREVIOUS, 0)
            mAudioManager?.dispatchMediaKeyEvent(audioSkipBackUp)
        })
        //val mLeftBlink: AnimatedVectorDrawable? = mLeftStimuli?.drawable as AnimatedVectorDrawable
        //mLeftBlink?.start()


        //private val mLeftBlink: AnimatedVectorDrawable? = mLeftStimuli?.drawable as AnimatedVectorDrawable
        //supportActionBar?.hide()
        //supportRequestWindowFeature(Window.FEATURE_NO_TITLE)

        //mStimuliView = findViewById<StimuliView>(R.id.stimuliView)

        //mLeftBlink?.start()



        //mTimer = Timer()

        //val doubleDelay = 1.0 / 120.0 * 1000.0

        //val delay = doubleDelay.toInt()
        //val period = doubleDelay.toInt()
        //mTimer!!.scheduleAtFixedRate(object : TimerTask() {
        //    override fun run() {
        //        mStimuliView!!.stimuliFlashing()
        //    }
        //}, delay.toLong(), period.toLong())
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        // ignore orientation/keyboard change
        super.onConfigurationChanged(newConfig)
    }
}

class NeurotransmitterHandler: Handler() {
    private var mActivity: MainActivity? = null

    fun NeurotransmitterHandler(mainActivity: MainActivity) {
        mActivity = mainActivity
    }


    override fun handleMessage(msg: Message?) {
        when(msg?.what){
            1 -> {

            }
            2 -> {

            }
        }
    }
}

// tasks:
// generate 3 rectangles (freq1, freq2, freq3) on start
// create timer object w/ period of (1/60) seconds
// increment counter from 0 to 30 (then reset) based on timer
// on each timer reset, mod2, mod3, mod5 the counter, and toggle colour if any remainders are 0
// Add back a song, play/pause, forward a song buttons above blinking rectangles
