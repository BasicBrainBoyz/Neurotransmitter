package combasicbrainboyzneurotransmitter.httpsgithub.bbbneurotransmitter.neurotransmitter

import android.os.HandlerThread

/**
 * Created by root on 24/02/18.
 */

class Neurotransmitter() {
    private var mBluetoothThread = BluetoothThread() // need bluetooth device
    private var mDetectionThread = HandlerThread("detection", -10)

    companion object {
        const val INFO_MESSAGE = 0
    }


    fun start(/* What params? */){
        // TODO Implement start function
    }

    // Allow the owner of the Neurotransmitter change its config
    fun changeState(){}

    fun holdOperation(){}

    fun continueOperation(){}

    fun reset(){}
}