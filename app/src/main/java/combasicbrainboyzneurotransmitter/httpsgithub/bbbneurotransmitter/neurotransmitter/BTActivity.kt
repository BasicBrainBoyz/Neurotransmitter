package combasicbrainboyzneurotransmitter.httpsgithub.bbbneurotransmitter.neurotransmitter

/**
 * Created by root on 18/03/18.
 */

import java.io.IOException
import java.io.OutputStream
import java.io.InputStream
import java.util.UUID

import android.os.Handler

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.widget.TextView

import java.nio.charset.Charset

class BTActivity {
    private var out: TextView? = null
    private var btAdapter: BluetoothAdapter? = null
    private var btSocket: BluetoothSocket? = null
    private var outStream: OutputStream? = null
    private var myLabel: TextView? = null

    private var workerThread: Thread? = null
    private var readBuffer: ByteArray? = null
    private var readBufferPosition: Int = 0
    @Volatile
    private var stopWorker: Boolean = false
    private var mmInputStream: InputStream? = null

    init{
        btAdapter = BluetoothAdapter.getDefaultAdapter()
        CheckBTState()
    }


    fun onResume() {

        // Set up a pointer to the remote node using it's address.
        val device = btAdapter?.getRemoteDevice(address)

        // Two things are needed to make a connection:
        //   A MAC address, which we got above.
        //   A Service ID or UUID.  In this case we are using the
        //     UUID for SPP.
        try {
            btSocket = device?.createRfcommSocketToServiceRecord(MY_UUID)
        } catch (e: IOException) {
        }

        // Discovery is resource intensive.  Make sure it isn't going on
        // when you attempt to connect and pass your message.
        btAdapter?.cancelDiscovery()

        // Establish the connection.  This will block until it connects.
        try {
            btSocket!!.connect()
        } catch (e: IOException) {

            try {
                btSocket?.close()
            } catch (e2: IOException) {
            }

        }

        // Create a data stream so we can talk to server.

        try {
            outStream = btSocket?.outputStream
        } catch (e: IOException) {
        }

        val message = "Hello from Android.\n"
        val msgBuffer: ByteArray = message.toByteArray()
        try {
            outStream?.write(msgBuffer)
        } catch (e: IOException) {
            var msg = "In onResume() and an exception occurred during write: " + e.message
            if (address == "00:00:00:00:00:00")
                msg = "$msg.\n\nUpdate your server address from 00:00:00:00:00:00 to the correct address on line 37 in the java code"
            msg = msg + ".\n\nCheck that the SPP UUID: " + MY_UUID.toString() + " exists on server.\n\n"

        }

        beginListenForData()

    }

    public  fun onPause() {

        if (outStream != null) {
            try {
                outStream!!.flush()
            } catch (e: IOException) {
            }
        }

        try {
            btSocket!!.close()
        } catch (e2: IOException) {
        }

    }

    private fun CheckBTState() {
        // Check for Bluetooth support and then check to make sure it is turned on

        // Emulator doesn't support Bluetooth and will return null
        if (btAdapter == null) {
        } else {
            if (btAdapter?.isEnabled as Boolean) {
                out?.append("\n...Bluetooth is enabled...")
            } else {
                //Prompt user to turn on Bluetooth
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                //startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            }
        }
    }


    private fun beginListenForData() {
        val handler = Handler()

        try {
            mmInputStream = btSocket?.inputStream
        } catch (e: IOException) {
        }

        stopWorker = false
        readBufferPosition = 0
        readBuffer = ByteArray(1024)
        workerThread = Thread(Runnable {
            while (!Thread.currentThread().isInterrupted && !stopWorker) {

                try {
                    //String msg = "Thread started...\n";

                    val bytesAvailable: Int = mmInputStream?.available() as Int
                    if (bytesAvailable > 0) {
                        val packetBytes = ByteArray(bytesAvailable)
                        mmInputStream?.read(packetBytes)
                        readBufferPosition = 0
                        val encodedBytes = ByteArray(bytesAvailable)
                        for (i in 0 until bytesAvailable) {
                            val b = packetBytes[i]

                            readBuffer!![readBufferPosition++] = b

                            System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.size)
                        }
                        val data: String = encodedBytes.toString() + "US-ASCII"
                        handler.post { out?.append("Data!: $data...\n") }
                    }
                } catch (ex: IOException) {
                    // String msg = "done looking for data\n";
                    //out.append("done");
                    stopWorker = true
                }

            }
        })
        out?.append("starting thread\n")
        workerThread?.start()
    }

    companion object {
        //private const val REQUEST_ENABLE_BT = 1

        // Well known SPP UUID
        private val MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

        // Insert your server's MAC address
        private const val address = "30:AE:A4:2C:A4:96"
    }
}
