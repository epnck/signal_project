package com.data_management.data_readers;

import com.data_management.DataStorage;

import java.io.IOException;

public interface DataReader {
    /**
     * Reads data from a specified source and stores it in the data storage.
     * 
     * @param dataStorage the storage where data will be stored
     * @throws IOException if there is an error reading the data
     */
    void startReading(DataStorage dataStorage) throws IOException;
}
