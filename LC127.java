class Solution {
    private class Ladder {
        String word;
        int dist;
        Ladder(String w, int d) {
            word = w;
            dist = d;
        }
    }
    
    public int ladderLength(String beginWord, String endWord, List<String> wordList) {
        Map<String, Integer> dict = new HashMap<>();
        int wlLen = wordList.size();
        int[] visit = new int[wlLen];
        
        for(int i=0; i<wlLen; i++) {
            dict.put(wordList.get(i), i);
        }
        
        if(!dict.containsKey(endWord)) return 0; 
        
        Queue<Ladder> queue = new LinkedList<>();
        queue.add(new Ladder(beginWord, 1));
        if(dict.containsKey(beginWord)) {
            visit[dict.get(beginWord)] = 1;
        }
        
        while(!queue.isEmpty()) {
            Ladder aStep = queue.poll(); 
            String str = aStep.word;
            int distSoFar = aStep.dist;
            
            if(str.compareTo(endWord) == 0) return distSoFar;
            
            for(int i=0; i<wlLen; i++) { 
                if(visit[i] == 0 && isDistanceOne(str, wordList.get(i))) {
                    visit[i] = 1;
                    queue.add(new Ladder(wordList.get(i), distSoFar + 1));
                }
            }
        }
        
        return 0;
    }
    
    private boolean isDistanceOne(String s1, String s2) {
        if(s1.length() != s2.length()) return false;
        
        int diff = 0;
        for(int i=0; i<s1.length(); i++) {
            if(s1.charAt(i) != s2.charAt(i)) diff++;
            if(diff > 1) return false;
        }
        
        return true;
    }
}
