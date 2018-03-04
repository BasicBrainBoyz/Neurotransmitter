package com.example.ssvepdetection

class SSVEPDetector(val targetFreqs: DoubleArray, SampRate: Double, private val WinSize: Int, private var fftNumPoints: Int) {

    var state = DetectorState.BASELINE
    private var freqIndex: Array<Int>
    private var FreqCF: DoubleArray
    private var BLSampCount = 0

    //checking window size and fft size values
    init  {
        if (WinSize > fftNumPoints) fftNumPoints = WinSize


        //Check if FFTNumpoints is power of 2, otherwise will not work
        var tempNumPoints = fftNumPoints
        while(((tempNumPoints != 2) && tempNumPoints % 2 == 0) || tempNumPoints == 1) {
            tempNumPoints = tempNumPoints /2
        }
        //If we reach one then it is indeed a power of 2! Wow!
        require(tempNumPoints != 1,{"FFT Length must be a power of 2"})
    }

    //Finding target frequencies indexes in the FFT output
    init {
        val maxFreq = SampRate/2.0

        // the second harmonic of all targets must be less than maxFreq
        require(targetFreqs.all( {freq -> 2*freq < maxFreq}), {"The second harmonic of all targets must be less than half the sample rate"})

        freqIndex = Array(targetFreqs.size, {i -> (targetFreqs[i]/maxFreq*fftNumPoints).toInt()})
        FreqCF = DoubleArray(targetFreqs.size, {i -> 0.0})

    }

    fun analyzeSample(data: DoubleArray): Double{
        require(data.size == this.WinSize, {"Data size does not match the required size"})

        //Padding zeros and converting to complex
        val dataPad = FFT.prepData(data, this.fftNumPoints)

        //Taking the FFt
        val Hdata = FFT.fft(dataPad)
        val Hmag = DoubleArray(Hdata.size, {i-> Hdata[i].mag})

        //measuring targetFreq strengths
        val freqStrength: DoubleArray
        freqStrength = DoubleArray(this.targetFreqs.size, { i -> Hmag[this.freqIndex[i]] + Hmag[2*this.freqIndex[i]]})

        val fftMean: Double
        fftMean = Hmag.average()

        //what to do with FFT result
        if (this.state == DetectorState.BASELINE) {
            calcCF(freqStrength, fftMean)
            return 0.0
        }  else  {
            return findFreq(freqStrength, fftMean)
        }
    }

    private fun calcCF(strengths: DoubleArray, average: Double){

        for ( i in this.targetFreqs.indices){
            //running average of CF
            this.FreqCF[i] = (this.FreqCF[i]*this.BLSampCount + strengths[i]/average)/(this.BLSampCount+1)
            this.BLSampCount = this.BLSampCount + 1
        }

    }
    private fun findFreq(strengths: DoubleArray, average: Double): Double {
        var bestScore = 0.0
        var bestIndex = 1
        var score: Double

        //find best score
        for (i in strengths.indices){
            score = strengths[i] - this.FreqCF[i]*average
            if (score > bestScore) {
                bestScore = score
                bestIndex = i
            }
        }

        //is best strength big enough?
        if (bestScore < strengths.average()) {
            return 0.0
        } else {
            return this.targetFreqs[bestIndex]
        }



    }

    fun setNewBL(){
        this.state = DetectorState.BASELINE
        this.BLSampCount = 0
        for (i in this.FreqCF.indices){
            this.FreqCF[i] = 0.0
        }
    }


}



enum class DetectorState {
    BASELINE, CLASSIFY
}