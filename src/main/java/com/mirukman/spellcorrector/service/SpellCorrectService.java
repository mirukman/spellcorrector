package com.mirukman.spellcorrector.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.mirukman.spellcorrector.data.DataManager;
import com.mirukman.spellcorrector.data.Indexer;
import com.mirukman.spellcorrector.item.MovieItem;
import com.mirukman.spellcorrector.language_utils.DeletesGenerator;
import com.mirukman.spellcorrector.language_utils.HangulCombinator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SpellCorrectService {
    
    private final DeletesGenerator deletesGenerator;
    private final Indexer indexer;
    private final HangulCombinator hangulCombinator;
    private final DataManager dataManager;

    @Autowired
    public SpellCorrectService(DeletesGenerator deletesGenerator, Indexer indexer, HangulCombinator hangulCombinator, DataManager dataManager) {
        this.deletesGenerator = deletesGenerator;
        this.indexer = indexer;
        this.hangulCombinator = hangulCombinator;
        this.dataManager = dataManager;
    }

    public Set<MovieItem> find(String inputQuery) {
        
        inputQuery = inputQuery.replaceAll("\\s+", "");

        //사용자 입력과 정확히 매칭되는 영화 제목이 존재하면 탐색 없이 리턴
        if(dataManager.getAllKeys().contains(inputQuery)) {
            return dataManager.getItemsByKey(inputQuery);
        }

        Set<String> searchQueries = new HashSet<>();
        
        //1. 사용자 입력
        searchQueries.add(hangulCombinator.compose(hangulCombinator.decompose(inputQuery, true)));

        //2. 사용자 입력을 삭제연산한 key들
        Set<String> deletes = Collections.emptySet();
        if(inputQuery.length() >= 4) {
            deletes = deletesGenerator.getEditDeletes(inputQuery, 2);
        } else if(inputQuery.length() >= 1) {
            deletes = deletesGenerator.getEditDeletes(inputQuery, 1);
        }
        searchQueries.addAll(deletes);

        Set<MovieItem> results = new HashSet<>();
        searchQueries.forEach(searchQuery -> {

            //제목이 key와 같은 영화가 있으면 결과set에 추가
            if(dataManager.getAllKeys().contains(searchQuery)) {
                results.addAll(dataManager.getItemsByKey(searchQuery));
            }
            
            //key를 통해 색인에서 영화를 검색하여 결과set에 추가
            indexer.getSuggestions(searchQuery).forEach(title -> {
                results.addAll(dataManager.getItemsByKey(title));
            });
        });

        return results;
    }
}
