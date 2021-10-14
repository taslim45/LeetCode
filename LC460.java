class LFUCache {
    private class Data {
        int key, value, time, freq;
        Data(int k, int v, int t, int f) {
            key = k;
            value = v;
            time = t;
            freq = f;
        }
    }
    
    Map<Integer, Data> keyToDataMap;
    Map<Integer, Set<Data>> freqToDataSetMap;
    
    int globalTime, capacity, globalFreq;
    
    public LFUCache(int capacity) {
        this.capacity = capacity;
        globalTime = 0;
        globalFreq = 1;
        keyToDataMap = new HashMap<>();
        freqToDataSetMap = new HashMap<>();
    }
    
    public int get(int key) {
        if(capacity == 0 || !keyToDataMap.containsKey(key)) return -1;
        
        globalTime++;
        update(key);
        return keyToDataMap.get(key).value;
    }
    
    public void put(int key, int value) {
        if(capacity == 0) return;
        
        globalTime++;
        
        if(keyToDataMap.containsKey(key)) {
            update(key, value);
            return;
        }
        
        if(keyToDataMap.size() == capacity) {
            evict();
        }
        insert(key, value);
    }
    
    private void insert(int key, int value) {
        // new item, frequency is 1, make globalfreq 1
        globalFreq = 1;
        
        Data data = new Data(key, value, globalTime, globalFreq);
        keyToDataMap.put(key, data);
        
        if(freqToDataSetMap.containsKey(globalFreq)) {
            // add this data to this frequency
            freqToDataSetMap.get(globalFreq).add(data);
        }
        else {
            // create an entry for this frequency
            Set<Data> dataSet = new TreeSet<>(new Comparator<Data>() {
                public int compare(Data d1, Data d2) {
                    return d1.time - d2.time;
                }
            });
            dataSet.add(data);
            freqToDataSetMap.put(globalFreq, dataSet);
        }
    }
    
    private void evict() {
        // evict the lowest frequency
        Set<Data> dataSet = freqToDataSetMap.get(globalFreq);
        Data deleteData = null;
        if(dataSet.size() == 1) {
            // only one element, delete the entry
            deleteData = dataSet.iterator().next();
            freqToDataSetMap.remove(globalFreq);
            
            if(freqToDataSetMap.size() >= 1) globalFreq++;
            else globalFreq = 1;
        }
        else {
            // more than one elements in this frequency
            // delete the first one
            deleteData = dataSet.iterator().next();
            dataSet.remove(deleteData);
        }
        
        keyToDataMap.remove(deleteData.key);
    }
    
    private void update(int key) {
        // update time and frequency
        Data data = keyToDataMap.get(key);
        int oldFreq = data.freq;
        
        // use old frequency to find from frequency table
        Set<Data> dataSet = freqToDataSetMap.get(oldFreq);
        if(dataSet.size() == 1) {
            // one entry, remove it
            freqToDataSetMap.remove(oldFreq);
            if(oldFreq == globalFreq) globalFreq++;
        }
        else {
            dataSet.remove(data);
        }
        
        data.freq++;
        data.time = globalTime;
        keyToDataMap.put(key, data);
        
        if(freqToDataSetMap.containsKey(oldFreq + 1)) {
            // add to existing set
            freqToDataSetMap.get(oldFreq+1).add(data);
        }
        else {
            // create new set
            Set<Data> set = new TreeSet<>(new Comparator<Data>() {
                public int compare(Data d1, Data d2) {
                    return d1.time - d2.time;
                }
            });
            set.add(data);
            freqToDataSetMap.put(oldFreq+1, set);
        }
    }
    
    private void update(int key, int value) {
        // update value
        Data data = keyToDataMap.get(key);
        data.value = value;
        keyToDataMap.put(key, data);
        // update rest
        update(key);
    }
}

/**
 * Your LFUCache object will be instantiated and called as such:
 * LFUCache obj = new LFUCache(capacity);
 * int param_1 = obj.get(key);
 * obj.put(key,value);
 */
