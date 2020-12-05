package com.mirukman.spellcorrector.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import com.mirukman.spellcorrector.item.MovieItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

@Component
public class DataManager {
    
    private Map<String, Set<MovieItem>> moviesMap =new HashMap<>();

    private ApplicationArguments args;

    @Autowired
    public DataManager(ApplicationArguments args) {
        this.args = args;
    }

    @PostConstruct
    private void init() {
        readFile(args.getSourceArgs()[0]);
    }

    //tsv 형식의 파일을 읽어와서 영화 아이템 객체들을 생성해서 저장한다.
    private void readFile(String filePath) {
        File file = new File(filePath);
        
        try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while((line = reader.readLine()) != null) {

                String[] tokens = line.split("\t");

                String title = tokens[0];
                String englishTitle = tokens[1];
                String releaseYear = tokens[2];
                String nation = tokens[3];
                String type = tokens[4];
                String genre = tokens[5];
                String status = tokens[6];
                String director = tokens[7];
                String producer = tokens[8];

                MovieItem item = new MovieItem.Builder(title)
                                        .englishTitle(englishTitle)
                                        .releaseYear(releaseYear)
                                        .nation(nation)
                                        .type(type)
                                        .genre(genre)
                                        .status(status)
                                        .director(director)
                                        .producer(producer)
                                        .build();

                //영화 제목으로부터 검색을 위한 키들을 추출해 각 키에 영화들을 매핑
                Set<String> searchKeys = makeSearchKeys(title);
                searchKeys.forEach(key -> {
                    Set<MovieItem> set = moviesMap.getOrDefault(key, new HashSet<>());
                    set.add(item);
                    moviesMap.put(key, set);
                });
            }
        } catch(FileNotFoundException e) {
            System.err.println("파일을 찾지 못했습니다: " + filePath);
            System.exit(0);
        } catch(IOException e) {
            System.err.println("파일 읽기 실패");
            System.exit(0);
        }

    }

    //영화 제목에서 영화 검색을 위한 key들을 추출하는 메소드
    //만들어진 키를 통해 해당하는 영화 아이템 목록들이 검색된다.
    private Set<String> makeSearchKeys(String title) {

        Set<String> searchKeys = new HashSet<>();

        //영화제목은 ':'를 기준으로 주제, 부제가 나뉘어있는 경우가 꽤 흔하다.
        if(title.contains(":")) {
            String[] titleArray = title.split(":");
            String mainTitle = titleArray[0];
            String subTitle = titleArray[1];

            searchKeys.add(mainTitle.replaceAll("\\s+", ""));
            searchKeys.addAll(Arrays.asList(mainTitle.split("\\s+")));

            if(titleArray.length >= 2) {
                searchKeys.add(subTitle);
                searchKeys.addAll(Arrays.asList(subTitle.split("\\s+")));
            }

        } else {
            searchKeys.add(title.replaceAll("\\s+", ""));
            searchKeys.addAll(Arrays.asList(title.split("\\s+")));
        }

        return searchKeys;
    }

    public Set<String> getAllKeys() {
        return moviesMap.keySet();
    }

    //키를 통해 영화 목록들을 검색하는 API 메소드
    public Set<MovieItem> getItemsByKey(String key) {
        return moviesMap.getOrDefault(key, new HashSet<>());
    }
}
