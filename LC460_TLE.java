class LFUCache {
    Map<Integer, Integer> cache, keyToTimeMap, keyToUseMap;
    Map<Integer, Set<Integer>> useToKeysMap;
    int globalTime, capacity, globalMinUse;
    
    public LFUCache(int capacity) {
        this.capacity = capacity;
        globalTime = 0;
        globalMinUse = 1;
        cache = new HashMap<>();
        keyToTimeMap = new HashMap<>();
        useToKeysMap = new HashMap<>();
        keyToUseMap = new HashMap<>();
    }
    
    public int get(int key) {
        if(capacity == 0 || !cache.containsKey(key)) return -1;
        
        globalTime++;
        
        // the key exists, update it
        update(key);
        // return the value
        return cache.get(key);
    }
    
    public void put(int key, int value) {
        if(capacity == 0) return;
        
        globalTime++;
        
        if(cache.containsKey(key)) {
            // update
            update(key, value);
            return;
        }
        
        if(cache.size() == capacity && cache.size() > 0) {
            // evict
            evict();
        }
        
        insert(key, value);
    }
    
    private void insert(int key, int value) {
        // new item
        cache.put(key, value);
        keyToTimeMap.put(key, globalTime);
        // use is always 1 for new item
        keyToUseMap.put(key, 1);
        if(useToKeysMap.containsKey(1)) {
            // more keys are present at use = 1
            useToKeysMap.get(1).add(key);
        }
        else {
            // initiate set of keys at use = 1
            Set<Integer> keys = new HashSet<>();
            keys.add(key);
            useToKeysMap.put(1, keys);
        }
        // reset global min use to 1
        globalMinUse = 1;
    }
    
    private void update(int key) {
        keyToTimeMap.put(key, globalTime);
        updateUse(key);
    }
    
    private void update(int key, int value) {
        cache.put(key, value);
        keyToTimeMap.put(key, globalTime);
        updateUse(key);
    }
    
    private void updateUse(int key) {
        int use = keyToUseMap.get(key);
        Set<Integer> keys = useToKeysMap.get(use);
        
        if(keys.size() == 1) {
            // only one key, remove it
            useToKeysMap.remove(use);
            if(globalMinUse == use) {
                // the only key left with this use is getting updated, so update globalMinUse 
                globalMinUse++;
            }
        }
        else {
            // multiple keys preset, just remove current key from this set
            keys.remove(key);
        }
        
        use++;
        if(useToKeysMap.containsKey(use)) {
            // there are more keys at this use, put current key in that use set
            Set<Integer> _keys = useToKeysMap.get(use);
            _keys.add(key);
        }
        else {
            // this key is the first item in this use set
            Set<Integer> _key = new HashSet<>();
            _key.add(key);
            useToKeysMap.put(use, _key);
        }
        // finally update key -> use map
        keyToUseMap.put(key, use);
    }
    
    private void evict() {
        // evict least used
        Set<Integer> keys = useToKeysMap.get(globalMinUse);
        int deleteKey = -1;
        
        if(keys.size() == 1) {
            // only one key at this use, remove it
            deleteKey = keys.iterator().next();
            useToKeysMap.remove(globalMinUse);
            globalMinUse--;
        }
        else {
            // multiple keys, find the one with lowest global time
            int minTime = Integer.MAX_VALUE;
            int minKey = -1;
            for(int key : keys) {
                int time = keyToTimeMap.get(key);
                if(time < minTime) {
                    minTime = time;
                    minKey = key;
                }
            }
            deleteKey = minKey;
            keys.remove(deleteKey);
        }
        cache.remove(deleteKey);
        keyToTimeMap.remove(deleteKey);
        keyToUseMap.remove(deleteKey);
    }
}

/**
 * Your LFUCache object will be instantiated and called as such:
 * LFUCache obj = new LFUCache(capacity);
 * int param_1 = obj.get(key);
 * obj.put(key,value);
 */
