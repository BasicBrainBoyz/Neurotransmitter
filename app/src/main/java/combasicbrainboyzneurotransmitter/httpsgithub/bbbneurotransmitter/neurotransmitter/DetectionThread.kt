package combasicbrainboyzneurotransmitter.httpsgithub.bbbneurotransmitter.neurotransmitter

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message

/**
 * Created by root on 24/02/18.
 */

//class DetectionThread(name: String, priority: Int) : HandlerThread(name, priority){
//    // TODO implement methods for detection thread
//
//    val mHandler = DetectionHandler(this.looper)
//
//}

class DetectionHandler(looper: Looper) : Handler(looper) {
    companion object {
        private const val SSVEP_DATA: Int = 1
    }


    override fun handleMessage(msg: Message?) {
        // take data and perform FFT
        when (msg?.what) {
            SSVEP_DATA -> {
                // analyze_
                // val data: DoubleArray = msg.object
            }
        }
    }
}

/*







*/





