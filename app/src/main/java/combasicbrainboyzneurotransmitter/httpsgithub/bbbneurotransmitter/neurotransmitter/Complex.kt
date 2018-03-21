package combasicbrainboyzneurotransmitter.httpsgithub.bbbneurotransmitter.neurotransmitter

/**
 * Last Modified on 11/2/2018.
 * Code by RosettaCode:
 * https://rosettacode.org/wiki/Fast_Fourier_transform#Kotlin
 */


import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.cosh
import kotlin.math.sinh
import kotlin.math.sqrt
import kotlin.math.pow
import kotlin.math.abs

class Complex(val re: Float, val im: Float) {

    operator infix fun plus(x: Complex) = Complex(re + x.re, im + x.im)
    operator infix fun minus(x: Complex) = Complex(re - x.re, im - x.im)
    operator infix fun times(x: Float) = Complex(re * x, im * x)
    operator infix fun times(x: Complex) = Complex(re * x.re - im * x.im, re * x.im + im * x.re)
    operator infix fun div(x: Float) = Complex(re / x, im / x)

    val exp: Complex by lazy { Complex(cos(im), sin(im)) * (cosh(re) + sinh(re)) }
    val mag: Float by lazy {sqrt(re.pow(2) + im.pow(2))}

    override fun toString() = when {
        b == "0.000" -> a
        a == "0.000" -> b + 'i'
        im > 0 -> a + " + " + b + 'i'
        else -> a + " - " + b + 'i'
    }



    private val a = "%1.3f".format(re)
    private val b = "%1.3f".format(abs(im))
}
