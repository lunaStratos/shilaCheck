package com.lunastratos.checkwebsite

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import org.jsoup.Jsoup
import org.w3c.dom.Text
import java.util.Properties
import java.util.Timer
import java.util.TimerTask
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


class MainActivity : AppCompatActivity() {

    lateinit var insertBtn:Button
    lateinit var okBtn:Button
    lateinit var hrefInput:EditText
    lateinit var intervalInput:EditText
    lateinit var webView: WebView
    lateinit var titleText: TextView
    lateinit var source: String
    lateinit var context: Context

    lateinit var profileAdapter: ProfileAdapter
    lateinit var rv_profile: RecyclerView
    val datas = mutableListOf<ProfileData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //버튼과 input 연결
        insertBtn = findViewById(R.id.insertBtn)
        okBtn = findViewById(R.id.okBtn)
        hrefInput = findViewById<EditText>(R.id.hrefInput)
        intervalInput = findViewById<EditText>(R.id.intervalInput)
        rv_profile = findViewById(R.id.rv_profile)
        webView = findViewById(R.id.webView)
        titleText = findViewById<TextView>(R.id.titleText)

        class MyJavascriptInterface(val mContext:Context) {

            @JavascriptInterface
            fun getHtml(html: String) {
                //위 자바스크립트가 호출되면 여기로 html이 반환됨
                var source = html
                //Log.e("html: ",source)
                val doc = Jsoup.parse(source)
                val noHave = doc.select("#prod_reapply").size.toInt()
                val yesHave = doc.select("#prod_cart").size.toInt()
                val title = doc.select("#productNameByDP").text()
                Log.d("result: ", "title= ${noHave}")
                Log.d("result: ", "title= ${yesHave}")
                Log.d("result: ", "title= ${title}")

                titleText.text = title
                Toast.makeText(mContext, "조회중입니다. \n 재고없음버튼: ${noHave} / 구매하기버튼: ${yesHave}", Toast.LENGTH_SHORT).show()
                sendEmail(noHave, yesHave, title)
            }

            fun sendEmail(noHave:Int, yesHave:Int, title:String) {

                // 보내는 메일 주소와 비밀번호
                val username = ""; // From ID
                val password = ""; // From ID의 암호
                val title: String = "상품입고::"+title     // 메일 제목
                val body: String = title + " 상품입고 완료"       // 메일 내용
                val dest: String  = "lunastratos@gmail.com"   // 받는 메일 주소

                if(noHave == 0){
                    val props = Properties();
                    props.put("mail.smtp.auth", "true");
                    props.put("mail.smtp.starttls.enable", "true");
                    props.put("mail.smtp.host", "smtp.naver.com"); // 네이버로 설정, Gmail은 보안이 까다로움
                    props.put("mail.smtp.port", "587");

                    // 비밀번호 인증으로 세션 생성
                    val session = Session.getInstance(props,
                        object: javax.mail.Authenticator() {
                            override  fun getPasswordAuthentication(): PasswordAuthentication {
                                return PasswordAuthentication(username, password);
                            }
                        })

                    // 메시지 객체 만들기
                    val message = MimeMessage(session)
                    message.setFrom(InternetAddress(username))
                    // 수신자 설정, 여러명으로도 가능
                    message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(dest))
                    message.subject = title
                    message.setText(body)

                    // 전송
                    Transport.send(message)
                }else{
                    Log.i("상품이 없습니다.", "")
                }


            }

        }

        webView.settings.javaScriptEnabled = true
        webView.addJavascriptInterface(MyJavascriptInterface(this), "Android")
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                view!!.loadUrl("javascript:window.Android.getHtml(document.getElementsByTagName('body')[0].innerHTML);")
            }
        }

        //링크와 select 저장하기
        insertBtn.setOnClickListener {

            val href:String = hrefInput.text.toString()
            if(datas.size != 0){
                datas.removeAt(0)
            }else{
                Toast.makeText(this, "추가되었습니다.", Toast.LENGTH_SHORT).show()
            }
            datas.apply {
                add(ProfileData(img = R.drawable.icon, name = "아이템", link = href, select = ""))
                profileAdapter.datas = datas
                profileAdapter.notifyDataSetChanged()
            }
        }

        val timer = Timer()
        val TT: TimerTask = object : TimerTask() {
            override fun run() {
                /**
                 * 플레르드뽀: 5040720
                 * 상품재고있는거: 4721725
                 * */
                webView.post(Runnable {
                    webView.loadUrl("https://m.shilladfs.com/estore/kr/ko/p/${datas.get(0).link}")
                })
            }
        }

        //https://www.shilladfs.com/estore/kr/ko/p/5040720?isSavedId=true
        var btnClick :Boolean = false;
        okBtn.setOnClickListener {

            if(btnClick){
                btnClick = false
                okBtn.text = "실행하기"
                timer.cancel();//타이머 종료
            }else{

                btnClick = true
                okBtn.text = "실행중"

                val time :Long= 1000*60*(intervalInput.text.toString().toLong())
                Toast.makeText(this, "${time/60000} 분마다 체크합니다.", Toast.LENGTH_SHORT).show()
                timer.schedule(TT, 0, time) //Timer 실행
            }
        }

        //리사이클뷰 실행
        initRecycler()
    }

    //리사이클 뷰 연결
    private fun initRecycler() {
        profileAdapter = ProfileAdapter(this)
        rv_profile.adapter = profileAdapter
    }

}
