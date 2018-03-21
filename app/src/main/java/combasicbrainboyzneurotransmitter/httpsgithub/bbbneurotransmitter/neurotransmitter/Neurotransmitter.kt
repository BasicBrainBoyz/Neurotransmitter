package combasicbrainboyzneurotransmitter.httpsgithub.bbbneurotransmitter.neurotransmitter

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message

/**
 * Created by root on 24/02/18.
 */

// Need to generate handler thread on UIThread that gives looper to below

class NeurotransmitterHandler {
    private var mHandler: Handler? = null
    private var mMainHandler: Handler? = null
    private var mBluetoothThread: BluetoothThread? = null // need bluetooth device
    private var mDetectionThread = HandlerThread("detection", -10)
    private var mDetectionHandler: DetectionHandler = DetectionHandler()
    private var mRequestedBaselineRuns: Int = 10

    companion object {
        private const val INFO_MESSAGE = 0
        private const val SSVEP_DATA = 1
        private const val SSVEP_DETECTIONS: Int = 2
        private const val SSVEP_BASELINES: Int = 3
        private const val DETECTIONS_MESSAGE: Int = 4
    }

    fun generateHandler(looper: Looper){
        mHandler = object: Handler(looper){
            override fun handleMessage(msg: Message?) {
                when(msg?.what){
                    INFO_MESSAGE -> {
                        val messageObject = msg.obj as String
                        val message = Message.obtain(mMainHandler, INFO_MESSAGE, messageObject)
                        mMainHandler?.sendMessage(message)
                    }
                    SSVEP_DATA -> {}
                    SSVEP_DETECTIONS -> {
                        val messageObject = msg.obj as Detections
                        val message = Message.obtain(mMainHandler, SSVEP_DETECTIONS, messageObject)
                        mMainHandler?.sendMessage(message)
                    }
                    SSVEP_BASELINES -> {
                        if(mRequestedBaselineRuns == 0){
                            mDetectionHandler.setDetectionState(DetectionState.CLASSIFY)
                        }
                        else{
                            mRequestedBaselineRuns -= 1
                        }
                    }
                    DETECTIONS_MESSAGE -> {
                        val messageObject = msg.obj as String
                        val message = Message.obtain(mMainHandler, DETECTIONS_MESSAGE, messageObject)
                        mMainHandler?.sendMessage(message)
                    }
                }
            }
        }
    }

    fun setMainHandler(mainHandler: Handler){
        mMainHandler = mainHandler
    }

    fun prepareComponents(){
        mDetectionThread.start()
        mDetectionHandler.setNTHandler(mHandler as Handler)
        mDetectionHandler.generateHandler(mDetectionThread.looper)
        mBluetoothThread = BluetoothThread(mHandler, mDetectionHandler.getHandler())
    }

    fun configureAlgorithm(targetFreqs: FloatArray, sampleRate: Float, inputSize: Int, fftOutputPoints: Int) {
        mDetectionHandler.initializeAlgorithm(targetFreqs, sampleRate, inputSize, fftOutputPoints)
    }

    fun establishBluetoothConnection(){

        mBluetoothThread?.establishESPConnection()
        mBluetoothThread?.setCollecting(true)

    }

    fun startSSVEPDetection(){
        mBluetoothThread?.sendMessageToESP("Ready")
        mBluetoothThread?.start()
      //  mBluetoothThread?.run()
    }

    // Can probably let main start right away after calling configure

    fun configure(mainHandler: Handler /* TODO WHAT ELSE*/){
        // TODO Implement configure function
    }

    // Allow the owner of the Neurotransmitter change its config
    fun changeState(){}

    fun holdOperation(){}

    fun continueOperation(){}

    fun reset(){}

}