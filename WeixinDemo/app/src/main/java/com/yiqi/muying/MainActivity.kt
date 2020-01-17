package com.yiqi.muying

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.yiqi.muying.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var api:IWXAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.e("","进入 MainActivity")
        registToWx()
        bindEvent()
    }

    private fun bindEvent() {
        btnWxLogin.onClick{
            api.apply {
                if (isWXAppInstalled){
                    sendReq(SendAuth.Req().run {
                        //官方说明：用于保持请求和回调的状态，授权请求后原样带回给第三方。该参数可用于防止csrf攻击（跨站请求伪造攻击），
                        // 建议第三方带上该参数，可设置为简单的随机数加session进行校验
                        scope = "snsapi_userinfo"
                        state = "wechat_sdk_gg_login_state"
                        this
                    })
                }else{
                    toast("您的设备未安装微信客户端！")
                }
            }
        }
    }

    private fun registToWx() {
        api = WXAPIFactory.createWXAPI(this, Configs.APP_ID,true).run {
            registerApp(Configs.APP_ID)
            this
        }
    }

    override fun onResume() {
        super.onResume()
        getStringSp("responseInfo").apply {
            if (this.isNotEmpty()){
                Log.e("",this)
            }
        }
    }
}
