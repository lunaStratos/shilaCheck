import requests
from bs4 import BeautifulSoup
import schedule
from email.mime.text import MIMEText
import smtplib, ssl

def checkGoods () :
    #신세계
    goodsCode1 = "103988000120" # 딥디크
    goodsCode2 = "103988000073"

    url = "https://www.ssgdfs.com/kr/goos/view/DIPTYQUE/beauty/perfume/"+goodsCode2
    response = requests.get(url)
    if response.status_code:
        html = response.text
        soup = BeautifulSoup(html, 'html.parser')
        # 재고 없음 버튼찾기
        btn = soup.select('.btnSSG.btnL')
        print("찾은 버튼 갯수" , btn)
        print(len(btn))

        if len(btn) == 0: #재고 없음이 풀림
            print("재고있음!")
            smtp_server = "smtp.naver.com"
            port = 587
            EMAIL_ADDR = ""
            EMAIL_PASSWORD = ""
            EMAIL_TO = "lunastratos@gmail.com"
            message = "ss"

            smtp = smtplib.SMTP(smtp_server, 587)
            smtp.starttls()
            smtp.ehlo()  # say Hello
            # smtp.starttls()  # TLS 사용시 필요
            smtp.login(EMAIL_ADDR, EMAIL_PASSWORD)

            msg = MIMEText('본문 테스트 메시지')
            msg['From'] = EMAIL_ADDR
            msg['To'] = EMAIL_ADDR
            msg['Subject'] = '플레르 드 뽀 입고 '
            smtp.sendmail(EMAIL_ADDR, EMAIL_TO, msg.as_string())

print("실행되었습니다.")

checkGoods()
schedule.every(5).minutes.do(checkGoods)

while True:
    schedule.run_pending()
