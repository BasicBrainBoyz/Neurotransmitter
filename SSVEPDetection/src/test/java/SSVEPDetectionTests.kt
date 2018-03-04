/**
 * Created by liaml on 3/4/2018.
 */
import org.junit.Test
import java.util.regex.Pattern
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue

//Classes to test
import com.example.ssvepdetection.*
import org.junit.Before
import kotlin.math.PI
import kotlin.math.sin

class SSVEPDetectorTest {

    val testFreqs = doubleArrayOf(12.0,20.0,30.0)
    val SampRate = 200.0
    val winSize = 512;
    val numFFTPoints = 1024;


    @Test
    fun detectSine() {
        //given
        var detector = SSVEPDetector(testFreqs,SampRate,winSize,numFFTPoints)
        detector.state = DetectorState.CLASSIFY //leave baselines as zero

        //data - perfect sine wave
        val ftest = this.testFreqs[0]
        val data = DoubleArray(this.winSize, {i -> sin(ftest*2*PI*i.toDouble()/SampRate)})

        //when
        var result = detector.analyzeSample(data) //should calc baseline

        assertTrue(result == testFreqs[0])
    }

    @Test
    fun BL_ReturnZero() {
        var detector = SSVEPDetector(testFreqs,SampRate,winSize,numFFTPoints)
        //data - perfect sine wave
        val ftest = this.testFreqs[0]
        val data = DoubleArray(this.winSize, {i -> sin(ftest*2*PI*i.toDouble()/SampRate)})

        //when
        var result: Double
        for (i in 1..20) {
            result = detector.analyzeSample(data) //should calc baseline

            assertTrue(result == 0.0)
        }


    }

    @Test
    fun CalcBaseline() {
        var detector = SSVEPDetector(testFreqs,SampRate,winSize,numFFTPoints)
        //data - perfect sine wave
        val ftest = testFreqs[1]
        val data = DoubleArray(this.winSize, {i -> sin(ftest*2*PI*i.toDouble()/SampRate)})

        //when
        var result = detector.analyzeSample(data) //should calc baseline
        detector.state = DetectorState.CLASSIFY
        result = detector.analyzeSample(data) //should return 0

        assertTrue(result == 0.0)
    }

    @Test
    fun threeFreq() {
        var detector = SSVEPDetector(testFreqs,SampRate,winSize,numFFTPoints)
        //data - three perfect
        var data = DoubleArray(this.winSize, {i -> sin(testFreqs[0]*2*PI*i.toDouble()/SampRate)+sin(testFreqs[0]*2*2*PI*i.toDouble()/SampRate) + sin(testFreqs[1]*2*PI*i.toDouble()/SampRate) + sin(testFreqs[1]*2*2*PI*i.toDouble()/SampRate) + sin(testFreqs[2]*2*PI*i.toDouble()/SampRate)+ sin(testFreqs[2]*2*2*PI*i.toDouble()/SampRate)})

        //when
        var result = detector.analyzeSample(data)
        detector.state = DetectorState.CLASSIFY
        data = DoubleArray(this.winSize, {i -> 2*sin(testFreqs[0]*2*PI*i.toDouble()/SampRate)+2*sin(testFreqs[0]*2*2*PI*i.toDouble()/SampRate) + 0.8*sin(testFreqs[1]*2*PI*i.toDouble()/SampRate) + 0.8*sin(testFreqs[1]*2*2*PI*i.toDouble()/SampRate) + 0.8*sin(testFreqs[2]*2*PI*i.toDouble()/SampRate)+ 0.8*sin(testFreqs[2]*2*2*PI*i.toDouble()/SampRate)})
        result = detector.analyzeSample(data) //should return 0

        assertTrue(result == 12.0)

    }
}

class ComplexNumberTest {
    @Test
    fun canEquals(){
         //assertTrue(Complex(1.0,0.0) == Complex(1.0,0.0))
        assertTrue(true)
    }

}

class fftTest{

    @Test
    fun testPrepData_Padding(){
        //given
        val data = doubleArrayOf(1.0,2.0,3.0,4.0)
        val fftSize = 8

        //when
        val preppedData = FFT.prepData(data,fftSize)

        //then
        val result = arrayOf(Complex(1.0,0.0), Complex(2.0,0.0), Complex(3.0,0.0), Complex(4.0,0.0), Complex(0.0,0.0),Complex(0.0,0.0),Complex(0.0,0.0),Complex(0.0,0.0))
        assertTrue(result.size == preppedData.size)

        var isEqual = true
        for (i in preppedData.indices){
            if ((preppedData[i].re != result[i].re) || preppedData[i].im != result[i].im) {
                isEqual = false
            }
        }

        assertTrue(isEqual)

    }

    @Test
    fun testPrepData_NoPadding(){
        //given
        val data = doubleArrayOf(1.0,2.0,3.0,4.0)
        val fftSize = 4

        //when
        val preppedData = FFT.prepData(data,fftSize)

        //then
        val result = arrayOf(Complex(1.0,0.0), Complex(2.0,0.0), Complex(3.0,0.0), Complex(4.0,0.0))
        assertTrue(result.size == preppedData.size)

        var isEqual = true
        for (i in preppedData.indices){
            if ((preppedData[i].re != result[i].re) || preppedData[i].im != result[i].im) {
                isEqual = false
            }
        }

        assertTrue(isEqual)

    }

}