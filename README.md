# 1. 소개 #
---

제목을 통해 국내 영화정보를 검색하는 REST API 입니다.

오타가 있을경우 교정을 통해 정타 후보셋을 추출하여 해당 정타 후보들에 매칭되는 영화 정보들을 응답해줍니다.

프로젝트는 tab으로 구분된 영화 정보들을 읽어들이고 검색할 수 있도록 구현하였습니다. 검색과정에 오타가 포함되면 정타 후보를 계산하여 영화 정보를 돌려줍니다.

해당 프로젝트에서는 key들을 추출 후 자음/모음 단위로 분리하여 일정 편집거리 이내에서 삭제 연산을 수행 후 미리 메모리에 역색인 합니다. 따라서 사용자 입력이 들어오면 해당 입력과 입력의 삭제연산된 단어들을 색인에서 검색하기만 하면되어 시간복잡도를 줄였습니다. 원래대로면 사용자 입력을 메모리의 모든 key들과 brute force 방식으로 편집거리를 계산하여 유사한 단어만 돌려주도록 해야할 것입니다.

삭제연산을 메모리에 미리 역색인하여 속도를 높이는 방식은 외국의 오픈소스 프로젝트인 'Symspell'에서 사용된 방식입니다.

[https://github.com/wolfgarbe/symspell](https://github.com/wolfgarbe/symspell)

이 프로젝트에서는 오타 역색인을 메모리에 하기 때문에 데이터의 수가 많아지면 비효율적입니다. 데이터의 수가 메모리로 감당이 되지 않을만큼 많다면 편집거리 제한을 2에서 1로 줄이거나 메모리가 아닌 디스크 색인을 위해 '루씬', '엘라스틱서치', '솔라' 등의 색인 오픈소스를 사용하면 될 것입니다.

복잡하고 고도화된 로직이 사용되지 않았지만 데이터 수가 한정적이거나 사용자가 입력하는 검색어가 특정 형식을 벗어나지 않는 경우라면 충분히 응용 후 사용하시면 효과를 볼 수 있을 것입니다.

참고로 이 프로젝트는 단위 테스트 및 통합테스트를 거치지 않은 프로토타입 수준의 프로젝트입니다.

<br>

# 2. 오타교정 #
---

### 1) 영화 검색을 위한 key 추출 ###

영화 제목으로부터 영화 검색이 가능하도록 하는 key들을 추출한다.

예를 들어 제목이 '범죄와의 전쟁' 이라면 \['범죄와의전쟁', '범죄와의', '전쟁'\] 세 개의 key가 생성된다.

이후 해시맵에 (key, 영화아이템) 쌍을 저장한다.

### 2) 오타 색인 ###

과정 1에서 만든 모든 key들을 불러와 각 key에 대해 아래의 과정을 진행한다.

자음/모음 단위로 분리 후 길이에 따라 편집거리 1~2까지 삭제연산을 한다.
~~~ txt
(도둑들 -> ㅗ둑들, ㄷ둑들, 도ㅜㄱ들, 돋ㄱ들, 도두들, 도두글, 도둑ㄷㄹ, 도둑드)
~~~

삭제 연산된 단어들 모두에 대해 (오타,정타)를 HashMap에 역색인한다.
(예시: key\[ㅗ둑들\] -> value\[도둑들\])

### 3) 교정 로직 ###

사용자 검색어가 입력될 경우 해당 검색어와 100% 매칭되는 영화제목이 존재할 경우 해당 영화를 돌려준다. 여러개일 경우 모두 돌려준다.

사용자 검색어가 영화제목 사전에 존재하지 않는 경우 교정 로직을 시작한다.

먼저 색인을 검색할 용어들을 결정한다.
	a. 사용자 입력
	b. 사용자 입력을 삭제연산한 단어들

a, b에서 생성한 key 각각에 대해 과정2에서 만든 색인을 검색하여 정타 후보들을 추출한다.

이후 정타 후보들에 해당하는 모든 영화 정보를 응답으로 돌려준다.

아래는 예시이다.

~~~ txt
사용자 입력: 간상
검색 용어 결정: (간상, ㅏㄴ상, ㄱㄴ상, 가상, 가낭, 간ㅅㅇ, 간사) 용어와 같은 영화 제목이 있으면 해당 영화들을 결과 셋에 저장한다.
색인 검색: 위에서 만든 삭제 용어들을 이용해 색인을 검색한다. 정타 후보들이 존재하면 결과 셋에 저장한다.

위에서 만든 모든 후보들을 응답으로 돌려준다.
~~~

<br>

### 3. 실행 ###
---

프로젝트를 다운로드 받아 실행시킵니다.

빌드 후 jar파일을 실행시킬 때 첫 번째 파라미터로 영화 정보 파일의 위치를 주어야 합니다. 파일 path는 'src/main/resources/data/korean_movies.tsv' 입니다.

실행 후 웹 브라우저를 열고 주소창에 아래 URL을 입력해보세요.

~~~ txt
localhost:8080/search?query=간상
~~~

![결과](https://github.com/mirukman/spellcorrector/images/search_result.png){: width="80%" height="80%"}