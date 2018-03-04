package com.example.ssvepdetection

/**
 * Last Modified on 11/2/2018.
 * Code by RosettaCode:
 * https://rosettacode.org/wiki/Fast_Fourier_transform#Kotlin
 */


import java.lang.Math.*


class Complex(val re: Double, val im: Double) {
    operator infix fun plus(x: Complex) = Complex(re + x.re, im + x.im)
    operator infix fun minus(x: Complex) = Complex(re - x.re, im - x.im)
    operator infix fun times(x: Double) = Complex(re * x, im * x)
    operator infix fun times(x: Complex) = Complex(re * x.re - im * x.im, re * x.im + im * x.re)
    operator infix fun div(x: Double) = Complex(re / x, im / x)
    val exp: Complex by lazy { Complex(cos(im), sin(im)) * (cosh(re) + sinh(re)) }
    val mag: Double by lazy {Math.sqrt(Math.pow(re,2.0) + Math.pow(im,2.0))}

    override fun toString() = when {
        b == "0.000" -> a
        a == "0.000" -> b + 'i'
        im > 0 -> a + " + " + b + 'i'
        else -> a + " - " + b + 'i'
    }



    private val a = "%1.3f".format(re)
    private val b = "%1.3f".format(abs(im))
}