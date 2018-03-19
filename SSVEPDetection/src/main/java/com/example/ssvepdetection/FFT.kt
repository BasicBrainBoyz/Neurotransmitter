package com.example.ssvepdetection

/**
 *
 * Original Code by RosettaCode, Last Modified on 11/2/2018.:
 * https://rosettacode.org/wiki/Fast_Fourier_transform#Kotlin
 *
 * Code updated by BBB ltd. on 3/4/2018
 */
object FFT {
    fun fft(a: Array<Complex>) = _fft(a, Complex(0.0f, 2.0f), 1.0f)
    fun inverseFFT(a: Array<Complex>) = _fft(a, Complex(0.0f, -2.0f), 2.0f)

    private fun _fft(a: Array<Complex>, direction: Complex, scalar: Float): Array<Complex> =
            if (a.size == 1)
                a
            else {
                val n = a.size
                require(n % 2 == 0, { "The Cooley-Tukey com.example.ssvepdetection.FFT algorithm only works when the length of the input is even." })

                var (evens, odds) = Pair(emptyArray<Complex>(), emptyArray<Complex>())
                for (i in a.indices)
                    if (i % 2 == 0) evens += a[i]
                    else odds += a[i]
                evens = _fft(evens, direction, scalar)
                odds = _fft(odds, direction, scalar)

                val pairs = (0 until n / 2).map {
                    val offset = (direction * (kotlin.math.PI.toFloat() * it / n)).exp * odds[it] / scalar
                    val base = evens[it] / scalar
                    Pair(base + offset, base - offset)
                }
                var (left, right) = Pair(emptyArray<Complex>(), emptyArray<Complex>())
                for ((l, r) in pairs) { left += l; right += r }
                left + right
            }

    fun prepData(data: Array<Float>, numPoints: Int): Array<Complex>{
        //returns an complex array of size numPoints.
        //for points greater than size of data, uses a zero to pad out the data
        return Array(numPoints, {i -> if (i < data.size) Complex(data[i],0.0f) else Complex(0.0f,0.0f) })
    }
}

class InvalidFFTLength(override var message:String): Exception(message)