package com.mirukman.spellcorrector.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import com.mirukman.spellcorrector.language_utils.DeletesGenerator;
import com.mirukman.spellcorrector.language_utils.HangulCombinator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Indexer {
    
    private Map<String, Set<String>> indexStore;

    private final DataManager dataManager;
    private final HangulCombinator hangulCombinator;
    private final DeletesGenerator deletesGenerator;

    @Autowired
    public Indexer(DataManager dataManager, HangulCombinator hangulCombinator, DeletesGenerator deletesGenerator) {
        indexStore = new HashMap<>();
        this.dataManager = dataManager;
        this.hangulCombinator = hangulCombinator;
        this.deletesGenerator = deletesGenerator;
    }
    
    @PostConstruct
    private void init() {
        createIndex();
    }

    //색인 생성
    //키들을 편집거리 1~2 까지 삭제연산 후 삭제연산된 단어를 통해 키를 검색할 수 있도록 역색인한다.
    private void createIndex() {

        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "3");

        Set<String> keys = dataManager.getAllKeys();
        AtomicInteger count = new AtomicInteger(0);
        keys.parallelStream()
                .filter(key -> key.length() >= 2)
                .forEach(key -> {
                    if(count.incrementAndGet() % 1000 == 0) {
                        System.out.printf("색인 %d%% 완료%n", count.get() * 100 / keys.size());
                    }
                    Set<String> deletes = Collections.emptySet();
                    if(key.length() >= 4) {
                        deletes = deletesGenerator.getEditDeletes(key, 2);
                    } else {
                        deletes = deletesGenerator.getEditDeletes(key, 1);
                    }

                    for(String delete : deletes) {
                        delete = hangulCombinator.compose(delete);
                        Set<String> indexedKeys = indexStore.getOrDefault(delete, new HashSet<>());
                        indexedKeys.add(key);
                        indexStore.put(delete, indexedKeys);
                    }
                });

        System.out.println("색인 크기: " + indexStore.keySet().size());

        System.clearProperty("java.util.concurrent.ForkJoinPool.common.parallelism");
    }

    //사용자가 입력한 키로부터 오타가 교정된 정타 후보셋들을 색인에서 가져오는 메소드
    public Set<String> getSuggestions(String delete) {
        return indexStore.getOrDefault(delete, new HashSet<>()); 
    }
}
