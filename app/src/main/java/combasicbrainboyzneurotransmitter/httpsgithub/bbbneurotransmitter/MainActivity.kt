package combasicbrainboyzneurotransmitter.httpsgithub.bbbneurotransmitter

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.os.*
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.widget.ImageView
import android.widget.TextView
import combasicbrainboyzneurotransmitter.httpsgithub.bbbneurotransmitter.neurotransmitter.NeurotransmitterHandler

class MainActivity : AppCompatActivity() {

    companion object {
        private const val INFO_MESSAGE = 0
        //private const val SSVEP_DATA = 1
        private const val SSVEP_DETECTIONS: Int = 2
        //private const val SSVEP_BASELINES: Int = 3

        private val stimuliFreqs: FloatArray = floatArrayOf(12.0f, 20.0f, 30.0f)
        private val samplingFreq: Float = 250.0f
        private val inputSize: Int = 512
        private val fftOutputPoints: Int = 1024
    }

    private var mHandler: Handler? = null

    private var mLeftStimuli: ImageView? = null
    private var mMiddleStimuli: ImageView? = null
    private var mRightStimuli: ImageView? = null
    private var mTrackText: ImageView? = null
    private var mInfoText: TextView? = null
    private var mConnectionStart: ImageView? = null

    private var mAudioPlay: ImageView? = null
    private var mAudioSkipFwd: ImageView? = null
    private var mAudioSkipBack: ImageView? = null

    private var mAudioManager: AudioManager? = null
    private var mAudioPlaying: Boolean = false

    private var mPlayIndicator: ImageView? = null
    //private var mStimuliView: StimuliView? = null
    //private var mTimer: Timer? = null

    private var mNTHandlerThread: HandlerThread? = null
    private var mNTHandler: NeurotransmitterHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.decorView.setBackgroundColor(Color.BLACK)

        mAudioManager = applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        mInfoText = findViewById(R.id.info_text)

        logMessage("Configuring graphics...")
        retrieveStimuli()
        retrieveAudioControls()

        logMessage("Configuring Neurotransmitter...")
        initializeHandler()
        initializeNeurotransmitter()

        mNTHandler?.configureAlgorithm(stimuliFreqs, samplingFreq, inputSize, fftOutputPoints)

        configureStartButton()
        logMessage("Ready to start operating....")

    }

    fun stimuliOnePresent(){
        var eventTime = (SystemClock.uptimeMillis() - 1)
        val audioSkipBackDown: KeyEvent? = KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PREVIOUS, 0)
        mAudioManager?.dispatchMediaKeyEvent(audioSkipBackDown)
        eventTime = (SystemClock.uptimeMillis() - 1)
        val audioSkipBackUp: KeyEvent? = KeyEvent(eventTime, eventTime, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PREVIOUS, 0)
        mAudioManager?.dispatchMediaKeyEvent(audioSkipBackUp)
    }

    fun stimuliTwoPresent(){
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
    }

    fun stimuliThreePresent(){
        var eventTime = (SystemClock.uptimeMillis() - 1)
        val audioSkipBackDown: KeyEvent? = KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_NEXT, 0)
        mAudioManager?.dispatchMediaKeyEvent(audioSkipBackDown)
        eventTime = (SystemClock.uptimeMillis() - 1)
        val audioSkipBackUp: KeyEvent? = KeyEvent(eventTime, eventTime, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_NEXT, 0)
        mAudioManager?.dispatchMediaKeyEvent(audioSkipBackUp)
    }

    fun logMessage(message: String){
        mInfoText?.setText(message.toCharArray(), 0, message.length)
    }

    private fun retrieveStimuli(){
        mLeftStimuli = findViewById(R.id.stimuli_left)
        mMiddleStimuli = findViewById(R.id.stimuli_middle)
        mRightStimuli = findViewById(R.id.stimuli_right)

        mLeftStimuli?.setOnClickListener({
            val left: Drawable? = mLeftStimuli?.drawable

            if(left is Animatable){
                left.start()
            }
        })

        mMiddleStimuli?.setOnClickListener({
            val middle: Drawable? = mMiddleStimuli?.drawable

            if(middle is Animatable){
                middle.start()
            }
        })

        mRightStimuli?.setOnClickListener({
            val right: Drawable? = mRightStimuli?.drawable

            if(right is Animatable){
                right.start()
            }
        })
    }

    private fun retrieveAudioControls(){
        mAudioPlay = findViewById(R.id.play)
        mAudioSkipFwd = findViewById(R.id.skip_fwd)
        mAudioSkipBack = findViewById(R.id.skip_back)

        mAudioPlay?.setOnClickListener({
            stimuliTwoPresent()
        })

        mAudioSkipFwd?.setOnClickListener({
            stimuliThreePresent()
        })

        mAudioSkipBack?.setOnClickListener({
            stimuliOnePresent()
        })
    }

    private fun initializeHandler(){
        mHandler = object: Handler(this.mainLooper){
            override fun handleMessage(msg: Message?) {
                when(msg?.what){
                    INFO_MESSAGE -> {
                        val message: String = msg.obj as String
                        logMessage(message)
                    }
                    SSVEP_DETECTIONS -> {
                        val detections: BooleanArray = msg.obj as BooleanArray
                        when {
                            detections[0] -> stimuliOnePresent()
                            detections[1] -> stimuliTwoPresent()
                            detections[2] -> stimuliThreePresent()
                        }
                    }
                }
            }
        }
    }

    private fun initializeNeurotransmitter(){
        mNTHandlerThread = HandlerThread("Neurotransmitter", -10)
        mNTHandlerThread?.start()
        mNTHandler = NeurotransmitterHandler()
        mNTHandler?.setMainHandler(mHandler!!)
        mNTHandler?.generateHandler(mNTHandlerThread?.looper as Looper)
        mNTHandler?.prepareComponents()

    }

    private fun configureStartButton(){
        mConnectionStart = findViewById(R.id.make_connection)

        mConnectionStart?.setOnClickListener({
            logMessage("Waiting for Connection...")
            mNTHandler?.establishBluetoothConnection()
            logMessage("Connection Established")
            mNTHandler?.startSSVEPDetection()
            logMessage("Running Algorithm")
        })
    }
}

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
