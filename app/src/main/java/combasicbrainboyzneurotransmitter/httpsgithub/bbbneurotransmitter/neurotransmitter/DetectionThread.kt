package combasicbrainboyzneurotransmitter.httpsgithub.bbbneurotransmitter.neurotransmitter

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.annotation.FloatRange
import org.jtransforms.fft.FloatFFT_1D
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Created by root on 24/02/18.
 */

class DetectionHandler {
    private var mHandler: Handler? = null
    private var mNTHandler: Handler? = null
    private var mDetectionState: DetectionState = DetectionState.BASELINE
    private lateinit var mStimuliFreqs: FloatArray
    private lateinit var mFreqIndexes: IntArray
    private lateinit var mFreqCoefficients: FloatArray
    private var mSampleRate: Float = 0.0f
    private var mInputSize: Int = 0
    private var mFFTOutputPoints: Int = 0
    private var mBaselineAverageCount: Int = 0
    private var mMessageId = 0
    private var mDetectionsId = 0
    private var mFFT_Calculator: FloatFFT_1D? = null


    companion object {
        private const val INFO_MESSAGE = 0
        private const val SSVEP_DATA: Int = 1
        private const val SSVEP_DETECTIONS: Int = 2
        private const val SSVEP_BASELINES: Int = 3
        private const val DETECTIONS_MESSAGE: Int = 4
    }

    fun generateHandler(looper: Looper){
        mHandler = object: Handler(looper){
            override fun handleMessage(msg: Message?) {
            // take data and perform FFT
                when (msg?.what) {
                    SSVEP_DATA -> {
                        val message = "Received Data for FFT" + " " + mDetectionsId.toString()
                        mDetectionsId += 1
                        val infoMessage: Message = Message.obtain(mNTHandler, DETECTIONS_MESSAGE, message)
                        infoMessage.sendToTarget()
                        analyzeSample(msg.obj as FloatArray)
                    }
                }
            }
        }
    }

    fun setNTHandler(ntHandler: Handler){
        mNTHandler = ntHandler
    }

    fun getHandler(): Handler?{
        return mHandler
    }

    fun initializeAlgorithm(targetFreqs: FloatArray, sampleRate: Float, inputSize: Int, fftOutputPoints: Int){
        mStimuliFreqs = targetFreqs
        mSampleRate = sampleRate
        mInputSize = inputSize
        mFFTOutputPoints = fftOutputPoints

        // Make sure parameters are sufficient sizes
        if(mInputSize > mFFTOutputPoints){
            mFFTOutputPoints = mInputSize
        }

        var tempNumPoints = mFFTOutputPoints
        while(((tempNumPoints != 2) && tempNumPoints % 2 == 0) || tempNumPoints == 1) {
            tempNumPoints /= 2
        }

        require(tempNumPoints != 1,{"FFT Length must be a power of 2"})

        // Figure out which FFT index the target frequencies will be at
        val maxFreq = mSampleRate/2.0f


        // the second harmonic of all targets must be less than maxFreq
        require(mStimuliFreqs.all( {freq -> 2*freq < maxFreq}), {"The second harmonic of all targets must be less than half the sample rate"})

        mFreqIndexes = IntArray(targetFreqs.size, {i -> (targetFreqs[i]/maxFreq*mFFTOutputPoints).toInt()+1})
        mFreqCoefficients = FloatArray(targetFreqs.size, {i -> 0.0f})

        mFFT_Calculator = FloatFFT_1D(mFFTOutputPoints.toLong())
    }

    private fun analyzeSample(data: FloatArray){
        require(data.size == this.mInputSize, {"Data size does not match the required size"})
        val dataMean = data.average().toFloat()
        //Padding zeros and converting to complex
        var dataPad: FloatArray = FloatArray(this.mFFTOutputPoints, {i -> if (i < data.size) data[i] - dataMean else 0.0f})

        //Taking the FFt
        mFFT_Calculator?.realForward(dataPad)
        val hMag = FloatArray(dataPad.size/2, {i->  sqrt(dataPad[2*i].pow(2.0f) + dataPad[2*i+1].pow(2.0f)) })

        //measuring targetFreq strengths
        val freqStrength: FloatArray
        freqStrength = FloatArray(this.mStimuliFreqs.size, { i -> hMag[this.mFreqIndexes[i]] + hMag[2*this.mFreqIndexes[i]]})

        val fftMean: Float
        fftMean = hMag.average().toFloat()

        //what to do with FFT result
        if(this.mDetectionState == DetectionState.BASELINE){
            calculateCoefficients(freqStrength, fftMean)
            val baselineRuns: Int = mBaselineAverageCount
            val baselineMessage = mNTHandler?.obtainMessage(SSVEP_BASELINES, baselineRuns)
            mNTHandler?.sendMessage(baselineMessage)
        } else {
            val detections: BooleanArray = findFrequencies(freqStrength, fftMean)
            val currentDetections = Detections(detections, mMessageId)
            mMessageId += 1
            val detectionsMessage = mNTHandler?.obtainMessage(SSVEP_DETECTIONS, currentDetections)
            mNTHandler?.sendMessage(detectionsMessage)
        }
    }

     private fun calculateCoefficients(strengths: FloatArray, average: Float){
        for ( i in this.mStimuliFreqs.indices){
            //running average of CF
            this.mFreqCoefficients[i] = (this.mFreqCoefficients[i]*this.mBaselineAverageCount + strengths[i]/average)/(this.mBaselineAverageCount+1)

        }
         this.mBaselineAverageCount = this.mBaselineAverageCount + 1
    }

    private fun findFrequencies(strengths: FloatArray, average: Float): BooleanArray {
        var bestScore = 0.0f
        var bestIndex = 1
        var score: Float

        var result = BooleanArray(this.mFreqCoefficients.size, { i -> false})

        for (i in strengths.indices){
            score = strengths[i] - this.mFreqCoefficients[i]*average
            if (score > bestScore) {
                bestScore = score
                bestIndex = i
            }
        }

        if (bestScore < strengths.average()) {
            return result //all false
        } else {
            result[bestIndex] = true
            return result
        }
    }

    fun setDetectionState(state: DetectionState){
        mDetectionState = state
    }

    fun setNewBaseline(){
        this.mDetectionState = DetectionState.BASELINE
        this.mBaselineAverageCount = 0
        for (i in this.mFreqCoefficients.indices){
            this.mFreqCoefficients[i] = 0.0f
        }
    }
}

enum class DetectionState {
    BASELINE, CLASSIFY
}

data class Detections(val detects: BooleanArray, val messageId: Int)





