package combasicbrainboyzneurotransmitter.httpsgithub.bbbneurotransmitter.neurotransmitter

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.Handler
import android.os.Message
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.util.*

/**
 * Created by root on 24/02/18.
 */

class BluetoothThread(ntHandler: Handler?, detectionHandler: Handler?): Thread(){
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

    init {
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
                var btSocketError: Message?
                btSocketError = Message.obtain(mNTHandler, INFO_MESSAGE, btIoException)
                mNTHandler?.sendMessage(btSocketError)
            }

            mBTAdapter?.cancelDiscovery()

            try{
                mBTSocket?.connect()
            }
            catch (e: IOException){
                val btConnectException = "Error: Connection to ESP32 Failed"
                var btConnectError: Message?
                btConnectError = Message.obtain(mNTHandler, INFO_MESSAGE, btConnectException)
                mNTHandler?.sendMessage(btConnectError)

                mBTSocket?.close()
            }

            try{
                mESPOutputStream =  mBTSocket?.outputStream
            }
            catch (e: IOException){
                val btOutputException = "Error: ESP32 Output Stream Generation Failed"
                var btOutputError: Message?
                btOutputError = Message.obtain(mNTHandler, INFO_MESSAGE, btOutputException)
                mNTHandler?.sendMessage(btOutputError)
            }

            try{
                mESPInputStream = mBTSocket?.inputStream
            }
            catch (e: IOException){
                val btInputException = "Error: ESP32 Input Stream Generation Failed"
                var btInputError: Message?
                btInputError = Message.obtain(mNTHandler, INFO_MESSAGE, btInputException)
                mNTHandler?.sendMessage(btInputError)

            }

        }
        else{
            val btNotEnabled = "ERROR: Bluetooth Not Enabled"
            var btEnabledError: Message?
            btEnabledError = Message.obtain(mNTHandler, INFO_MESSAGE, btNotEnabled)
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
        var sampleBuffer = ByteArray(2)
        var availableBytes: Int

        while(mCollectData){
            if(mSamplesIndex < 511) {

                // read in needed number of samples
                try {
                    availableBytes = mESPInputStream?.available() as Int
                    if (availableBytes > 1) {
                        mESPInputStream?.read(sampleBuffer, 0, 2)

                        val byteBuffer: ByteBuffer = ByteBuffer.wrap(sampleBuffer)
                        val newSampleShort: Short = byteBuffer.getShort(0)
                        val newSample: Float = newSampleShort.toFloat()
                        mSamples[mSamplesIndex] = newSample
                        mSamplesIndex += 1
                    }
                } catch (e: IOException) {
                    val btReadException = "Error: Reading Data From ESP32 Failed"
                    var btReadError: Message?
                    btReadError = Message.obtain(mNTHandler, INFO_MESSAGE, btReadException)
                    mNTHandler?.sendMessage(btReadError)
                }
            }
            else { // Have needed 512 samples
                var detectionDataMessage: Message?
                detectionDataMessage = Message.obtain(mDetectionHandler, SSVEP_DATA, mSamples)
                mDetectionHandler?.sendMessage(detectionDataMessage)

                val elements: List<Float> = mSamples.takeLast(452)
                val tempSamples: FloatArray = elements.toFloatArray()
                val tempZeros: FloatArray = FloatArray(60)
                val newSamples: FloatArray = tempSamples.plus(tempZeros)
                mSamples = newSamples

                mSamplesIndex = 451
            }
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
                val btMessageError: Message?
                btMessageError = Message.obtain(mNTHandler, INFO_MESSAGE, btMessageException)
                mNTHandler?.sendMessage(btMessageError)
        }
    }
}
