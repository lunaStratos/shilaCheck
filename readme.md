# 신라면세점 상품체크

신라면세점의 경우 상품체크가 특정 시간에 Batch가 돌아가면서 푸시알림 or sms를 보내는 구조로 추측

때문에 푸시알림 전에 이미 입고 된 경우도 있고, 타인의 취소로 인해서 입고된 경우 신라면세점 알림 서비스 만으로는 알기 어려움

때문에 버튼형태의 id와 class값을 이용해서 이를체크, 메일 알림으로 처리함

테스트 상품은 플레르 드 뽀이며 상품의 goods id값을 넣어서 체크

shell:startup에 넣어서 시작프로그램으로 등록해서 사용하면 메일이 올라옴 


# 결과

![샘플이미지](./img/img1.jpg)

실제 prod상에서 정상적으로 메일이 와서 구입을 할수 있었다. 

# 실행파일만들기?

 pip install pyinstaller 로 import한후  pyinstaller main.py 을 하면 된다.
 
 dist 폴더에 cmd 실행파일이 생긴다.

 
# 안드로이드 버전 