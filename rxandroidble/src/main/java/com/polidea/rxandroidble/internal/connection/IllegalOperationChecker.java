package com.polidea.rxandroidble.internal.connection;

import android.bluetooth.BluetoothGattCharacteristic;

import com.polidea.rxandroidble.internal.BluetoothGattCharacteristicProperty;
import com.polidea.rxandroidble.internal.BleIllegalOperationException;

import javax.inject.Inject;

import rx.Completable;
import rx.functions.Action0;

/**
 * Class for checking whether the requested operation is legal on chosen characteristic.
 */
public class IllegalOperationChecker {

    private IllegalOperationHandler resultHandler;

    @Inject
    public IllegalOperationChecker(IllegalOperationHandler resultHandler) {
        this.resultHandler = resultHandler;
    }

    /**
     * This method checks whether the supplied characteristic possesses properties supporting the requested kind of operation, specified by
     * the supplied bitmask.
     *
     * Emits {@link BleIllegalOperationException} if there was no match between supported and necessary properties of characteristic and
     * check has not been suppressed
     *
     * @param characteristic   a {@link BluetoothGattCharacteristic} the operation is done on
     * @param neededProperties properties required for the operation to be successfully completed
     * @return {@link Completable} deferring execution of the check till subscription
     */
    public Completable checkAnyPropertyMatches(final BluetoothGattCharacteristic characteristic,
                                               final @BluetoothGattCharacteristicProperty int neededProperties) {
        return Completable.fromAction(new Action0() {
            public void call() {
                final int characteristicProperties = characteristic.getProperties();

                if ((characteristicProperties & neededProperties) == 0) {
                    BleIllegalOperationException exception = resultHandler.handleMismatchData(characteristic, neededProperties);
                    if (exception != null) {
                        throw exception;
                    }
                }
            }
        });
    }
}