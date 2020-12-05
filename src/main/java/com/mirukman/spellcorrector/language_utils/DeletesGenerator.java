package com.mirukman.spellcorrector.language_utils;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeletesGenerator {
    
    private final HangulCombinator hangulCombinator;

    @Autowired
    public DeletesGenerator(HangulCombinator hangulCombinator) {
        this.hangulCombinator = hangulCombinator;
    }

    public Set<String> getEditDeletes(String key, int maxEditDistance) {
        key = hangulCombinator.decompose(key, true);
        Set<String> deletes = new HashSet<>();
        if(key.length() <= maxEditDistance) {
            deletes.add("");
        }

        return edit(key, 0, deletes, maxEditDistance);
    }

    private Set<String> edit(String key, int editDistance, Set<String> deletes, int maxEditDistance) {

        StringBuilder deletesBuilder = new StringBuilder(key);
        
        if(++editDistance > maxEditDistance) {
            return deletes;
        }

        if(key.length() < 1) {
            return deletes;
        }

        for(int i = 0; i < deletesBuilder.length(); i++) {
            String delete = deletesBuilder.substring(0, i) + deletesBuilder.substring(i + 1);
            deletes.add(hangulCombinator.compose(delete));
            if(editDistance < maxEditDistance) {
                edit(delete, editDistance, deletes, maxEditDistance);
            }
        }

        return deletes;
    }
}
