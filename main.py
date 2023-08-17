import requests
from bs4 import BeautifulSoup
import schedule
from email.mime.text import MIMEText
import smtplib, ssl

def checkGoods () :
    goodsCode1 = "4999357"
    goodsCode2 = "5040720" #딥디크

    url = "https://www.shilladfs.com/estore/kr/ko/p/"+goodsCode2+"?isSavedId=true#url";

    response = requests.get(url)
    if response.status_code:
        html = response.text
        soup = BeautifulSoup(html, 'html.parser')

        # 재고 없음 버튼찾기 
        btn = soup.select('div.pro_detail > div.pro_top_btnarea > div.btn_wrap > button.btn_grade1.type3')
        print("찾은 버튼 갯수")
        print(len(btn))

        if len(btn) == 0: #재고 없음이 풀림
            smtp_server = "smtp.naver.com"
            port = 587
            EMAIL_ADDR = "네이버메일"
            EMAIL_PASSWORD = "네이버메일암호"
            EMAIL_TO = "메시지보낼이메일"
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
# schedule.every(30).minutes.do(checkGoods)
schedule.every(5).minutes.do(checkGoods)

while True:
    schedule.run_pending()
