package combasicbrainboyzneurotransmitter.httpsgithub.bbbneurotransmitter.neurotransmitter

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.annotation.FloatRange

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

    companion object {
        private const val SSVEP_DATA: Int = 1
        private const val SSVEP_DETECTIONS: Int = 2
        private const val SSVEP_BASELINES: Int = 3
    }

    fun generateHandler(looper: Looper){
        mHandler = object: Handler(looper){
            override fun handleMessage(msg: Message?) {
            // take data and perform FFT
                when (msg?.what) {
                    SSVEP_DATA -> {
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

        mFreqIndexes = IntArray(targetFreqs.size, {i -> (targetFreqs[i]/maxFreq*mFFTOutputPoints).toInt()})
        mFreqCoefficients = FloatArray(targetFreqs.size, {i -> 0.0f})
    }

    private fun analyzeSample(data: FloatArray){
        require(data.size == this.mInputSize, {"Data size does not match the required size"})

        //Padding zeros and converting to complex
        val dataPad = FFT.prepData(data, this.mFFTOutputPoints)

        //Taking the FFt
        val hData = FFT.fft(dataPad)
        val hMag = FloatArray(hData.size, {i-> hData[i].mag})

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
            val detectionsMessage = mNTHandler?.obtainMessage(SSVEP_DETECTIONS, detections)
            mNTHandler?.sendMessage(detectionsMessage)
        }
    }

     private fun calculateCoefficients(strengths: FloatArray, average: Float){
        for ( i in this.mStimuliFreqs.indices){
            //running average of CF
            this.mFreqCoefficients[i] = (this.mFreqCoefficients[i]*this.mBaselineAverageCount + strengths[i]/average)/(this.mBaselineAverageCount+1)
            this.mBaselineAverageCount = this.mBaselineAverageCount + 1
        }
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






