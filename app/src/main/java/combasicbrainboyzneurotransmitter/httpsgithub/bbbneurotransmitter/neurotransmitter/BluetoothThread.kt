package combasicbrainboyzneurotransmitter.httpsgithub.bbbneurotransmitter.neurotransmitter

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.Handler
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.util.*

/**
 * Created by root on 24/02/18.
 */

class BluetoothThread: Thread(){
    private var mESPInputStream: InputStream? = null
    private var mESPOutputStream: OutputStream? = null

    private var mCollectData: Boolean = true
    private var mDetectionHandler: Handler? = null
    private var mBTAdapter: BluetoothAdapter? = null
    private var mNTHandler: Handler? = null
    private var mBTSocket: BluetoothSocket? = null

    private var mSamplesIndex: Int = 0
    private var mSamples: FloatArray = FloatArray(512)

    companion object {
        private const val INFO_MESSAGE = 0
        private const val SSVEP_DATA: Int = 1
        private val ESP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        private const val ESP_mac_address = "30:AE:A4:2C:A4:96"
    }

    fun BluetoothThread(ntHandler: Handler?, detectionHandler: Handler?){
        mDetectionHandler = detectionHandler
        mNTHandler = ntHandler
    }

    fun establishESPConnection(){
        mBTAdapter = BluetoothAdapter.getDefaultAdapter()

        if(mBTAdapter != null){
            val device: BluetoothDevice? = mBTAdapter?.getRemoteDevice(ESP_mac_address)

            try{
                mBTSocket = device?.createRfcommSocketToServiceRecord(ESP_UUID)
            }
            catch (e: IOException){
                val btIoException = "Error: Cannot Create Socket for ESP32"
                val btSocketError = mNTHandler?.obtainMessage(INFO_MESSAGE, btIoException)
                mNTHandler?.sendMessage(btSocketError)
            }

            mBTAdapter?.cancelDiscovery()

            try{
                mBTSocket?.connect()
            }
            catch (e: IOException){
                val btConnectException = "Error: Connection to ESP32 Failed"
                val btConnectError = mNTHandler?.obtainMessage(INFO_MESSAGE, btConnectException)
                mNTHandler?.sendMessage(btConnectError)

                mBTSocket?.close()
            }

            try{
                mESPOutputStream =  mBTSocket?.outputStream
            }
            catch (e: IOException){
                val btOutputException = "Error: ESP32 Output Stream Generation Failed"
                val btOutputError = mNTHandler?.obtainMessage(INFO_MESSAGE, btOutputException)
                mNTHandler?.sendMessage(btOutputError)
            }

            try{
                mESPInputStream = mBTSocket?.inputStream
            }
            catch (e: IOException){
                val btInputException = "Error: ESP32 Input Stream Generation Failed"
                val btInputError = mNTHandler?.obtainMessage(INFO_MESSAGE, btInputException)
                mNTHandler?.sendMessage(btInputError)

            }

        }
        else{
            val btNotEnabled = "ERROR: Bluetooth Not Enabled"
            val btEnabledError = mNTHandler?.obtainMessage(INFO_MESSAGE, btNotEnabled)
            mNTHandler?.sendMessage(btEnabledError)
        }
    }

    // TODO Implement reconnection logic
    //fun reestablishESPConnection(){
    //
    //}

    fun closeESPConnection(){
        if(mBTSocket != null){
            mBTSocket?.close()
        }
    }

    override fun run(){
        var sampleBuffer: ByteArray = ByteArray(4)
        var newSampleCount: Int = 0
        var availableBytes: Int = 0

        while(mCollectData){
            if(mSamplesIndex < 511) {

                // read in needed number of samples
                try {
                    availableBytes = mESPInputStream?.available() as Int
                    if (availableBytes > 1) {
                        mESPInputStream?.read(sampleBuffer, 2, 2)
                        // TODO TURN NEW BYTES INTO FLOAT
                        // TODO APPEND TO FLOAT ARRAY
                        // TODO INDEX NUMBER OF NEW SAMPLES

                        val byteBuffer: ByteBuffer = ByteBuffer.wrap(sampleBuffer)
                        val newSampleShort: Short = byteBuffer.getShort(0)
                        val newSample: Float = newSampleShort.toFloat()
                        mSamples[mSamplesIndex] = newSample
                    }
                } catch (e: IOException) {
                    val btReadException = "Error: Reading Data From ESP32 Failed"
                    val btReadError = mNTHandler?.obtainMessage(INFO_MESSAGE, btReadException)
                    mNTHandler?.sendMessage(btReadError)
                }
            }
            else { // Have needed 512 samples
                // Generate message for processing
                // Send message
                // Take last 512 - 60 samples from current floatArray
                // Reset floatArray with said samples
                // Set mHeldSamples to (512-60)

                val detectionDataMessage = mDetectionHandler?.obtainMessage(SSVEP_DATA, mSamples)
                mDetectionHandler?.sendMessage(detectionDataMessage)

                //val tempSamples: FloatArray = FloatArray(512)
                //tempSamples(451, {i -> mSamples[i]})
                //tempSamples[0-451] = mSamples[59-511] // TODO FIX THIS LOGIC
                mSamplesIndex = 451

            }

            // if(enough data)
            // make ssvepMessage: Message
            // (SSVEP_MESSAGE, ssvepData: ______)
        }
    }

    fun setCollecting(collecting: Boolean){
        mCollectData = collecting
    }

    fun sendMessageToESP(message: String){
        val msgBuffer: ByteArray = message.toByteArray()

        try {
            mESPOutputStream?.write(msgBuffer)
        } catch(e: IOException){
                val btMessageException = "Error: Sending Message to ESP32 Failed"
                val btMessageError = mNTHandler?.obtainMessage(INFO_MESSAGE, btMessageException)
                mNTHandler?.sendMessage(btMessageError)
        }
    }
}
