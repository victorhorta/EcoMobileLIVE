package com.ecomaplive.ecomobilelive;

import android.util.SparseArray;

/**
 * Builds a lockable SparseArray. The main intention is to use it as a static
 * SparseArray, and lock it after statically adding the values to be mapped.
 * 
 * @author Victor
 * 
 * @param <E>
 */
public class LockableSparseArray<E> extends SparseArray<E> {
    private boolean mLocked = false;
    
    public LockableSparseArray(){
        super();
        this.mLocked = false;
    }
    
    
    public LockableSparseArray (int initialCapacity) {
        super(initialCapacity);
        this.mLocked = false;        
    }
    
    /**
     * Method used to lock the SparseArray 
     */
    public void lock() {
        mLocked = true;
    }
    
    @Override
    public void append(int key, E value) {
        if (mLocked)
            return; // Maybe throw an exception
        super.append(key, value);
    }
    
    @Override
    public void put (int key, E value) {
        if (mLocked)
            return; // Maybe throw an exception
        super.put(key, value);
    }
    
    @Override
    public void remove (int key) {
        if (mLocked)
            return; // Maybe throw an exception
        super.remove(key);
    }
    
    @Override
    public void delete (int key) {
        if (mLocked)
            return; // Maybe throw an exception
        super.delete(key);
    }
    
    @Override
    public void clear () {
        if (mLocked)
            return; // Maybe throw an exception
        super.clear();
    }
}
